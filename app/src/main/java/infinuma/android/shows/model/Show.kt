package infinuma.android.shows.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Show(
    val id: String,
    val title: String,
    val description: String,
    val image_url: String,
    var average_rating: Float?,
    var no_of_reviews: Int?
)

@kotlinx.serialization.Serializable
data class ListShowsResponse(
    @SerialName("shows") val shows: MutableList<Show>
)

@kotlinx.serialization.Serializable
data class ListShowResponse(
    @SerialName("show") val show: Show
)

