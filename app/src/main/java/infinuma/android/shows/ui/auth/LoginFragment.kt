package infinuma.android.shows.ui.auth

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import infinuma.android.shows.Constants
import infinuma.android.shows.R
import infinuma.android.shows.databinding.FragmentLoginBinding
import infinuma.android.shows.networking.ApiModule

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        exitTransition = inflater.inflateTransition(R.transition.fade)
        }

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
                findNavController().navigate(R.id.toShowNavGraph)
            } else {
                if(!viewModel.loginErrorResult.value.isNullOrEmpty()) {
                    Toast.makeText(context, "${viewModel.loginErrorResult.value}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun rememberUser(remember : Boolean) {
        val preferences = requireActivity().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        preferences.edit {
            putString(Constants.keyAuthAccToken, viewModel.loginAuthData.value?.get(Constants.headerAuthAccToken))
            putString(Constants.keyAuthClient, viewModel.loginAuthData.value?.get(Constants.headerAuthClient))
            putString(Constants.keyAuthExpiry, viewModel.loginAuthData.value?.get(Constants.headerAuthExpiry))
            putString(Constants.keyAuthUid, viewModel.loginAuthData.value?.get(Constants.headerAuthUid))
            putString(Constants.keyAuthContent, viewModel.loginAuthData.value?.get(Constants.headerAuthContent))
            putString(Constants.keyEmail, binding.loginInputEmail.text.toString())
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