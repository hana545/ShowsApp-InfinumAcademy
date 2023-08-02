package infinuma.android.shows.ui.show_details

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import infinuma.android.shows.db.ShowsDatabase

class ShowDetailsViewModelFactory (private val database: ShowsDatabase
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShowDetailsViewModel::class.java)) {
            return ShowDetailsViewModel(database = database) as T
        }
        throw IllegalArgumentException("Sorry, can't work with this")
    }
}