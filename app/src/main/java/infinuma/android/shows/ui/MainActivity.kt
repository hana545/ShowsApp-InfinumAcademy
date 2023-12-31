package infinuma.android.shows.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import infinuma.android.shows.Constants
import infinuma.android.shows.R
import infinuma.android.shows.databinding.ActivityMainBinding
import infinuma.android.shows.networking.ApiModule

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkLoggedIn()

        ApiModule.initRetrofit(this)
    }

    private fun checkLoggedIn() {
        if(isLoggedIn()){
            Navigation.findNavController(this, R.id.fragmentContainer).navigate(R.id.toShowNavGraph)
        }
    }

    private fun isLoggedIn() : Boolean{
        val preferences = this.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        return preferences.getBoolean(Constants.keyLogedIn, false)
    }
}