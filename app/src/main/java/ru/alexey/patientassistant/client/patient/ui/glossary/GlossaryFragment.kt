/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.alexey.patientassistant.client.patient.ui.glossary

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ru.alexey.patientassistant.client.App
import ru.alexey.patientassistant.client.R
import ru.alexey.patientassistant.client.databinding.GlossaryFragmentBinding
import javax.inject.Inject

class GlossaryFragment : Fragment() {

    //GlossaryViewModel
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<GlossaryViewModel> { viewModelFactory }

    //Glossary list adapter
    private lateinit var glossaryAdapter: GlossaryAdapter

    private lateinit var binding: GlossaryFragmentBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)

        //Inject dagger dependencies
        (requireActivity().application as App)
            .appComponent
            .glossaryComponent().create()
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //setup with data binding
        binding = DataBindingUtil.inflate(
            inflater, R.layout.glossary_fragment, container, false
        )
        binding.lifecycleOwner = this

        //setup list
        setupGlossaryList()

        //setup viewModel
        binding.glossaryViewModel = viewModel
        setupGlossaryViewModel()

        viewModel.refreshGlossary()
        return binding.root
    }

    private fun setupGlossaryList() {
        glossaryAdapter = GlossaryAdapter(
            ItemClickListener { glossary ->
                viewModel.onDefinitionClicked(glossary)
            })
        with(binding.glossaryList) {
            //on click navigate to GlossaryContentDetailsFragment
            adapter = glossaryAdapter
            layoutManager = LinearLayoutManager(activity)
        }

        //set onSwipeRefresh view updating
        with(binding.glossarySwipeRefresh) {
            setColorSchemeResources(R.color.mainPrimary)
            setOnRefreshListener {
                viewModel.refreshGlossary()
            }
        }
    }

    private fun setupGlossaryViewModel() {
        with(viewModel) {
            //Add an Observer for Navigating when definition is clicked
            navigateToDefinitionDetails.observe(viewLifecycleOwner, Observer { glossaryItem ->
                glossaryItem?.let {
                    this@GlossaryFragment.findNavController().navigate(
                        GlossaryFragmentDirections.actionGlossaryFragmentToGlossaryDetailsFragment(
                            glossaryItem.title, glossaryItem.content
                        )
                    )

                    viewModel.onDefinitionNavigated()
                }
            })

            //Update list if exist database updates
            glossaryItemsList.observe(viewLifecycleOwner, Observer {
                it?.let {
                    glossaryAdapter.submitList(it)
                    binding.glossarySwipeRefresh.isRefreshing = false
                    binding.invalidateAll()
                }
            })

            isProgressShow.observe(viewLifecycleOwner, Observer<Boolean>{isProgress ->
                binding.glossarySwipeRefresh.isRefreshing = isProgress
            })

            eventNetworkError.observe(viewLifecycleOwner, Observer<Boolean> { isNetworkError ->
                if(isNetworkError) onNetworkError()
            })
        }
    }

    private fun onNetworkError() {
        if (!viewModel.isNetworkErrorShown.value!!) {
            with(Snackbar.make(binding.root, R.string.network_error, Snackbar.LENGTH_SHORT)) {
                view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.darkGray))
                setTextColor(ContextCompat.getColor(requireContext(), R.color.lightPink))
                setAction(R.string.update) {
                    binding.glossarySwipeRefresh.isRefreshing = true
                    viewModel.refreshGlossary()
                }
                show()
            }
            viewModel.onNetworkErrorShown()
        }
    }
}

