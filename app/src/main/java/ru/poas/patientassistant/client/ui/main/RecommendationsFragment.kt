package ru.poas.patientassistant.client.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI

import ru.poas.patientassistant.client.R
import ru.poas.patientassistant.client.viewmodel.main.RecommendationsViewModel

class RecommendationsFragment : Fragment() {

    companion object {
        fun newInstance() = RecommendationsFragment()
    }

    private lateinit var viewModel: RecommendationsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.recommendations_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RecommendationsViewModel::class.java)
        // TODO: Use a ViewModel
    }


}
