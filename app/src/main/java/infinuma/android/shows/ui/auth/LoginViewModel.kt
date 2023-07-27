package infinuma.android.shows.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.util.Util
import infinuma.android.shows.model.HeaderResponse
import infinuma.android.shows.model.SignInRequest
import infinuma.android.shows.networking.ApiModule
import kotlinx.coroutines.launch
import org.json.JSONObject

class LoginViewModel  : ViewModel() {

    private val _loginResult: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val loginResult: LiveData<Boolean> = _loginResult

    private val _loginErrorResult: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val loginErrorResult: LiveData<String> = _loginErrorResult

    private val _loginToken: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val loginToken: LiveData<String> = _loginToken

    fun onLoginButtonClicked(username: String, password: String) =
        viewModelScope.launch {
            try {
                signInUser(username, password)
            } catch (ups: Exception) {
                Log.e("LOGIN", "ups "+ups.toString())
                _loginResult.value = false
                _loginErrorResult.value = ups.message
            }
        }

    private suspend fun signInUser(username: String, password: String) {
        Log.i("LOGIN", "start api call")
        val response = ApiModule.retrofit.signIn(
            request =
            SignInRequest(email = username, password = password)
        )
        if(response.isSuccessful) {
            //Save token
            _loginToken.value =  response.headers().get("access-token").toString()
            Log.i("LOGIN", "tokken:"+loginToken.value )
            _loginResult.value = true
        } else {
            //throw IllegalStateException()
            _loginErrorResult.value = response.errorBody()?.string()?.let {
                JSONObject(it).getJSONArray("errors").get(0).toString() // or whatever your message is
            } ?: run {
                response.code().toString()
            }
            _loginResult.value = false
        }
    }

}