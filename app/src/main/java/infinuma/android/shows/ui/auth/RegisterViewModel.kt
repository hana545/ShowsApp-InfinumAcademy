package infinuma.android.shows.ui.auth

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import infinuma.android.shows.model.RegisterRequest
import infinuma.android.shows.networking.ApiModule
import java.security.AccessController.getContext
import kotlinx.coroutines.launch
import org.json.JSONObject

class RegisterViewModel  : ViewModel() {

    private val _registrationResult: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val registrationResult: LiveData<Boolean> = _registrationResult

    private val _registrationErrorResult: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val registrationErrorResult: LiveData<String> = _registrationErrorResult

    fun onRegisterButtonClicked(username: String, password: String) =
        viewModelScope.launch {
            try {
                registerUser(username, password)
            } catch (ups: Exception) {
                Log.e("REGISTER", ups.toString())
                _registrationResult.value = false
                _registrationErrorResult.value = ups.message
            }
        }

    private suspend fun registerUser(username: String, password: String) {
        val response = ApiModule.retrofit.register(
            request =
            RegisterRequest(email = username, password = password, passwordConfirmation = password)
        )
        Log.e("REGISTER", response.headers().toString())
        if(response.isSuccessful) {
            _registrationResult.value = true
        } else {
            //throw IllegalStateException()
            _registrationErrorResult.value = response.errorBody()?.string()?.let {
                JSONObject(it).getJSONArray("errors").get(0).toString() // or whatever your message is
            } ?: run {
                response.code().toString()
            }
            _registrationResult.value = false
        }
    }

}