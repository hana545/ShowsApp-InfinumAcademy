package infinuma.android.shows.ui.show_details

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import infinuma.android.shows.Constants
import infinuma.android.shows.model.Review
import infinuma.android.shows.model.ReviewRequest
import infinuma.android.shows.model.Show
import infinuma.android.shows.networking.ApiModule
import kotlinx.coroutines.launch

class ShowDetailsViewModel : ViewModel() {

    private val _showLiveData = MutableLiveData<Show?>()
    val showLiveData: LiveData<Show?> = _showLiveData

    private val _reviewsLiveData = MutableLiveData<MutableList<Review>>()
    val reviewsLiveData: LiveData<MutableList<Review>> = _reviewsLiveData

    private val _postReviewResult = MutableLiveData<Boolean>()
    val postReviewResult: LiveData<Boolean> = _postReviewResult

    fun getShow(id : String) =
        viewModelScope.launch {
            try {
                getShowByID(id)
            } catch (ups: Exception) {
                Log.e("SHOWLIST", "ups "+ups.toString())
            }
        }

    private suspend fun getShowByID(id : String) {
        val response = ApiModule.retrofit.getShowByID(id)
        if (response.isSuccessful) {
            _showLiveData.value = response.body()?.show

        }
    }

    fun getReviewList(): LiveData<MutableList<Review>> {
        return reviewsLiveData
    }

    fun getShowReviews(id : String) =
        viewModelScope.launch {
            try {
                listReviews(id)
            } catch (ups: Exception) {
                Log.e("SHOWLIST", "ups "+ups.toString())
            }
        }

    private suspend fun listReviews(id : String) {
        val response = ApiModule.retrofit.listReviews(id)
        if (response.isSuccessful) {
            _reviewsLiveData.value = response.body()?.reviews
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
            getShow(request.showId)
            _postReviewResult.value = true
        } else {
            _postReviewResult.value = false
        }
    }
}