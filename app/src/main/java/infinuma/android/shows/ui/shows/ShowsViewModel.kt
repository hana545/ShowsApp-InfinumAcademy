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
import androidx.lifecycle.ViewModel
import infinuma.android.shows.Constants
import infinuma.android.shows.R
import infinuma.android.shows.model.Show

class ShowsViewModel(application: Application) : AndroidViewModel(application) {

    private val sharPreferences = application.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
    private val _listShows = MutableLiveData<MutableList<Show>>()
    val listShows: LiveData<MutableList<Show>> get() = _listShows

    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> get() = _userEmail

    private val _userLogedIn = MutableLiveData<Boolean>()
    val userLogedIn: LiveData<Boolean> get() = _userLogedIn

    private val _currentPhotoUri = MutableLiveData<Uri>()
    val currentPhotoUri: LiveData<Uri> get() = _currentPhotoUri

    private val _profilePhotoUri = MutableLiveData<Uri>()
    val profilePhotoUri: LiveData<Uri> get() = _profilePhotoUri


    init {
        _listShows.value = setShowList()
        _userEmail.value = sharPreferences.getString(Constants.keyEmail, "")
        _userLogedIn.value = sharPreferences.getBoolean(Constants.keyLogedIn, false)
        _profilePhotoUri.value = sharPreferences.getString(Constants.keyImageUri, "")?.toUri()
        _currentPhotoUri.value = Uri.EMPTY
    }

    fun removeShowsList() {
        _listShows.value?.clear()
        _listShows.value = _listShows.value
    }

    fun addShowsList() {
        _listShows.value?.addAll(setShowList())
        _listShows.value = _listShows.value
    }

    fun updateUserInfo(email: String, password : String, remember : Boolean) {
        sharPreferences.edit {
            putString(Constants.keyEmail, email)
            putString(Constants.keyPassword, password)
            putBoolean(Constants.keyLogedIn, remember)
        }
        _userEmail.value = email
        _userLogedIn.value = remember
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
        _currentPhotoUri.value = uri
    }

    fun setProfilePhotoUri(uri: Uri) {
        _profilePhotoUri.value = uri
        sharPreferences.edit {
            putString(Constants.keyImageUri, uri.toString())
        }
    }

    private fun setShowList(): MutableList<Show> {
        var autoincrement = 0
        val description =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
        return mutableListOf(
            Show("SH-${autoincrement++}", "The Office", description, "Sitcom", R.drawable.the_office, mutableListOf()),
            Show("SH-${autoincrement++}", "Stranger Things", description, "Science fiction", R.drawable.stranger_things, mutableListOf()),
            Show("SH-${autoincrement++}", "Grey's Anatomy", description, "Medical drama", R.drawable.greys_anatomy, mutableListOf()),
            Show("SH-${autoincrement++}", "Supernatural", description, "Fantasy drama", R.drawable.supernatural, mutableListOf()),
            Show("SH-${autoincrement++}", "Parks and Recreation", description, "Sitcom", R.drawable.parks_and_recreation, mutableListOf()),
            Show("SH-${autoincrement++}", "Breaking Bad", description, "Crime drama", R.drawable.breaking_bad, mutableListOf()),
            Show("SH-${autoincrement++}", "Friends", description, "Sitcom", R.drawable.friends, mutableListOf())
        )
    }


}