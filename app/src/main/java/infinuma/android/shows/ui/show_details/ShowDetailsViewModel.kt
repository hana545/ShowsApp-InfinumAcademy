package infinuma.android.shows.ui.show_details

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import infinuma.android.shows.db.ShowsDatabase
import infinuma.android.shows.model.Review
import infinuma.android.shows.model.ReviewRequest
import infinuma.android.shows.model.Show
import infinuma.android.shows.model.User
import infinuma.android.shows.networking.ApiModule
import kotlinx.coroutines.launch

class ShowDetailsViewModel(private val database: ShowsDatabase) : ViewModel() {

    private val _showLiveData = MutableLiveData<Show?>()
    val showLiveData: LiveData<Show?> = _showLiveData

    private val _reviewsLiveData = MediatorLiveData<MutableList<Review>>()
    val reviewsLiveData: LiveData<MutableList<Review>> = _reviewsLiveData

    private val _postReviewResult = MutableLiveData<Boolean>()
    val postReviewResult: LiveData<Boolean> = _postReviewResult

    fun getShow(id : String, isConnected : Boolean) =
        viewModelScope.launch {
            try {
                if(isConnected) {
                    getShowByAPI(id)
                } else {
                    getShowByDB(id)
                }
            } catch (ups: Exception) {
                Log.e("SHOWDETAILLIST", "getShow ups "+ups.toString())
            }
        }

    private suspend fun getShowByAPI(id : String) {
        val response = ApiModule.retrofit.getShowByID(id)
        if (response.isSuccessful) {
            _showLiveData.value = response.body()?.show

        }
    }
    private suspend fun getShowByDB(id : String) : Show {
        val show = database.showDao().getShow(id)
        _showLiveData.value = show.toShow()
        return show.toShow()

    }

    fun getShowReviews(id : String) =
        viewModelScope.launch {
            try {
                getReviewsByAPI(id)
            } catch (ups: Exception) {
                Log.e("SHOWDETAILLIST", "getReview ups "+ups.toString())
            }
        }

    private suspend fun getReviewsByAPI(id : String) {
        val response = ApiModule.retrofit.listReviews(id)
        if (response.isSuccessful) {
            _reviewsLiveData.value = response.body()?.reviews
            insertReviewsInDB(response.body()?.reviews!!, id)
        }
    }

    private fun insertReviewsInDB(reviews: MutableList<Review>, showId: String) {
        val tmpReviewEntityList = reviews.map { it.toEntity(showId) } ?: emptyList()
        viewModelScope.launch {
                database.reviewDao().insertAllReviews(tmpReviewEntityList)
        }

    }
    fun getReviewsByDB(showId: String) : LiveData<MutableList<Review>> {
        return database.reviewDao().getAllReviews(showId).map { listOfReviewEntities ->
            return@map listOfReviewEntities.map { reviewEntity ->
                Review(
                    id = reviewEntity.id,
                    user = User(reviewEntity.userId, reviewEntity.userName, reviewEntity.userImageUrl),
                    rating = reviewEntity.rating,
                    comment = reviewEntity.comment
                )
            }.toMutableList()
        }
    }

    fun addReview(comment : String, rating : Int, showId : String) =
        viewModelScope.launch {
            try {
                postReview(ReviewRequest(rating,comment, showId))
            } catch (ups: Exception) {
                Log.e("SHOWDETAILLIST", "ups "+ups.toString())
                _postReviewResult.value = false
            }
        }

    private suspend fun postReview(request : ReviewRequest) {
        val response = ApiModule.retrofit.postReview(request)
        if (response.isSuccessful) {
            getShowReviews(request.showId)
            getShow(request.showId, true)
            //updateShow(request.showId)
            _postReviewResult.value = true
        } else {
            _postReviewResult.value = false
        }
    }

    private suspend fun updateShow(showId: String) {
        database.showDao().updateShow(getShowByDB(showId).toEntity())
    }

}