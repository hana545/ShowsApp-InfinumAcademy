package infinuma.android.shows.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import infinuma.android.shows.R
import infinuma.android.shows.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    private val KEY_USERNAME = "USERNAME"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.tmpTextView.text = "Welcome, " + intent.getStringExtra(KEY_USERNAME)?.substringBefore("@") + "!"
    }
}