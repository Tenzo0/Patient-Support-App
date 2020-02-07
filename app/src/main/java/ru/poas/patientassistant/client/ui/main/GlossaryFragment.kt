package ru.poas.patientassistant.client.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import ru.poas.patientassistant.client.R
import ru.poas.patientassistant.client.viewmodel.main.GlossaryViewModel

class GlossaryFragment : Fragment() {

    companion object {
        fun newInstance() = GlossaryFragment()
    }

    private lateinit var viewModel: GlossaryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.glossary_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(GlossaryViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
