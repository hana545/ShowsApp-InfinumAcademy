package infinuma.android.shows.model

import infinuma.android.shows.db.ReviewEntity
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Review(
    @SerialName("id") val id: String,
    @SerialName("user") val user: User,
    @SerialName("rating") val rating: Int,
    @SerialName("comment") val comment: String,
) {
    fun toEntity(showId: String): ReviewEntity {
        return ReviewEntity(this.id, this.user.id, this.user.email, this.rating, this.comment, showId, this.user.imageUrl)
    }
}
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