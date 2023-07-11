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

/*
1. Put the app in background and move it back to foreground

2023-07-09 12:18:59.329  8869-8869  ActivityLifecycle       infinuma.android.shows               D  onCreate
2023-07-09 12:18:59.346  8869-8869  ActivityLifecycle       infinuma.android.shows               D  onStart
2023-07-09 12:18:59.349  8869-8869  ActivityLifecycle       infinuma.android.shows               D  onResume
2023-07-09 12:19:07.970  8869-8869  ActivityLifecycle       infinuma.android.shows               D  onPause
2023-07-09 12:19:07.990  8869-8869  ActivityLifecycle       infinuma.android.shows               D  onStop
2023-07-09 12:19:09.052  8869-8869  ActivityLifecycle       infinuma.android.shows               D  onRestart
2023-07-09 12:19:09.054  8869-8869  ActivityLifecycle       infinuma.android.shows               D  onStart
2023-07-09 12:19:09.055  8869-8869  ActivityLifecycle       infinuma.android.shows               D  onResume


2. Kill the app

2023-07-09 12:19:47.704  8874-8874  ActivityLifecycle       infinuma.android.shows               D  onPause
2023-07-09 12:19:47.721  8874-8874  ActivityLifecycle       infinuma.android.shows               D  onStop
2023-07-09 12:19:48.528  8874-8874  ActivityLifecycle       infinuma.android.shows               D  onDestroy

3. Lock the phones screen and unlock it

2023-07-09 12:20:09.197  9302-9302  ActivityLifecycle       infinuma.android.shows               D  onPause
2023-07-09 12:20:09.225  9302-9302  ActivityLifecycle       infinuma.android.shows               D  onStop
2023-07-09 12:20:14.227  9302-9302  ActivityLifecycle       infinuma.android.shows               D  onRestart
2023-07-09 12:20:14.233  9302-9302  ActivityLifecycle       infinuma.android.shows               D  onStart
2023-07-09 12:20:14.237  9302-9302  ActivityLifecycle       infinuma.android.shows               D  onResume

*/


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val KEY_USERNAME = "USERNAME"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        Log.d("ActivityLifecycle", "onCreate")

        val inputEmail = binding.loginInputEmail
        val inputPassword = binding.loginInputPassword
        val btnLogin = binding.loginBtn

        inputEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(!s.toString().isEmailValid()){
                    inputEmail.error = "Enter valid email"
                    btnLogin.isEnabled = false
                } else {
                    if (inputPassword.text?.length!! >= 6) btnLogin.isEnabled = true
                }
            }
        })
        inputPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(s.length < 6){
                    inputPassword.error = "Your password should contain at least 6 characters"
                    btnLogin.isEnabled = false
                } else {
                    if (inputEmail.text.toString().isEmailValid()) btnLogin.isEnabled = true
                }
            }
        })

        btnLogin.setOnClickListener {
            //Explicit Intent
            /*
            val intent = Intent(this, WelcomeActivity::class.java)
            intent.putExtra(KEY_USERNAME,inputEmail.text.toString())
            startActivity(intent)*/


            //Implicit Intent
            val sendIntent = Intent().apply {
                action = ("infinuma.android.shows.ui.WelcomeActivity" )
                putExtra(KEY_USERNAME, inputEmail.text.toString())
            }

            try {
                startActivity(sendIntent)
            } catch (e: ActivityNotFoundException) {
                Log.d("ImplicitIntentError", e.toString())
            }

        }
    }
    fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
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