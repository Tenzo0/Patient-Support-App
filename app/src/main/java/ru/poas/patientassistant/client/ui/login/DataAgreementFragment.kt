package ru.poas.patientassistant.client.ui.login

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
import ru.poas.patientassistant.client.R
import ru.poas.patientassistant.client.databinding.DataAgreementFragmentBinding

class DataAgreementFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: DataAgreementFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.data_agreement_fragment, container, false)

        //If user accept law about personal data
        binding.acceptButton.setOnClickListener {
            //Navigate to MainActivity
            startMainActivity()
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
                    startMainActivity()
                }
                // decline button
                Dialog.BUTTON_NEGATIVE -> activity!!.finish()
            }
        }

    private fun startMainActivity() {
        //Navigate to MainActivity
        findNavController().navigate(
            DataAgreementFragmentDirections.actionDataAgreementFragmentToMainActivity()
        )
        //Finish LoginActivity
        activity!!.finish()
    }

    companion object {
        //private const val DIALOG_EXIT = 1
    }
}
