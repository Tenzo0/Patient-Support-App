/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.alexey.patientassistant.client.login.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.alexey.patientassistant.client.R
import ru.alexey.patientassistant.client.databinding.DataAgreementFragmentBinding
import ru.alexey.patientassistant.client.preferences.UserPreferences

class DataAgreementFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: DataAgreementFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.data_agreement_fragment, container, false)

        //If user accept law about personal data
        binding.acceptButton.setOnClickListener {
            navigateNext()
        }

        //if decline
        val declineDialog = createDeclineDialog()
        binding.declineButton.setOnClickListener {
            declineDialog.show()
        }

        return binding.root
    }

    private fun createDeclineDialog(): Dialog = AlertDialog.Builder(requireContext())
        .setTitle("Выход из приложения")
        .setMessage("При несогласии на обработку персональных данных, " +
            "вы не сможете использовать приложение.")
        .setPositiveButton("Согласиться", myClickListener)
        .setNegativeButton("выйти", myClickListener)
        .setNeutralButton("Отмена", myClickListener)
        .create()

    private var myClickListener: DialogInterface.OnClickListener =
        DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                // accept button
                Dialog.BUTTON_POSITIVE -> {
                    navigateNext()
                }
                // decline button
                Dialog.BUTTON_NEGATIVE -> activity!!.finish()
            }
        }

    private fun navigateNext() {
        if (UserPreferences.isTemporaryPassword()) {
            findNavController().navigate(
                DataAgreementFragmentDirections.actionDataAgreementFragmentToChangePasswordFragment()
            )
        }
        else {
            //Navigate to MainActivity
            findNavController().navigate(DataAgreementFragmentDirections.actionDataAgreementFragmentToMainActivity())
            //Finish LoginActivity
            requireActivity().finish()
        }
    }
}
