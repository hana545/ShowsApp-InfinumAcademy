package infinuma.android.shows.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import infinuma.android.shows.databinding.ActivityLoginBinding



class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val KEY_USERNAME = "USERNAME"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        Log.d("ActivityLifecycle", "onCreate")


        binding.loginInputEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(!s.toString().isEmailValid()){
                    binding.loginInputEmail.error = "Enter valid email"
                    binding.loginBtn.isEnabled = false
                } else {
                    if (binding.loginInputPassword.text?.length!! >= 6) binding.loginBtn.isEnabled = true
                }
            }
        })
        binding.loginInputPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(s.length < 6){
                    binding.loginInputPassword.error = "Your password should contain at least 6 characters"
                    binding.loginBtn.isEnabled = false
                } else {
                    if (binding.loginInputEmail.text.toString().isEmailValid()) binding.loginBtn.isEnabled = true
                }
            }
        })

        binding.loginBtn.setOnClickListener {
            //Explicit Intent
            /*
            val intent = Intent(this, WelcomeActivity::class.java)
            intent.putExtra(KEY_USERNAME,inputEmail.text.toString())
            startActivity(intent)*/


            //Implicit Intent
            val sendIntent = Intent().apply {
                action = ("infinuma.android.shows.ui.WelcomeActivity" )
                putExtra(KEY_USERNAME, binding.loginInputEmail.text.toString())
            }

            try {
                startActivity(sendIntent)
            } catch (e: ActivityNotFoundException) {
                Log.d("ImplicitIntentError", e.toString())
            }

        }
    }
    fun String.isEmailValid(): Boolean {
        return !this.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    override fun onStart() {
        Log.d("ActivityLifecycle", "onStart")
        super.onStart()
    }

    override fun onPause() {
        Log.d("ActivityLifecycle", "onPause")
        super.onPause()
    }

    override fun onResume() {
        Log.d("ActivityLifecycle", "onResume")
        super.onResume()
    }

    override fun onStop() {
        Log.d("ActivityLifecycle", "onStop")
        super.onStop()
    }
    override fun onDestroy() {
        Log.d("ActivityLifecycle", "onDestroy")
        super.onDestroy()
    }

    override fun onRestart() {
        Log.d("ActivityLifecycle", "onRestart")
        super.onRestart()
    }
}