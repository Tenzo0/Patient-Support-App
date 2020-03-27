package ru.poas.patientassistant.client.patient.ui.drugs

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import ru.poas.patientassistant.client.B2DocApplication
import ru.poas.patientassistant.client.R
import ru.poas.patientassistant.client.databinding.DrugsFragmentBinding
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class DrugsFragment : Fragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<DrugsViewModel> { viewModelFactory }
    private lateinit var binding: DrugsFragmentBinding
    @Inject lateinit var adapter: DrugsAdapter
    private lateinit var picker: DatePickerDialog

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
        //Setup list of drugs
        binding = DataBindingUtil.inflate(inflater, R.layout.drugs_fragment, container, false)
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
        viewModel.refreshDrugs()

        setHasOptionsMenu(true)

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

    companion object {
        private val drugsFragmentDateFormat = SimpleDateFormat(
            "d MMMM",
            Locale("ru", "RU")
        )
    }
}
