package ru.poas.patientassistant.client.ui.login

import android.app.ProgressDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.poas.patientassistant.client.R
import ru.poas.patientassistant.client.databinding.LoginFragmentBinding
import ru.poas.patientassistant.client.utils.hideKeyboard
import ru.poas.patientassistant.client.viewmodel.login.LoginViewModel

class LoginFragment : Fragment() {

    private lateinit var binding: LoginFragmentBinding

    private var progressDialog: ProgressDialog? = null

    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.login_fragment, container, false)
        viewModel = ViewModelProvider(this, LoginViewModel.Factory(activity!!.application))
            .get(LoginViewModel::class.java)
        init()

        return binding.root
    }

    private fun init() {

        binding.phoneNumberEditText.isKeepHint = false

        binding.signinButton.setOnClickListener {
            if (checkInputData()) {
                viewModel.authUser(
                    binding.phoneNumberEditText.text.toString(),
                    binding.passwordEditText.text.toString()
                )
            }
        }

        binding.signupButton.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSignUpFragment())
        }

        // Observer for the progress bar.
        viewModel.isProgressShow.observe(viewLifecycleOwner, Observer<Boolean> { isProgressShow ->
            if (isProgressShow) showDialog() else hideDialog()
        })

        // Observer fot rhe network error
        viewModel.eventNetworkError.observe(viewLifecycleOwner, Observer<Boolean> { isNetworkError ->
            if (isNetworkError) onNetworkError()
        })

        viewModel.isAuthed.observe(viewLifecycleOwner, Observer<LoginViewModel.LoginType> { authed ->
            when (authed) {
                LoginViewModel.LoginType.FIRTSLY_AUTHED -> {
                    activity?.hideKeyboard()
                    findNavController().navigate(
                        LoginFragmentDirections.actionLoginFragmentToDataAgreementFragment()
                    )
                }
                //If user is authorized
                LoginViewModel.LoginType.AUTHED ->
                {
                    //Navigate to MainActivity
                    findNavController().navigate(
                        LoginFragmentDirections.actionLoginFragmentToMainActivity()
                    )
                    //Finish LoginActivity
                    activity!!.finish()
                }
            }
        })
    }

    private fun onNetworkError() {
        if (!viewModel.isNetworkErrorShown.value!!) {
            Snackbar.make(binding.root, getString(R.string.network_error), Snackbar.LENGTH_SHORT)
                .show()
            viewModel.onNetworkErrorShown()
        }
    }

    private fun showDialog() {
        progressDialog = ProgressDialog.show(this.activity, "", getString(R.string.please_wait))
    }

    private fun hideDialog() = progressDialog?.dismiss()

    private fun checkInputData(): Boolean {
        var isOk = true

        if (TextUtils.isEmpty(binding.phoneNumberEditText.rawText)) {
            binding.phoneNumberTextInputLayout.error = getString(R.string.empty_phone_number_error)
            isOk = false
        } else {
            binding.phoneNumberTextInputLayout.error = ""
        }

        if (binding.phoneNumberEditText.rawText.length != 10) {
            binding.phoneNumberTextInputLayout.error =
                getString(R.string.length_phone_number_not_equal_eleven_error)
            isOk = false
        } else {
            binding.phoneNumberTextInputLayout.error = ""
        }

        if (TextUtils.isEmpty(binding.passwordEditText.text)) {
            binding.passwordTextInputLayout.error = getString(R.string.empty_password_error)
            isOk = false
        } else {
            binding.passwordTextInputLayout.error = ""
        }

        return isOk
    }

}
