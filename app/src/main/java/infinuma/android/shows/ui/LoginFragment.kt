package infinuma.android.shows.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import infinuma.android.shows.Constants
import infinuma.android.shows.R
import infinuma.android.shows.databinding.FragmentLoginBinding



class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!

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

        binding.loginBtn.setOnClickListener {
            if(binding.checkboxRememberMe.isChecked) rememberUser()
            findNavController().navigate(R.id.toShowNavGraph)

        }
    }

    private fun rememberUser() {
        val preferences = requireActivity().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        preferences.edit {
            putString(Constants.keyEmail, binding.loginInputEmail.text.toString())
            putString(Constants.keyPassword, binding.loginInputPassword.text.toString())
            putBoolean(Constants.keyLogedIn, true)
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