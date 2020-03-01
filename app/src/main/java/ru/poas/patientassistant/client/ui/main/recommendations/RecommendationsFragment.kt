package ru.poas.patientassistant.client.ui.main.recommendations

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.poas.patientassistant.client.R
import ru.poas.patientassistant.client.databinding.RecommendationsFragmentBinding
import ru.poas.patientassistant.client.db.recommendations.getRecommendationsDatabase
import java.text.SimpleDateFormat
import java.util.*

class RecommendationsFragment : Fragment() {

    private lateinit var viewModel: RecommendationsViewModel
    private lateinit var binding: RecommendationsFragmentBinding
    private var selectedDay = 0 //TODO day of the beginning of recommendations
    private lateinit var currentRecommendationCalendar: Calendar
    private lateinit var picker: DatePickerDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil
            .inflate(layoutInflater, R.layout.recommendations_fragment,
                container, false)

        //Database and viewmodel connection
        val database = getRecommendationsDatabase(requireNotNull(this.activity)
            .application)
        viewModel = ViewModelProvider(this, RecommendationsViewModel
            .RecommendationsViewModelFactory(database)
        ).get(RecommendationsViewModel::class.java)

        currentRecommendationCalendar = Calendar.getInstance()
        val day = currentRecommendationCalendar.get(Calendar.DAY_OF_MONTH)
        val month = currentRecommendationCalendar.get(Calendar.MONTH)
        val year = currentRecommendationCalendar.get(Calendar.YEAR)

        //TODO fix recommendation text and Date formatting
        selectedDay = day
        picker = DatePickerDialog(activity!!,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                run {
                    try {
                        viewModel.recommendationsList.value?.let {
                            //TODO fix to count of elapsed days from started day
                            binding.recommendationText.text = it[dayOfMonth - selectedDay].recommendationUnit.content
                            val dateFormat = SimpleDateFormat("d MMM")
                            val selectedDate = Calendar.getInstance()
                            selectedDate.set(year, monthOfYear, dayOfMonth)
                            binding.currentDateText.text = dateFormat.format(selectedDate)
                        }
                    }
                    catch (e: Exception) {

                    }
                }
            }, year, month, day)

        viewModel.refreshRecommendationsInfo()

        binding.floatingActionButton.setOnClickListener {
            binding.recommendationText.text = viewModel.recommendationsList.value.toString()
        }

        //Set Toolbar menu with calendar icon visible
        setHasOptionsMenu(true)

        return binding.root
    }

    //On click calendar icon show DatePickerDialog
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_choose_date -> {
                picker.show()
                true
            }
            else -> false
        }
    }

    //Set calendar icon in top left corner of Toolbar (as menu item)
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.recommendations_fragment_menu, menu)
    }
}
