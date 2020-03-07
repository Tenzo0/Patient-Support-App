package ru.poas.patientassistant.client.ui.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import ru.poas.patientassistant.client.R
import ru.poas.patientassistant.client.databinding.ProfileFragmentBinding
import ru.poas.patientassistant.client.preferences.UserPreferences
import ru.poas.patientassistant.client.ui.main.MainActivity

class ProfileFragment : Fragment() {

    private lateinit var binding: ProfileFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.profile_fragment, container, false)

        with(binding) {
            patientProfileDoctorName.text = UserPreferences.getRelatedDoctorName()
            patientProfilePhoneNumber.text = UserPreferences.getPhone()
            profileName.text = UserPreferences.getUserFullName()
            val doctorName = UserPreferences.getRelatedDoctorName()
            patientProfileDoctorName.text = if (doctorName.isNullOrBlank())
                 "не назначен"
            else
                doctorName
            exitButton.setOnClickListener {
                (requireActivity() as MainActivity).exit()
            }
        }

        return binding.root
    }
}
