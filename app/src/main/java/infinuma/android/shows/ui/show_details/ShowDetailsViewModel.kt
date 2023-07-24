package infinuma.android.shows.ui.show_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import infinuma.android.shows.R
import infinuma.android.shows.model.Review
import infinuma.android.shows.model.Show

class ShowDetailsViewModel : ViewModel() {

    private val _show = MutableLiveData<Show?>()
    val show: LiveData<Show?> get() = _show

    fun setShow(show: Show) {
        _show.value = show
    }

    fun addReview(rating: Int, text: String) {
        val currentShow = _show.value
        if (currentShow != null) {
            currentShow.reviews.add(
                Review(
                    "dummy_user",
                    rating,
                    text,
                    R.drawable.ic_profile_placeholder
                )
            )
            _show.value = currentShow
        }
    }
    fun getAverageReview(): Float {
        val currentShow = _show.value
        return currentShow?.reviews?.sumOf { it.review }?.toFloat() ?: (0f / currentShow?.reviews?.size?.toFloat()!!) ?: 1f
    }


}