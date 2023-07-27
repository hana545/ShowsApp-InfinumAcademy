package infinuma.android.shows.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class RegisterResponse(
    @SerialName("user") val user: User
)

@kotlinx.serialization.Serializable
data class User(
    @SerialName("id") val id: String,
    @SerialName("email") val email: String,
    @SerialName("image_url") val imageUrl: String?,
)