package infinuma.android.shows.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import infinuma.android.shows.Constants
import infinuma.android.shows.R
import infinuma.android.shows.databinding.ActivityMainBinding
import infinuma.android.shows.networking.ApiModule

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkLogedIn()

        ApiModule.initRetrofit(this)
    }

    private fun checkLogedIn() {
        if(isLogedIn()){
            val options = NavOptions.Builder()
                .setPopUpTo(R.id.loginFragment, true)
                .build()

            Navigation.findNavController(this, R.id.fragmentContainer).navigate(R.id.toShowNavGraph, null, options)
        }
    }

    private fun isLogedIn() : Boolean{
        val preferences = this.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        return preferences.getBoolean(Constants.keyLogedIn, false)
    }
}