package ru.poas.patientassistant.client.ui.main.recommendations

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.text.Layout.HYPHENATION_FREQUENCY_FULL
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import ru.poas.patientassistant.client.R
import ru.poas.patientassistant.client.databinding.RecommendationsFragmentBinding
import ru.poas.patientassistant.client.db.recommendations.getRecommendationsDatabase
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


class RecommendationsFragment : Fragment() {

    private lateinit var viewModel: RecommendationsViewModel
    private lateinit var binding: RecommendationsFragmentBinding
    private lateinit var picker: DatePickerDialog
    private val recommendationsFragmentDateFormat = SimpleDateFormat("d MMMM",
        Locale("ru", "RU"))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil
            .inflate(layoutInflater, R.layout.recommendations_fragment,
                container, false)

        //Database and viewmodel connection TODO вынести отсюда бд -.-
        val database = getRecommendationsDatabase(activity!!.applicationContext)
        viewModel = ViewModelProvider(this, RecommendationsViewModel
            .RecommendationsViewModelFactory(database)
        ).get(RecommendationsViewModel::class.java)

        binding.chevronRight.setOnClickListener {
            viewModel.incSelectedDate()
            updateRecommendationView(viewModel.selectedDate.value)
        }

        binding.chevronLeft.setOnClickListener {
            viewModel.decSelectedDate()
            updateRecommendationView(viewModel.selectedDate.value)
        }

        picker = DatePickerDialog(activity!!,
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                viewModel.updateSelectedDate(year, month, day)
                updateRecommendationView(viewModel.selectedDate.value)
            },
            viewModel.selectedDate.value!!.get(Calendar.YEAR),
            viewModel.selectedDate.value!!.get(Calendar.MONTH),
            viewModel.selectedDate.value!!.get(Calendar.DAY_OF_WEEK)
        )

        viewModel.recommendationsList.observe(viewLifecycleOwner, Observer {
            updateRecommendationView(viewModel.selectedDate.value)
        })

        binding.recommendationText.textLocale = Locale("ru", "RU")
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            binding.recommendationText.hyphenationFrequency = HYPHENATION_FREQUENCY_FULL
        else {
            binding.recommendationText.gravity = Gravity.END
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshRecommendationsInfo()
        }

        viewModel.isProgressShow.observe(viewLifecycleOwner, Observer {
            binding.swipeRefreshLayout.isRefreshing = it
        })

        binding.doneRecommendationButton.setOnClickListener {
            viewModel.confirmRecommendation()
        }

        viewModel.isRecommendationConfirmed.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                binding.doneRecommendationButton.visibility = GONE
                Snackbar.make(binding.root, "Рекомендация выполнена!", Snackbar.LENGTH_SHORT).show()
                Timber.i("Рекомендация выполнена")
            }
        })

        viewModel.isNetworkErrorShown.observe(viewLifecycleOwner, Observer {
            if (it == true)
                Snackbar.make(binding.root, R.string.network_error, Snackbar.LENGTH_SHORT).show()
        })

        //Set Toolbar menu with calendar icon visible
        setHasOptionsMenu(true)

        return binding.root
    }

    private fun updateRecommendationView(date: Calendar?) {
        with(viewModel) {
            binding.currentDateText.text = recommendationsFragmentDateFormat.format(date!!.time)

            val millisPassedFromOperation = date.timeInMillis - operationDate.value!!.timeInMillis
            val daysPassedFromOperation = (millisPassedFromOperation / (1000 * 60 * 60 * 24))

            Timber.i("$daysPassedFromOperation days passed since operation date")

            //Find recommendation by days passed from operation date
            val recommendation = recommendationsList
                .value?.firstOrNull {
                it.day == daysPassedFromOperation.toInt()
            }

            //If recommendation found, set it to view
            if (recommendation?.recommendationUnit != null) {
                binding.recommendationText.text = recommendation.recommendationUnit.content
                binding.displayedRecommendations.visibility = VISIBLE
                binding.emptyRecommendationCard.visibility = GONE

                binding.importantCard.visibility = if (recommendation.recommendationUnit.importantContent.isNotBlank()) {
                    binding.importantRecommendationText.text = recommendation.recommendationUnit.importantContent
                    VISIBLE
                }
                else {
                    GONE
                }
            }
            //Else show view with empty recommendation text
            else {
                binding.displayedRecommendations.visibility = GONE
                binding.emptyRecommendationCard.visibility = VISIBLE
            }

            binding.doneRecommendationButton.visibility =
                if (selectedDate.value!!.get(Calendar.DATE) == Calendar.getInstance().get(Calendar.DATE)
                    && !isRecommendationConfirmed.value!!)
                    VISIBLE
                else
                    GONE
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshRecommendationsInfo()
    }

    //On click calendar icon show DatePickerDialog
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.choose_date_button -> {
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
