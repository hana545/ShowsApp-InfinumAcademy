package infinuma.android.shows.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import infinuma.android.shows.Constants
import infinuma.android.shows.model.SignInRequest
import infinuma.android.shows.networking.ApiModule
import kotlinx.coroutines.launch
import okhttp3.Headers
import org.json.JSONObject

class LoginViewModel  : ViewModel() {

    private val _loginResult: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val loginResult: LiveData<Boolean> = _loginResult

    private val _loginErrorResult: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val loginErrorResult: LiveData<String> = _loginErrorResult

    private val _loginAuthData: HashMap<String, String> by lazy { HashMap<String, String>() }
    val loginAuthData: HashMap<String, String> = _loginAuthData

    var imageUri = ""

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
        val response = ApiModule.retrofit.signIn(
            request =
            SignInRequest(email = username, password = password)
        )
        if(response.isSuccessful) {
            imageUri = response.body()?.user!!.imageUrl.toString()
            createHeader(response.headers())
            _loginResult.value = true
        } else {
            //throw IllegalStateException()
            _loginErrorResult.value = response.errorBody()?.string()?.let {
                JSONObject(it).getJSONArray("errors").get(0).toString()
            } ?: run {
                response.code().toString()
            }
            _loginResult.value = false
        }
    }
    private fun createHeader(header : Headers) {
        _loginAuthData[Constants.headerAuthAccToken] = header[Constants.headerAuthAccToken].toString()
        _loginAuthData[Constants.headerAuthClient] = header[Constants.headerAuthClient].toString()
        _loginAuthData[Constants.headerAuthUid] = header[Constants.headerAuthUid].toString()
    }

}