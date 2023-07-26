package infinuma.android.shows.ui.shows

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import infinuma.android.shows.Constants
import infinuma.android.shows.R
import infinuma.android.shows.model.Show

class ShowsViewModel(application: Application) : AndroidViewModel(application) {

    private val sharPreferences = application.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
    private val _listShowsLiveData = MutableLiveData<MutableList<Show>>()
    val listShowsLiveData: LiveData<MutableList<Show>> get() = _listShowsLiveData

    private val _userEmailLiveData = MutableLiveData<String>()
    private val _userLogedInLiveData = MutableLiveData<Boolean>()

    private val _currentPhotoUriLiveData = MutableLiveData<Uri>()
    val currentPhotoUriLiveData: LiveData<Uri> get() = _currentPhotoUriLiveData

    private val _profilePhotoUriLiveData = MutableLiveData<Uri>()
    val profilePhotoUriLiveData: LiveData<Uri> get() = _profilePhotoUriLiveData


    init {
        _listShowsLiveData.value = setShowList()
        _userEmailLiveData.value = sharPreferences.getString(Constants.keyEmail, "")
        _userLogedInLiveData.value = sharPreferences.getBoolean(Constants.keyLogedIn, false)
        _profilePhotoUriLiveData.value = sharPreferences.getString(Constants.keyImageUri, "")?.toUri()
        _currentPhotoUriLiveData.value = Uri.EMPTY
    }

    fun removeShowsList() {
        _listShowsLiveData.value?.clear()
        _listShowsLiveData.value = _listShowsLiveData.value
    }

    fun addShowsList() {
        _listShowsLiveData.value?.addAll(setShowList())
        _listShowsLiveData.value = _listShowsLiveData.value
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