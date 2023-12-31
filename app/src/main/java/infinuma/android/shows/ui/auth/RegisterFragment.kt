package infinuma.android.shows.ui.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import infinuma.android.shows.R
import infinuma.android.shows.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null

    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupEmailListener()
        setupPasswordListener()
        setupRepeatPasswordListener()

        initRegisterButton()
    }

    private fun initRegisterButton() = with(binding) {
        registrationBtn.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            viewModel.onRegisterButtonClicked(
                username = registrationInputEmail.text.toString(),
                password = registrationInputPassword.text.toString()
            )
        }
        viewModel.registrationResult.observe(viewLifecycleOwner){ result ->
            binding.progressBar.visibility = View.GONE
            if (result) {
                val direction = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment(true)
                val options = NavOptions.Builder()
                    .setPopUpTo(R.id.loginFragment, true)
                    .build()
                findNavController().navigate(direction, options)
            } else {
                Toast.makeText(context, "${viewModel.registrationErrorResult.value}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun setupEmailListener(){
        binding.registrationInputEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(!s.toString().isEmailValid()){
                    binding.registrationInputEmailLayout.error = "Enter valid email"
                    binding.registrationBtn.isEnabled = false
                } else {
                    binding.registrationInputEmailLayout.error = null
                    allDataCorrect()
                }
            }
        })
    }

    fun setupPasswordListener(){
        binding.registrationInputPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(s.length < 6) {
                    binding.registrationInputPasswordLayout.error = "Your password should contain at least 6 characters"
                    binding.registrationBtn.isEnabled = false
                } else if (s.toString() != binding.registrationInputRepeatPassword.text.toString() && binding.registrationInputRepeatPassword.text!!.length > 6){
                        binding.registrationInputRepeatPasswordLayout.error = "Your passwords do not match"
                        binding.registrationBtn.isEnabled = false
                } else {
                    binding.registrationInputPasswordLayout.error = null
                    allDataCorrect()
                }
            }
        })
    }

    fun setupRepeatPasswordListener(){
        binding.registrationInputRepeatPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(s.length < 6){
                    binding.registrationInputRepeatPasswordLayout.error = "Your password should contain at least 6 characters"
                    binding.registrationBtn.isEnabled = false
                } else if (s.toString() != binding.registrationInputPassword.text.toString()){
                    binding.registrationInputRepeatPasswordLayout.error = "Your passwords do not match $s - ${binding.registrationInputPassword.text}"
                    binding.registrationBtn.isEnabled = false
                } else {
                    binding.registrationInputRepeatPasswordLayout.error = null
                    allDataCorrect()
                }
            }
        })
    }
    fun allDataCorrect() {
        binding.apply {
            if (registrationInputEmailLayout.error.isNullOrEmpty() &&
                registrationInputPasswordLayout.error.isNullOrEmpty() &&
                registrationInputRepeatPasswordLayout.error.isNullOrEmpty()  ) {
                registrationBtn.isEnabled = true
            }
        }

    }

    fun String.isEmailValid(): Boolean {
        return this.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}