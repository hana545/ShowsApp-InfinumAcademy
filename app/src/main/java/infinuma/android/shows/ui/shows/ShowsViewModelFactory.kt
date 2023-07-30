package infinuma.android.shows.ui.shows

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import infinuma.android.shows.db.ShowsDatabase

class ShowsViewModelFactory (private val application: Application, private val database: ShowsDatabase
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShowsViewModel::class.java)) {
            return ShowsViewModel(application, database = database) as T
        }
        throw IllegalArgumentException("Sorry, can't work with this")
    }
}