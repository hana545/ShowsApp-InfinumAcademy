package infinuma.android.shows.networking

import android.content.Context
import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.Request
import infinuma.android.shows.Constants as CVariable

class HeaderInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            CVariable.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        // Retrieve the header values from Shared Preferences

        val userAccessToken = sharedPreferences.getString(CVariable.keyAuthAccToken, "")!!
        val userAccessClient = sharedPreferences.getString(CVariable.keyAuthClient, "")!!
        val userAccessUid = sharedPreferences.getString(CVariable.keyAuthUid,"")!!

        val request: Request = chain.request().newBuilder()
            .header("access-token", userAccessToken)
            .header("uid", userAccessUid)
            .header("client", userAccessClient)
            .build()

        return chain.proceed(request)
    }
}