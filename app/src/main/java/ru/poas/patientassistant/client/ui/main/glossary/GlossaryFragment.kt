package ru.poas.patientassistant.client.ui.main.glossary

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar

import ru.poas.patientassistant.client.R
import ru.poas.patientassistant.client.databinding.GlossaryFragmentBinding
import ru.poas.patientassistant.client.db.glossary.getGlossaryDatabase

class GlossaryFragment : Fragment() {

    private lateinit var viewModel: GlossaryViewModel

    private lateinit var binding: GlossaryFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val application = requireNotNull(this.activity).application

        binding = DataBindingUtil.inflate(
            inflater, R.layout.glossary_fragment, container, false
        )

        /**
         * ViewModel connection
         */
        val database = getGlossaryDatabase(application)
        val viewModelFactory =
            GlossaryViewModel.GlossaryViewModelFactory(
                database,
                application
            )
        viewModel =
            ViewModelProvider(
                this, viewModelFactory
            ).get(GlossaryViewModel::class.java)
        binding.dictionaryViewModel = viewModel
        viewModel.refreshGlossary()

        //on click navigate to GlossaryContentDetailsFragment
        val adapter = GlossaryAdapter(ItemClickListener { glossary ->
            viewModel.onDefinitionClicked(glossary)
        })
        binding.glossaryList.adapter = adapter

        val manager = LinearLayoutManager(activity)
        binding.glossaryList.layoutManager = manager

        //Necessary to the observe LiveData updates with binding
        binding.lifecycleOwner = this

        binding.glossarySwipeRefresh.setColorSchemeResources(
            R.color.mainPrimary,
            R.color.green,
            R.color.red
        )

        binding.glossarySwipeRefresh.setOnRefreshListener {
            viewModel.refreshGlossary()
        }

        //Add an Observer for Navigating when definition is clicked
        viewModel.navigateToDefinitionDetails.observe(viewLifecycleOwner, Observer { glossaryItem ->
            glossaryItem?.let {
                this.findNavController().navigate(
                    GlossaryFragmentDirections
                        .actionGlossaryFragmentToGlossaryDetailsFragment(
                            glossaryItem.title, glossaryItem.content
                        )
                )

                viewModel.onDefinitionNavigated()
            }
        })

        /**
         * DataBinding for this Fragment
         */
        viewModel.glossary.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
                binding.glossarySwipeRefresh.isRefreshing = false
                binding.invalidateAll()
            }
        })

        viewModel.isProgressShow.observe(viewLifecycleOwner, Observer<Boolean>{isProgress ->
            binding.glossarySwipeRefresh.isRefreshing = isProgress
        })

        viewModel.eventNetworkError.observe(viewLifecycleOwner, Observer<Boolean> { isNetworkError ->
            if(isNetworkError) onNetworkError()
        })

        return binding.root
    }

    private fun onNetworkError() {
        if (!viewModel.isNetworkErrorShown.value!!) {
            Snackbar.make(binding.root, getString(R.string.network_error), Snackbar.LENGTH_SHORT)
                .setAction(R.string.update) {
                    binding.glossarySwipeRefresh.isRefreshing = true
                    viewModel.refreshGlossary()
                }
                .show()
            viewModel.onNetworkErrorShown()
        }
    }
}

