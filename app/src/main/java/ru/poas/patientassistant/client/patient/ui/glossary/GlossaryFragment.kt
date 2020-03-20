package ru.poas.patientassistant.client.patient.ui.glossary

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ru.poas.patientassistant.client.B2DocApplication
import ru.poas.patientassistant.client.R
import ru.poas.patientassistant.client.databinding.GlossaryFragmentBinding
import javax.inject.Inject

class GlossaryFragment : Fragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    val glossaryAdapter: GlossaryAdapter = GlossaryAdapter(
        ItemClickListener { glossary ->
            viewModel.onDefinitionClicked(glossary)
        })


    private val viewModel by viewModels<GlossaryViewModel> { viewModelFactory }

    private lateinit var binding: GlossaryFragmentBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().application as B2DocApplication)
            .appComponent
            .glossaryComponent().create()
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.glossary_fragment, container, false
        )

        with(binding) {
            dictionaryViewModel = viewModel

            with(glossarySwipeRefresh) {
                setColorSchemeResources(
                    R.color.mainPrimary,
                    R.color.green,
                    R.color.red
                )

                setOnRefreshListener {
                    viewModel.refreshGlossary()
                }
            }

            with(glossaryList) {

            }
        }
        viewModel.refreshGlossary()

        //on click navigate to GlossaryContentDetailsFragment
        binding.glossaryList.adapter = glossaryAdapter
        binding.glossaryList.layoutManager = LinearLayoutManager(activity)

        //Necessary to the observe LiveData updates with binding
        binding.lifecycleOwner = this

        //Add an Observer for Navigating when definition is clicked
        viewModel.navigateToDefinitionDetails.observe(viewLifecycleOwner, Observer { glossaryItem ->
            glossaryItem?.let {
                this.findNavController().navigate(
                    GlossaryFragmentDirections.actionGlossaryFragmentToGlossaryDetailsFragment(
                        glossaryItem.title, glossaryItem.content
                    )
                )

                viewModel.onDefinitionNavigated()
            }
        })

        viewModel.glossaryItem.observe(viewLifecycleOwner, Observer {
            it?.let {
                glossaryAdapter.submitList(it)
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

