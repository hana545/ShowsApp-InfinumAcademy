package infinuma.android.shows.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SignInResponse(
    @SerialName("user") val user: User
)
