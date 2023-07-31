package infinuma.android.shows.ui.show_details

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import infinuma.android.shows.Constants
import infinuma.android.shows.db.ReviewEntity
import infinuma.android.shows.db.ShowEntity
import infinuma.android.shows.db.ShowsDatabase
import infinuma.android.shows.model.RegisterResponse
import infinuma.android.shows.model.Review
import infinuma.android.shows.model.ReviewRequest
import infinuma.android.shows.model.Show
import infinuma.android.shows.model.User
import infinuma.android.shows.networking.ApiModule
import infinuma.android.shows.networking.NetworkUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.internal.notifyAll

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
                Log.e("SHOWDETAILLIST", "ups "+ups.toString())
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
                Log.e("SHOWDETAILLIST", "ups "+ups.toString())
            }
        }

    private suspend fun getReviewsByAPI(id : String) {
        val response = ApiModule.retrofit.listReviews(id)
        if (response.isSuccessful) {
            _reviewsLiveData.value = response.body()?.reviews
            insertReviewsInDB(response.body()?.reviews!!, id)
        } else {

        }
    }

    private fun insertReviewsInDB(reviews: MutableList<Review>, showId: String) {
        val tmpReviewEntityList = reviews.map { it.toEntity(showId) } ?: emptyList()
        GlobalScope.launch {
                database.reviewDao().insertAllReviews(tmpReviewEntityList)
        }

    }
    fun getReviewsByDB(showId: String) : LiveData<MutableList<Review>> {
        return database.reviewDao().getAllReviews(showId).map { listOfReviewEntities ->
            return@map listOfReviewEntities.map { reviewEntity ->
                Review(
                    id = reviewEntity.id,
                    user = User(reviewEntity.userId, reviewEntity.userName, ""),
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

    private fun ShowEntity.toShow(): Show {
        return Show(this.id, this.title, this.description, this.imageUrl, this.avgRating, this.numReviews)
    }

    private fun Show.toEntity(): ShowEntity {
        return ShowEntity(this.id, this.title, this.description, this.imageUrl, this.averageRating, this.numReviews)
    }

    private fun Review.toEntity(showId: String): ReviewEntity {
        return ReviewEntity(this.id, this.user.id, this.user.email, this.rating, this.comment, showId)
    }

}