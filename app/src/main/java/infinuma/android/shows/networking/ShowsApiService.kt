package infinuma.android.shows.networking

import infinuma.android.shows.model.ListReviewResponse
import infinuma.android.shows.model.ListReviewsResponse
import infinuma.android.shows.model.ListShowResponse
import infinuma.android.shows.model.ListShowsResponse
import infinuma.android.shows.model.RegisterRequest
import infinuma.android.shows.model.RegisterResponse
import infinuma.android.shows.model.ReviewRequest
import infinuma.android.shows.model.SignInRequest
import infinuma.android.shows.model.SignInResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ShowsApiService {

    @POST("/users")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("/users/sign_in")
    suspend fun signIn(@Body request: SignInRequest): Response<SignInResponse>

    @Multipart
    @PUT("/users")
    suspend fun updateUser(@Part file : MultipartBody.Part): Response<RegisterResponse>

    @GET("/shows")
    suspend fun listShows(): Response<ListShowsResponse>

    @GET("/shows/{id}")
    suspend fun getShowByID(@Path("id") key: String): Response<ListShowResponse>

    @GET("/shows/{id}/reviews")
    suspend fun listReviews(@Path("id") key: String): Response<ListReviewsResponse>

    @POST("/reviews")
    suspend fun postReview(@Body request: ReviewRequest): Response<ListReviewResponse>

}