package infinuma.android.shows

import androidx.core.content.FileProvider

object Constants {
    val SHARED_PREFERENCES = "USER_INFO"
    val keyEmail = "USER_EMAIL"
    val keyPassword = "USER_PASSWORD"
    val keyLogedIn = "LOGED_IN"
    val keyImageUri = "USER_IMAGE_URI"

    val FileProviderAuthority = "infinuma.android.shows.fileprovider"

    val PERMISSION_REQUEST_CODE = 123
    val CAMERA_REQUEST_CODE = 124
    val IMAGE_PICKER_REQUEST_CODE = 142

}