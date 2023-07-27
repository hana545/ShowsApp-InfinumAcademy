package infinuma.android.shows.model

import androidx.annotation.DrawableRes
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Review(
    val id: String,
    val user: User,
    val rating: Int,
    val comment: String,
)
@kotlinx.serialization.Serializable
data class ListReviewsResponse(
    @SerialName("reviews") val reviews: MutableList<Review>
)
@kotlinx.serialization.Serializable
data class ListReviewResponse(
    @SerialName("review") val review: Review
)

@kotlinx.serialization.Serializable
data class ReviewRequest(
    @SerialName("rating") val rating: Int,
    @SerialName("comment") val comment: String,
    @SerialName("show_id") val show_id: String
)