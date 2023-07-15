package infinuma.android.shows.model

import androidx.annotation.DrawableRes

data class Show(val ID: String,
                val name: String,
                val description : String,
                val genre: String,
                @DrawableRes val imageResourceId: Int)

