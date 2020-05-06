/*
 * Copyright (c) Alexey Barykin 2020.
 */

package ru.poas.patientassistant.client.login.ui

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


class LoginFragment : Fragment() {

    private lateinit var binding: LoginFragmentBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.login_fragment, container, false)
        viewModel = ViewModelProvider(this,
            LoginViewModel.Factory(
                activity!!.application
            )
        )
            .get(LoginViewModel::class.java)
        init()

        return binding.root
    }

    private fun init() {

        binding.phoneNumberEditText.isKeepHint = false

        binding.signinButton.setOnClickListener {
            if (checkInputData()) {
                viewModel.authUser(
                    binding.phoneNumberEditText.rawText,
                    binding.passwordEditText.text.toString()
                )
                requireActivity().hideKeyboard()
            }
        }

        // Observer for the progress bar.
        viewModel.isProgressShow.observe(viewLifecycleOwner, Observer<Boolean> { isProgressShow ->
            if (isProgressShow) binding.progressbar.show() else binding.progressbar.hide()
        })

        // Observer fot rhe network error
        viewModel.eventNetworkError.observe(viewLifecycleOwner, Observer<Boolean> { isNetworkError ->
            if (isNetworkError) onNetworkError()
        })

        viewModel.isAuthorized.observe(viewLifecycleOwner, Observer<LoginViewModel.LoginType> { authed ->
            if (authed == LoginViewModel.LoginType.AUTHORIZED ||
                    authed == LoginViewModel.LoginType.FIRSTLY_AUTHORIZED
            )
                //TODO choosing role
                if (viewModel.chosenRole == null) {
                    viewModel.chosenRole = viewModel.roles.value?.first()
                    if (viewModel.chosenRole != null)
                        viewModel.chooseRole(viewModel.chosenRole!!)
                }

            //Navigation
            when (authed) {
                LoginViewModel.LoginType.FIRSTLY_AUTHORIZED -> {
                    findNavController().navigate(
                        LoginFragmentDirections.actionLoginFragmentToDataAgreementFragment()
                    )
                }
                //If user is authorized
                LoginViewModel.LoginType.AUTHORIZED ->
                {
                    //Navigate to MainActivity
                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToMainActivity())
                    //Finish LoginActivity
                    requireActivity().finish()
                }
            }
        })
    }

    private fun onNetworkError() {
        if (!viewModel.isNetworkErrorShown.value!!) {
            Snackbar.make(binding.root, R.string.network_error, Snackbar.LENGTH_SHORT)
                .show()
            viewModel.onNetworkErrorShown()
        }
    }

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
