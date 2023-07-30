package infinuma.android.shows.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Review(
    @SerialName("id") val id: String,
    @SerialName("user") val user: User,
    @SerialName("rating") val rating: Int,
    @SerialName("comment") val comment: String,
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
    @SerialName("show_id") val showId: String
)