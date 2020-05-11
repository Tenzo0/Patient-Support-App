/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.alexey.patientassistant.client.patient.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import ru.alexey.patientassistant.client.R
import ru.alexey.patientassistant.client.databinding.ProfileFragmentBinding
import ru.alexey.patientassistant.client.preferences.UserPreferences
import ru.alexey.patientassistant.client.patient.ui.PatientActivity

class ProfileFragment : Fragment() {

    private lateinit var binding: ProfileFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.profile_fragment, container, false)

        with(binding) {
            patientProfilePhoneNumber.text = StringBuilder().apply {
                val phone = UserPreferences.getPhone()!!
                append("+7 (")
                append(phone.take(3))
                append(") ")
                append(phone.subSequence(3, 6))
                append('-')
                append(phone.subSequence(6, 8))
                append('-')
                append(phone.subSequence(8, 10))
            }
            profileName.text = UserPreferences.getUserFullName()
            exitButton.setOnClickListener {
                (requireActivity() as PatientActivity).exit()
            }
        }

        return binding.root
    }
}
