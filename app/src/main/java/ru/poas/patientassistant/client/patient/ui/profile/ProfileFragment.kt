/*
 * Copyright (c) Alexey Barykin 2020.
 */

package ru.poas.patientassistant.client.patient.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import ru.poas.patientassistant.client.R
import ru.poas.patientassistant.client.databinding.ProfileFragmentBinding
import ru.poas.patientassistant.client.preferences.UserPreferences
import ru.poas.patientassistant.client.patient.ui.PatientActivity

class ProfileFragment : Fragment() {

    private lateinit var binding: ProfileFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.profile_fragment, container, false)

        with(binding) {
            patientProfilePhoneNumber.text = UserPreferences.getPhone()
            profileName.text = UserPreferences.getUserFullName()
            exitButton.setOnClickListener {
                (requireActivity() as PatientActivity).exit()
            }
        }

        return binding.root
    }
}
