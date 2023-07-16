package infinuma.android.shows.model

import androidx.annotation.DrawableRes

data class Review(
    val author: String,
    val review: Int,
    val description: String,
    @DrawableRes val imageResourceId: Int,

    )
