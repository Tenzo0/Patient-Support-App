/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.alexey.patientassistant.client.patient.ui.drugs

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import com.google.android.material.snackbar.Snackbar
import ru.alexey.patientassistant.client.App
import ru.alexey.patientassistant.client.R
import ru.alexey.patientassistant.client.databinding.DrugsFragmentBinding
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class DrugsFragment : Fragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<DrugsViewModel> { viewModelFactory }
    private lateinit var binding: DrugsFragmentBinding
    private lateinit var adapter: DrugsAdapter
    private lateinit var picker: DatePickerDialog

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().application as App)
            .appComponent
            .drugsComponent().create()
            .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("DRUGS_FRAGMENT: $this \n VIEWMODEL: $viewModel")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Setup list of drugs
        binding = DataBindingUtil.inflate(inflater, R.layout.drugs_fragment, container, false)
        adapter = DrugsAdapter(viewModel)
        binding.drugsList.adapter = adapter
        binding.drugsList.itemAnimator = DefaultItemAnimator()

        //Setup date navigation
        setupRecommendationsDatePickerDialog()

        updateViewDate(viewModel.selectedDate)

        viewModel.drugsListForSelectedDate.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
        viewModel.drugsList.observe(viewLifecycleOwner, Observer {
            viewModel.updateDrugsListForSelectedDate()
        })
        viewModel.refreshDrugs(requireContext())

        //set onSwipeRefresh view updating
        with(binding.drugsSwipeRefresh) {
            setColorSchemeResources(R.color.mainPrimary)
            setOnRefreshListener {
                viewModel.refreshDrugs(requireContext())
            }
        }
        viewModel.isProgressShow.observe(viewLifecycleOwner, Observer<Boolean>{isProgress ->
            binding.drugsSwipeRefresh.isRefreshing = isProgress
        })
        viewModel.eventNetworkError.observe(viewLifecycleOwner, Observer<Boolean> { isNetworkError ->
            if(isNetworkError) onNetworkError()
        })

        setHasOptionsMenu(true)

        viewModel
        return binding.root
    }

    //On click calendar icon show DatePickerDialog
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.choose_date_button -> {
                picker.show()
                true
            }
            R.id.chevron_left -> {
                viewModel.decSelectedDate()
                updateViewDate(viewModel.selectedDate)
                true
            }
            R.id.chevron_right -> {
                viewModel.incSelectedDate()
                updateViewDate(viewModel.selectedDate)
                true
            }
            else -> false
        }
    }

    private fun setupRecommendationsDatePickerDialog() {

        //Select date with DatePickerDialog
        with(viewModel.selectedDate) {
            picker = DatePickerDialog(
                requireActivity(),
                DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    viewModel.updateSelectedDate(year, month, day)
                    updateViewDate(viewModel.selectedDate)
                }, get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DAY_OF_WEEK)
            )
        }
    }

    private fun updateViewDate(date: Calendar) {
        binding.currentDay.text = drugsFragmentDateFormat.format(date.time)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.date_navigation_menu, menu)
    }

    private fun onNetworkError() {
        if (!viewModel.isNetworkErrorShown.value!!) {
            with(Snackbar.make(binding.root, R.string.network_error, Snackbar.LENGTH_SHORT)) {
                view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.darkGray))
                setTextColor(ContextCompat.getColor(requireContext(), R.color.lightPink))
                setAction(R.string.update) {
                    binding.drugsSwipeRefresh.isRefreshing = true
                    viewModel.refreshDrugs(requireContext())
                }
                show()
            }
            viewModel.onNetworkErrorShown()
        }
    }

    companion object {
        private val drugsFragmentDateFormat = SimpleDateFormat(
            "d MMMM",
            Locale("ru", "RU")
        )
    }
}
