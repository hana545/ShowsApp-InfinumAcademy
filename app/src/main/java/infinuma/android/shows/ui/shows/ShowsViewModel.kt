package infinuma.android.shows.ui.shows

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import infinuma.android.shows.Constants
import infinuma.android.shows.model.RegisterRequest
import infinuma.android.shows.model.RegisterResponse
import infinuma.android.shows.model.Show
import infinuma.android.shows.networking.ApiModule
import java.io.File
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject

class ShowsViewModel(application: Application) : AndroidViewModel(application) {

    private val sharPreferences = application.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
    private val _listShowsLiveData = MutableLiveData<MutableList<Show>>()
    val listShowsLiveData: LiveData<MutableList<Show>> get() = _listShowsLiveData

    private val _currentPhotoUriLiveData = MutableLiveData<Uri>()
    val currentPhotoUriLiveData: LiveData<Uri> get() = _currentPhotoUriLiveData

    private val _profilePhotoUriLiveData = MutableLiveData<Uri>()
    val profilePhotoUriLiveData: LiveData<Uri> get() = _profilePhotoUriLiveData


    init {
        _listShowsLiveData.value = mutableListOf()
        _profilePhotoUriLiveData.value = sharPreferences.getString(Constants.keyImageUri, "")?.toUri()
        _currentPhotoUriLiveData.value = Uri.EMPTY
    }

    fun removeShowsList() {
        _listShowsLiveData.value?.clear()
        _listShowsLiveData.value = _listShowsLiveData.value
    }

    fun addShowsList() {
        getShows()

    }
    fun getShowList(): LiveData<MutableList<Show>> {
        getShows()
        return listShowsLiveData
    }

    fun clearSharedPreferences() {
        sharPreferences.edit {
            putString(Constants.keyEmail, "")
            putString(Constants.keyPassword, "")
            putBoolean(Constants.keyLogedIn, false)
            putString(Constants.keyImageUri, "")
        }
    }

    fun setCurrentPhotoUri(uri: Uri) {
        _currentPhotoUriLiveData.value = uri
    }

    fun setProfilePhotoUri(uri: Uri) {
        _profilePhotoUriLiveData.value = uri
        sharPreferences.edit {
            putString(Constants.keyImageUri, uri.toString())
        }
    }

    fun getShows() =
        viewModelScope.launch {
            try {
                listShows()
            } catch (ups: Exception) {
                Log.e("SHOWLIST", "ups "+ups.toString())
            }
        }

    private suspend fun listShows() {
        val response = ApiModule.retrofit.listShows()
            if (response.isSuccessful) {
                _listShowsLiveData.value = response.body()?.shows
            }
        }

    fun uploadPhoto(file: File) =
        viewModelScope.launch {
            try {
                putPhoto(file)
            } catch (ups: Exception) {
                Log.e("UPLOADFILE", "ups "+ups.toString())
            }
        }

    private suspend fun putPhoto(file : File) {

        val filePart = MultipartBody.Part.createFormData("image", file.name, RequestBody.create("image/*".toMediaTypeOrNull(), file))
        val response = ApiModule.retrofit.updateUser(filePart)
        if (response.isSuccessful) {
            _profilePhotoUriLiveData.value = response.body()?.user!!.imageUrl?.toUri()
            sharPreferences.edit {
                putString(Constants.keyImageUri, response.body()?.user!!.imageUrl)
            }
        } else {
            Log.i("UPLOADFILE", response.errorBody()?.string()?.let {
                JSONObject(it).getJSONArray("errors").toString() // or whatever your message is
            } ?: run {
                response.code().toString()
            })
        }
    }

}