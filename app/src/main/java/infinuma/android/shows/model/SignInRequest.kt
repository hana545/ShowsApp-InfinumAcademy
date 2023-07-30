package infinuma.android.shows.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SignInRequest(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
)