package infinuma.android.shows.ui.auth

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import infinuma.android.shows.Constants
import infinuma.android.shows.R
import infinuma.android.shows.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupEmailListener()
        setupPasswordListener()

        setupAnimations()

        if (arguments?.getBoolean("registered") == true) {
            binding.apply {
                loginTitle.text = "Registration successful"
                toRegistrationBtn.visibility = View.GONE
            }
        }

        binding.apply {
            loginBtn.setOnClickListener {
                binding.progressBar.visibility = View.VISIBLE
                viewModel.onLoginButtonClicked(
                    username = loginInputEmail.text.toString(),
                    password = loginInputPassword.text.toString()
                )
            }

            toRegistrationBtn.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
        }

        viewModel.loginResult.observe(viewLifecycleOwner){ result ->
            binding.progressBar.visibility = View.GONE
            if (result) {
                if (binding.checkboxRememberMe.isChecked) rememberUser(true) else rememberUser(false)
                val options = NavOptions.Builder()
                    .setPopUpTo(R.id.loginFragment, true)
                    .build()
                findNavController().navigate(R.id.toShowNavGraph, null, options)
            } else {
                if(!viewModel.loginErrorResult.value.isNullOrEmpty()) {
                    Toast.makeText(context, "${viewModel.loginErrorResult.value}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupAnimations() {
        binding.loginImgLogo.apply{
            alpha = 0f
            translationY =  -(resources.displayMetrics.heightPixels-y)
            animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(1500)
                .setInterpolator(BounceInterpolator())
                .start()
        }
        binding.loginAppName.apply{
            scaleX = 0.5f
            scaleY = 0.5f
            animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(2000)
                .setInterpolator(OvershootInterpolator())
                .start()
        }
    }

    private fun rememberUser(remember : Boolean) {
        val preferences = requireActivity().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        preferences.edit {
            putString(Constants.keyAuthAccToken, viewModel.loginAuthData[Constants.headerAuthAccToken])
            putString(Constants.keyAuthClient, viewModel.loginAuthData[Constants.headerAuthClient])
            putString(Constants.keyAuthUid, viewModel.loginAuthData[Constants.headerAuthUid])
            putString(Constants.keyEmail, binding.loginInputEmail.text.toString())
            putString(Constants.keyImageUri, viewModel.imageUri)
            Log.e("LOGIN", "url "+viewModel.imageUri)
            putBoolean(Constants.keyLogedIn, remember)
        }
    }

    fun setupEmailListener(){
        binding.loginInputEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(!s.toString().isEmailValid()){
                    binding.loginInputEmailLayout.error = "Enter valid email"
                    binding.loginBtn.isEnabled = false
                } else {
                    binding.loginInputEmailLayout.error = null
                    if (binding.loginInputPassword.text?.length!! >= 6) binding.loginBtn.isEnabled = true
                }
            }
        })
    }

    fun setupPasswordListener(){
        binding.loginInputPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(s.length < 6){
                    binding.loginInputPasswordLayout.error = "Your password should contain at least 6 characters"
                    binding.loginBtn.isEnabled = false
                } else {
                    binding.loginInputPasswordLayout.error = null
                    if (binding.loginInputEmail.text.toString().isEmailValid()) binding.loginBtn.isEnabled = true
                }
            }
        })

    }

    fun String.isEmailValid(): Boolean {
        return this.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}