package ru.poas.patientassistant.client.ui.main.drugs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.poas.patientassistant.client.R
import ru.poas.patientassistant.client.databinding.DrugsFragmentBinding

class DrugsFragment : Fragment() {

    private lateinit var viewModel: DrugsViewModel
    private lateinit var binding: DrugsFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.drugs_fragment, container, false)
        viewModel = ViewModelProvider(this).get(DrugsViewModel::class.java)

        return binding.root
    }

}
