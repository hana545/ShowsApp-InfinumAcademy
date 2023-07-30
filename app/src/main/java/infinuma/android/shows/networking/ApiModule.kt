package infinuma.android.shows.networking

import android.content.Context
import android.content.SharedPreferences
import android.provider.SyncStateContract.Constants
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import infinuma.android.shows.Constants as CVariable

object ApiModule {
    private const val BASE_URL = "https://tv-shows.infinum.academy/"

    lateinit var retrofit: ShowsApiService

    private val json = Json { ignoreUnknownKeys = true }

    fun initRetrofit(context: Context) {
        val headerInterceptor = object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val sharedPreferences: SharedPreferences = context.getSharedPreferences(
                    CVariable.SHARED_PREFERENCES, Context.MODE_PRIVATE)
                // Retrieve the header values from Shared Preferences

                val userAccessToken = sharedPreferences.getString(CVariable.keyAuthAccToken, "")!!
                val userAccessClient = sharedPreferences.getString(CVariable.keyAuthClient, "")
                val userAccessExpiry = sharedPreferences.getString(CVariable.keyAuthExpiry, "")
                val userAccessUid = sharedPreferences.getString(CVariable.keyAuthUid,"")
                val userAccessContent = sharedPreferences.getString(CVariable.keyAuthContent, "")

                val request: Request = chain.request().newBuilder()
                    .header("access-token", userAccessToken!!)
                    .header("client", userAccessClient!!)
                    .header("toke-type", "Bearer")
                    .header("expiry", userAccessExpiry!!)
                    .header("uid", userAccessUid!!)
                    .header("Content-Type", userAccessContent!!)
                    .build()

                return chain.proceed(request)
            }
        }
        val okhttp = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(headerInterceptor)
            .addInterceptor(ChuckerInterceptor.Builder(context = context).build())
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(okhttp)
            .build()
            .create(ShowsApiService::class.java)
    }
}