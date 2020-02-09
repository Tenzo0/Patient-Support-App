package ru.poas.patientassistant.client.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            findNavController().navigate(
                DataAgreementFragmentDirections.actionDataAgreementFragmentToMainActivity()
            )
            //Finish LoginActivity
            activity!!.finish()
        }

        return binding.root
    }

}
