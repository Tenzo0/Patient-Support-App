package ru.poas.patientassistant.client.ui.patient

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import ru.poas.patientassistant.client.R
import ru.poas.patientassistant.client.viewmodel.main.MedicinesViewModel

class MedicinesFragment : Fragment() {

    companion object {
        fun newInstance() = MedicinesFragment()
    }

    private lateinit var viewModel: MedicinesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.medicines_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MedicinesViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
