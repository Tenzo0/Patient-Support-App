package ru.poas.patientassistant.client.ui.login

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import ru.poas.patientassistant.client.R
import ru.poas.patientassistant.client.databinding.ChangePasswordFragmentBinding

/**
 * A simple [Fragment] subclass.
 */
class ChangePasswordFragment : Fragment() {

    private lateinit var binding: ChangePasswordFragmentBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.change_password_fragment,
            container, false)

        viewModel = ViewModelProvider(this, LoginViewModel.Factory(activity!!.application))
            .get(LoginViewModel::class.java)

        //If user accept law about personal data
        binding.nextButton.setOnClickListener {
            if(isCorrectInputData()) {
                viewModel.updatePassword(binding.newPassword.text.toString())
            }
        }

        viewModel._isPasswordUpdated.observe(viewLifecycleOwner, Observer<Boolean> {
            if (it == true) {
                findNavController().navigate(
                    ChangePasswordFragmentDirections.actionChangePasswordFragmentToMainActivity()
                )
                //Finish LoginActivity
                requireActivity().finish()
            }
        })

        return binding.root
    }

    private fun isCorrectInputData(): Boolean {
        var isOk = true

        if (TextUtils.isEmpty(binding.newPassword.text)) {
            binding.newPasswordTextInputLayout.error = getString(R.string.empty_password_error)
            isOk = false
        } else {
            binding.newPasswordTextInputLayout.error = ""
        }

        if (!TextUtils.equals(binding.newPasswordRepeat.text, binding.newPassword.text)) {
            binding.newPasswordRepeatTextInputLayout.error =
                getString(R.string.password_and_repeat_password_are_not_equal_error)
            isOk = false
        } else {
            binding.newPasswordRepeatTextInputLayout.error = ""
        }

        return isOk
    }

}
