package infinuma.android.shows.ui.show_details

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import infinuma.android.shows.Constants
import infinuma.android.shows.model.Review
import infinuma.android.shows.model.ReviewRequest
import infinuma.android.shows.model.Show
import infinuma.android.shows.networking.ApiModule
import kotlinx.coroutines.launch

class ShowDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val sharPreferences = application.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)

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
        val response = ApiModule.retrofit.getShowByID(createHeader(), id)
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
        val response = ApiModule.retrofit.listReviews(createHeader(), id)
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
        val response = ApiModule.retrofit.postReview(createHeader(), request)
        if (response.isSuccessful) {
            getShowReviews(request.showId)
            getShow(request.showId)
            _postReviewResult.value = true
        } else {
            _postReviewResult.value = false
        }
    }

    private fun createHeader(): HashMap<String, String> {
        val headers = HashMap<String, String>()
        headers[Constants.headerAuthAccToken] = sharPreferences.getString(Constants.keyAuthAccToken, "")!!
        headers[Constants.headerAuthClient] = sharPreferences.getString(Constants.keyAuthClient, "")!!
        headers[Constants.headerAuthTokenType] = Constants.headerAuthTokenType
        headers[Constants.headerAuthExpiry] = sharPreferences.getString(Constants.keyAuthExpiry, "")!!
        headers[Constants.headerAuthUid] = sharPreferences.getString(Constants.keyAuthUid, "")!!
        headers[Constants.headerAuthContent] = sharPreferences.getString(Constants.keyAuthContent, "")!!
        return headers
    }

}