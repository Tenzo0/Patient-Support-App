/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.alexey.patientassistant.client.patient.ui.recommendations

import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.*
import android.view.View.*
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_patient.*
import ru.alexey.patientassistant.client.App
import ru.alexey.patientassistant.client.R
import ru.alexey.patientassistant.client.databinding.RecommendationsFragmentBinding
import ru.alexey.patientassistant.client.patient.vo.Recommendation
import ru.alexey.patientassistant.client.preferences.DatePreferences
import ru.alexey.patientassistant.client.preferences.UserPreferences
import ru.alexey.patientassistant.client.utils.*
import ru.alexey.patientassistant.client.utils.DateUtils.databaseSimpleDateFormat
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class RecommendationsFragment : Fragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<RecommendationsViewModel> { viewModelFactory }
    private lateinit var binding: RecommendationsFragmentBinding
    private lateinit var picker: DatePickerDialog
    private var isCurrentItemContainRecommendation: Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().application as App)
            .appComponent
            .recommendationsComponent().create()
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.recommendations_fragment,
                container, false)

        setupHeightChangeAnimations()

        //Set onRefresh and onClick listeners
        with(binding) {

            swipeRefreshLayout.setColorSchemeResources(R.color.mainPrimary)
            swipeRefreshLayout.setOnRefreshListener {
                viewModel.refreshRecommendationsInfo()
            }

            doneRecommendationButton.setOnClickListener {
                viewModel.confirmRecommendation(
                    getCurrentRecommendation(viewModel.selectedDate)!!.recommendationUnit.id
                )
                with(Snackbar.make(binding.root, R.string.recommendationIsDone, Snackbar.LENGTH_LONG)) {
                    view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.darkGray))
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    show()
                }
                Timber.i(getString(R.string.recommendationIsDone))
            }
        }

        setupViewModel()

        setupRecommendationsDatePickerDialog()

        //Set Toolbar menu with calendar icon visible
        setHasOptionsMenu(true)

        requireActivity().toolbar.title = getString(R.string.for_patient)

        updateRecommendationViewForDate(viewModel.selectedDate)

        return binding.root
    }

    private fun setupHeightChangeAnimations() {
        binding.recommendationCard.layoutTransition = LayoutTransition().apply {
            enableTransitionType(LayoutTransition.CHANGING)
            setDuration(ANIMATION_DURATION)
        }

        binding.importantCard.layoutTransition = LayoutTransition().apply {
            enableTransitionType(LayoutTransition.CHANGING)
            setDuration(ANIMATION_DURATION)
        }
    }

    private fun setupRecommendationsDatePickerDialog() {

        //Select date with DatePickerDialog
        with(viewModel.selectedDate) {
            picker = DatePickerDialog(
                requireActivity(),
                DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    viewModel.updateSelectedDate(year, month, day)
                    updateRecommendationViewForDate(viewModel.selectedDate)
                }, get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DAY_OF_WEEK)
            )
        }
    }

    private fun setupViewModel() {
        with(viewModel) {
            recommendationsList.observe(viewLifecycleOwner, Observer {
                updateRecommendationViewForDate(selectedDate)
            })

            isProgressShow.observe(viewLifecycleOwner, Observer {
                binding.swipeRefreshLayout.isRefreshing = it
            })

            isRecommendationConfirmed.observe(viewLifecycleOwner, Observer {
                updateRecommendationButton()
            })

            isNetworkErrorShown.observe(viewLifecycleOwner, Observer {
                if (it == true)
                    Snackbar.make(
                        binding.root,
                        R.string.network_error,
                        Snackbar.LENGTH_SHORT
                    ).show()
            })

            refreshRecommendationsInfo()
        }
    }

    private fun getCurrentRecommendation(date: Calendar?): Recommendation? {
        var recommendation: Recommendation? = null
        try {
            val millisPassedFromOperation =
                date!!.timeInMillis - viewModel.operationDate.timeInMillis
            val daysPassedFromOperation = (millisPassedFromOperation / (1000 * 60 * 60 * 24))

            Timber.i("$daysPassedFromOperation days passed since operation date")

            //Find recommendation by days passed from operation date
            recommendation = viewModel.recommendationsList
                .value?.firstOrNull {
                it.day == daysPassedFromOperation.toInt()
            }
        }
        catch (e: Exception) {
            Timber.e(e)
        }
        return recommendation
    }

    private fun updateRecommendationButton() {
        val selectedDate = viewModel.selectedDate
        val currentDate = DatePreferences.getActualServerDate()
        val isEqualDates =
            (databaseSimpleDateFormat.format(selectedDate.timeInMillis) == currentDate)
        if (viewModel.isRecommendationConfirmed.value == false && isEqualDates
            && isCurrentItemContainRecommendation)
            revealNotVisibleView(binding.doneRecommendationButton)
        else {
            hideVisibleView(binding.doneRecommendationButton)
        }
    }

    private fun updateRecommendationViewForDate(date: Calendar?) {
        binding.chosenDate.text = recommendationsFragmentDateFormat.format(date!!.time)

        with(viewModel) {
            try {
                UserPreferences.init(requireContext())
                binding.scrollView.fullScroll(FOCUS_UP)

                val millisPassedFromOperation =
                    date.timeInMillis - databaseSimpleDateFormat.parse(UserPreferences.getOperationDate()).time
                val daysPassedFromOperation = (millisPassedFromOperation / (1000 * 60 * 60 * 24))

                Timber.i("$daysPassedFromOperation days passed since operation date (${viewModel.operationDate.get(Calendar.DATE)})")

                //Find recommendation by days passed from operation date
                val recommendation = recommendationsList
                    .value?.firstOrNull {
                    it.day == daysPassedFromOperation.toInt()
                }

                with(binding) {
                    //If recommendation found, set it to view
                    if (recommendation?.recommendationUnit != null) {
                        isCurrentItemContainRecommendation = true
                        scrollView.setScrollingEnabled(true)
                        refreshRecommendationConfirm(recommendation.recommendationUnit.id)
                        recommendationText.text = recommendation.recommendationUnit.content
                        crossfadeViews(displayedRecommendations, emptyRecommendationCard)
                        importantCard.visibility = if (recommendation.recommendationUnit.importantContent.isNotBlank()) {
                            binding.importantRecommendationText.text =
                                recommendation.recommendationUnit.importantContent
                            VISIBLE
                        } else {
                            GONE
                        }
                    }
                    //Else show view with empty recommendation text
                    else {
                        isCurrentItemContainRecommendation = false
                        scrollView.setScrollingEnabled(false)
                        crossfadeViews(emptyRecommendationCard, displayedRecommendations)
                    }
                }
                updateRecommendationButton()
            }
            catch (e: Exception) {
                Timber.e("first user auth? $e")
                with(binding) {
                    isCurrentItemContainRecommendation = false
                    scrollView.setScrollingEnabled(false)
                    crossfadeViews(emptyRecommendationCard, displayedRecommendations)
                }
            }
        }
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
                updateRecommendationViewForDate(viewModel.selectedDate)
                true
            }
            R.id.chevron_right -> {
                viewModel.incSelectedDate()
                updateRecommendationViewForDate(viewModel.selectedDate)
                true
            }
            else -> false
        }
    }

    //Set calendar icon in top left corner of Toolbar (as menu item)
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.date_navigation_menu, menu)
    }



    companion object {
        private val recommendationsFragmentDateFormat = SimpleDateFormat(
            "d MMMM",
            Locale("ru", "RU")
        )
    }
}
class LockableNestedScrollView
    @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0):
    NestedScrollView(context, attrs, defStyleAttr)
{
    // true if we can scroll (not locked)
    // false if we cannot scroll (locked)
    private var isScrollable = true;

    fun setScrollingEnabled(enabled: Boolean) {
        isScrollable = enabled;
    }

    fun isScrollable(): Boolean = isScrollable

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent) =
        when (event.action) {
            MotionEvent.ACTION_DOWN ->
                // if we can scroll pass the event to the superclass
                isScrollable && super.onTouchEvent(event);
            else ->
                super.onTouchEvent(event);
        }

     override fun onInterceptTouchEvent(event: MotionEvent) =
         // Don't do anything with intercepted touch events if
         // we are not scrollable
         isScrollable && super.onInterceptTouchEvent(event)
}