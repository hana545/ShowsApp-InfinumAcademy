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
import infinuma.android.shows.model.Show
import infinuma.android.shows.networking.ApiModule
import kotlinx.coroutines.launch

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
        val response = ApiModule.retrofit.listShows(createHeader())
            if (response.isSuccessful) {
                _listShowsLiveData.value = response.body()?.shows
            }
        }

    private fun createHeader(): HashMap<String, String> {
        val headers = HashMap<String, String>()
        headers[Constants.headerAuthAccToken] = sharPreferences.getString(Constants.keyAuthAccToken, "")!!
        headers[Constants.headerAuthClient] = sharPreferences.getString(Constants.keyAuthClient, "")!!
        headers[Constants.headerAuthTokenType] = Constants.headerAuthTokenType
        headers[Constants.headerAuthExpiry] = sharPreferences.getString(Constants.keyAuthExpiry, "")!!
        headers[Constants.headerAuthUid] = sharPreferences.getString(Constants.keyAuthUid, "")!!
        headers[Constants.headerAuthContent] = sharPreferences.getString(Constants.keyAuthContent, "")!!
        return headers
    }
}