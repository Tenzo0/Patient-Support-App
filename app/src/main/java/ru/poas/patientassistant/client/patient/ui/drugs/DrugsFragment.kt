package ru.poas.patientassistant.client.patient.ui.drugs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.poas.patientassistant.client.B2DocApplication
import ru.poas.patientassistant.client.R
import ru.poas.patientassistant.client.databinding.DrugsFragmentBinding
import javax.inject.Inject

class DrugsFragment : Fragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<DrugsViewModel> { viewModelFactory }
    private lateinit var binding: DrugsFragmentBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().application as B2DocApplication)
            .appComponent
            .drugsComponent().create()
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.drugs_fragment, container, false)

        viewModel.drugsList.observe(viewLifecycleOwner, Observer {

        })
        viewModel.refreshDrugs()

        return binding.root
    }
}
