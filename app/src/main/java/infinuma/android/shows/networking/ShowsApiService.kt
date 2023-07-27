package infinuma.android.shows.networking

import infinuma.android.shows.model.RegisterRequest
import infinuma.android.shows.model.RegisterResponse
import infinuma.android.shows.model.SignInRequest
import infinuma.android.shows.model.SignInResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ShowsApiService {

    @POST("/users")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("/users/sign_in")
    suspend fun signIn(@Body request: SignInRequest): Response<SignInResponse>
}