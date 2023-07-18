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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


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
            val intent = Intent(this, ShowsActivity::class.java)
            startActivity(intent)

        }
    }
    fun String.isEmailValid(): Boolean {
        return !this.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
    }
    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onRestart() {
        super.onRestart()
    }
}