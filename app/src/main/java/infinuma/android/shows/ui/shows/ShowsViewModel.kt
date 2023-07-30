package infinuma.android.shows.ui.shows

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.room.ColumnInfo
import infinuma.android.shows.Constants
import infinuma.android.shows.db.ShowEntity
import infinuma.android.shows.model.Show
import infinuma.android.shows.networking.ApiModule
import infinuma.android.shows.db.ShowsDatabase
import java.io.File
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject

class ShowsViewModel(application: Application,  private val database: ShowsDatabase) : AndroidViewModel(application) {

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
                val tmpShowEntityList = response.body()?.shows?.map { it.toEntity() } ?: emptyList()
                GlobalScope.launch {
                    if (!showsLiveData.value.isNullOrEmpty()) {
                        if (showsLiveData.value?.size == 0) {
                            tmpShowEntityList.let { database.showDao().insertAllShows(it) }
                        } else {
                            for (showEntity in tmpShowEntityList) {
                                // Check if the showEntity already exists in the database
                                val existingShow = database.showDao().getShow(showEntity.id)

                                if (existingShow == null) {
                                    // Insert the showEntity into the database if it doesn't exist
                                    database.showDao().insert(showEntity)
                                }
                            }
                        }
                    } else {
                        database.showDao().insertAllShows(tmpShowEntityList)
                    }
                }
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

    val showsLiveData: LiveData<MutableList<Show>> =
        database.showDao().getAllShows().map { listOfShowEntities ->
            return@map listOfShowEntities.map { showEntity ->
                Show(
                    id = showEntity.id,
                    title = showEntity.title,
                    description = showEntity.description,
                    imageUrl = showEntity.imageUrl,
                    averageRating = showEntity.avgRating,
                    numReviews = showEntity.numReviews
                )
            } as @JvmSuppressWildcards MutableList<Show>

        }

    fun Show.toEntity(): ShowEntity {
        return ShowEntity(this.id, this.title, this.description, this.imageUrl, this.averageRating, this.numReviews)
    }
}