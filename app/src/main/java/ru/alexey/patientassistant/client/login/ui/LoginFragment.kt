/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.alexey.patientassistant.client.login.ui

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.alexey.patientassistant.client.R
import ru.alexey.patientassistant.client.databinding.LoginFragmentBinding
import ru.alexey.patientassistant.client.utils.hideKeyboard


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
        binding.phoneNumberEditText.setText("1234567890")
        binding.signinButton.setOnClickListener {
            if (checkInputData()) {
                viewModel.authUser(
                    binding.phoneNumberEditText.rawText,
                    binding.passwordEditText.text.toString()
                )
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
            requireActivity().hideKeyboard()
            if (authed == LoginViewModel.LoginType.AUTHORIZED ||
                    authed == LoginViewModel.LoginType.FIRSTLY_AUTHORIZED
            )
            //TODO choosing role
            if (viewModel.chosenRole == null && !viewModel.roles.isNullOrEmpty()) {
                viewModel.chosenRole = viewModel.roles.first()
                if (viewModel.chosenRole != null) {
                    viewModel.chooseRole(viewModel.chosenRole!!)

                    //Navigation
                    if (viewModel.chosenRole!!.name == "ROLE_PATIENT") when (authed) {
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
                }
            }
            else if (viewModel.roles.isNullOrEmpty()) {
                with(Snackbar.make(binding.root, "Нет доступных ролей", Snackbar.LENGTH_SHORT)) {
                    view.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.darkGray
                        )
                    )
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.lightPink))
                    show()
                }
            }
        })
    }

    private fun onNetworkError() {
        if (!viewModel.isNetworkErrorShown.value!!) {
            val errorMessage =
                if (viewModel.isIncorrectLoginOrPassword)
                    R.string.incorrect_auth
                else
                    R.string.network_error

            with(Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT)) {
                view.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.darkGray
                    )
                )
                setTextColor(ContextCompat.getColor(requireContext(), R.color.lightPink))
                show()
            }
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
