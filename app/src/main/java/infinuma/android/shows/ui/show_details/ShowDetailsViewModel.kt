package infinuma.android.shows.ui.show_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import infinuma.android.shows.R
import infinuma.android.shows.model.Review
import infinuma.android.shows.model.Show

class ShowDetailsViewModel : ViewModel() {

    private val _showLiveData = MutableLiveData<Show?>()
    val showLiveData: LiveData<Show?> = _showLiveData

    fun setShow(show: Show) {
        _showLiveData.value = show
    }

    fun addReview(rating: Int, text: String) {
        val currentShow = _showLiveData.value
        if (currentShow != null) {
            currentShow.reviews.add(
                Review(
                    "dummy_user",
                    rating,
                    text,
                    R.drawable.ic_profile_placeholder,
                )
            )
            currentShow.avgReview = currentShow.reviews.sumOf { it.review }.toFloat() / currentShow.reviews.size.toFloat()
            _showLiveData.value = currentShow
        }
    }

}