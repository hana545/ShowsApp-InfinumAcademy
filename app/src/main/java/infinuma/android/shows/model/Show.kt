package infinuma.android.shows.model

import infinuma.android.shows.db.ShowEntity
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Show(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("image_url") val imageUrl: String,
    @SerialName("average_rating") var averageRating: Float?,
    @SerialName("no_of_reviews") var numReviews: Int?
) {
    fun toEntity(): ShowEntity {
        return ShowEntity(this.id, this.title, this.description, this.imageUrl, this.averageRating, this.numReviews)
    }
}

@kotlinx.serialization.Serializable
data class ListShowsResponse(
    @SerialName("shows") val shows: MutableList<Show>
)

@kotlinx.serialization.Serializable
data class ListShowResponse(
    @SerialName("show") val show: Show
)

