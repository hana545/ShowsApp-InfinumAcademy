package infinuma.android.shows.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class HeaderResponse(
    @SerialName("access-token") val token: String,
    @SerialName("client") val client: String,
    @SerialName("token-type") val token_type: String,
    @SerialName("expiry") val expiry: String,
    @SerialName("uid") val uid: String,
    @SerialName("Content-Type") val content_type : String
)
