package ru.poas.patientassistant.client.ui.login

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.poas.patientassistant.client.R
import ru.poas.patientassistant.client.databinding.SignUpFragmentBinding
import ru.poas.patientassistant.client.viewmodel.login.SignUpViewModel
import java.text.SimpleDateFormat
import java.util.*

class SignUpFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: SignUpFragmentBinding

    private var progressDialog: ProgressDialog? = null

    private var calendar: Calendar = Calendar.getInstance()

    private var isDateSelected: Boolean = false;

    private lateinit var viewModel: SignUpViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.sign_up_fragment, container, false)
        viewModel = ViewModelProviders.of(this, SignUpViewModel.Factory()).get(SignUpViewModel::class.java)
        init()
        return binding.root
    }

    private fun init() {
        // Observer for the progress bas show state.
        viewModel.isProgressShow.observe(viewLifecycleOwner, Observer<Boolean> { isProgressShow ->
            if (isProgressShow) showDialog() else hideDialog()
        })

        // Observer for the network error state.
        viewModel.eventNetworkError.observe(viewLifecycleOwner, Observer<Boolean> { isNetworkError ->
            if (isNetworkError) onNetworkError()
        })

        viewModel.phoneExists.observe(viewLifecycleOwner, Observer<Boolean> { phoneExists ->
            /* TODO if (phoneExists) {
                binding.phoneNumberTextInputLayout.error = getString(R.string.phone_exists)
            } else {
                binding.phoneNumberTextInputLayout.error = ""
            }*/
            binding.phoneNumberTextInputLayout.error = ""
        })

        viewModel.isSignUp.observe(viewLifecycleOwner, Observer<Boolean> { isSignUp ->
            /* TODO if (isSignUp) {
                Toast.makeText(
                    this.activity,
                    getString(R.string.success_sign_up),
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToLoginFragment())
            }*/
            findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToLoginFragment())
        })

        binding.signupButton.setOnClickListener {
            //TODO if (checkInputData())
                viewModel.signUp()//formSignUpUSerDto(), binding.passwordEditText.text.toString())
        }

        binding.pickDateButton.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(context!!, this, year, month, day).show()
        }
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

    /*TODO private fun formSignUpUSerDto(): SignUpUserDto = SignUpUserDto(
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time),
        binding.nameEditText.text.toString(),
        binding.surnameEditText.text.toString(),
        binding.femaleRadioButton.isChecked,
        binding.phoneNumberEditText.text.toString(),
        binding.passwordEditText.text.toString(),
        binding.weightEditText.text.toString().toDoubleOrNull(),
        binding.manHeightTextEdit.text.toString().toDoubleOrNull()
    */

    private fun checkInputData(): Boolean {
        var isOk = true

        //Check phone number's empty
        if (TextUtils.isEmpty(binding.phoneNumberEditText.text)) {
            binding.phoneNumberTextInputLayout.error = getString(R.string.empty_phone_number_error)
            isOk = false
        } else {
            binding.phoneNumberTextInputLayout.error = ""
        }

        //Check phone number length
        if (isOk && binding.phoneNumberEditText.text?.length != 10) {
            binding.phoneNumberTextInputLayout.error =
                getString(R.string.length_phone_number_not_equal_eleven_error)
            isOk = false
        } else {
            binding.phoneNumberTextInputLayout.error = ""
        }

        //Check name's empty
        if (TextUtils.isEmpty(binding.nameEditText.text)) {
            binding.nameTextInputLayout.error = getString(R.string.empty_name_error)
            isOk = false
        } else {
            binding.nameTextInputLayout.error = ""
        }

        //Check surname's empty
        if (TextUtils.isEmpty(binding.surnameEditText.text)) {
            binding.surnameTextInputLayout.error = getString(R.string.empty_surname_error)
            isOk = false
        } else {
            binding.surnameTextInputLayout.error = ""
        }

        //Check password empty
        if (TextUtils.isEmpty(binding.passwordEditText.text)) {
            binding.passwordTextInputLayout.error = getString(R.string.empty_password_error)
            isOk = false
        } else {
            binding.passwordTextInputLayout.error = ""
        }

        //Check password repeating
        if (TextUtils.isEmpty(binding.repeatPasswordEditText.text)) {
            binding.repeatPasswordTextInputLayout.error =
                getString(R.string.empty_repeat_password_error)
            isOk = false
        } else {
            binding.repeatPasswordTextInputLayout.error = ""
        }

        //Check man's height
        if (TextUtils.isEmpty(binding.manHeightTextEdit.text)) {
            binding.manHeightTextInputLayout.error = getString(R.string.empty_height_error)
            isOk = false
        } else {
            binding.manHeightTextInputLayout.error = ""
        }

        //Check man's weigth
        if (TextUtils.isEmpty(binding.weightEditText.text)) {
            binding.weightTextInputLayout.error = getString(R.string.empty_weight_error)
            isOk = false
        } else {
            binding.weightTextInputLayout.error = ""
        }

        //Check birth date
        if (!isDateSelected && isOk) {
            Snackbar.make(binding.root, getString(R.string.date_is_null), Snackbar.LENGTH_SHORT)
                .show()
        }

        //Return isOk if all checks are completed
        return isOk
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        binding.pickDateButton.text =
            SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(calendar.time)
        isDateSelected = true
    }

}
