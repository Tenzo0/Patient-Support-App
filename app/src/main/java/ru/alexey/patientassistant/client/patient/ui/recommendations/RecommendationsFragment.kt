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
import kotlin.math.floor

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

    override fun onResume() {
        super.onResume()
        viewModel.refreshRecommendationsInfo()
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
                    getRecommendationByDate(viewModel.selectedDate.value!!)!!.recommendationUnit.id
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

        requireActivity().toolbar.title = getString(R.string.recommendations)

        binding.swipeRefreshLayout.requestFocus()
        //updateRecommendationViewForDate(viewModel.selectedDate)

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

    @Suppress("DEPRECATION")
    private fun setupRecommendationsDatePickerDialog() {

        //Select date with DatePickerDialog
        with(viewModel.selectedDate.value!!) {
            picker = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { _, year, month, date ->
                    viewModel.updateSelectedDate(year, month, date)
                }, year + 1900, month, date
            )
        }
    }

    private fun setupViewModel() {
        with(viewModel) {
            recommendationsList.observe(viewLifecycleOwner, Observer {
                updateRecommendationViewForDate()
            })

            selectedDate.observe(viewLifecycleOwner, Observer {
                updateRecommendationViewForDate()
            })

            operationDate.observe(viewLifecycleOwner, Observer {
                if (it != null)
                    updateRecommendationViewForDate()
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
        }
    }

    private fun getRecommendationByDate(date: Date): Recommendation? {
        var recommendation: Recommendation? = null
        viewModel.operationDate.value?.let { operationDate ->
            val millisPassedFromOperation = date.time - operationDate.time
            val daysPassedFromOperationAsDouble =
                (millisPassedFromOperation.toDouble() / (1000 * 60 * 60 * 24))
            val daysPassedFromOperation =
                if (daysPassedFromOperationAsDouble >= 0) daysPassedFromOperationAsDouble.toInt()
                else floor(daysPassedFromOperationAsDouble)

            Timber.i("$daysPassedFromOperation days passed since operation date $operationDate")

            //Find recommendation by days passed from operation date
            recommendation = viewModel.recommendationsList.value?.firstOrNull { it.day == daysPassedFromOperation }
        }

        return recommendation
    }

    private fun updateRecommendationButton() {
        val selectedDate = viewModel.selectedDate.value
        val currentDate = DatePreferences.getActualServerDate()
        val isEqualDates = (databaseSimpleDateFormat.format(selectedDate?.time) == currentDate)
        if (viewModel.isRecommendationConfirmed.value == false && isEqualDates && isCurrentItemContainRecommendation)
            revealNotVisibleView(binding.doneRecommendationButton)
        else {
            hideVisibleView(binding.doneRecommendationButton)
        }
    }

    private fun updateRecommendationViewForDate() {

        val selectedDate = viewModel.selectedDate.value!!
        binding.chosenDate.text = recommendationsFragmentDateFormat.format(selectedDate.time)

        with(viewModel) {
            //Find recommendation by days passed from operation date
            val recommendation = getRecommendationByDate(selectedDate)
            recommendation?.recommendationUnit?.let {
                val isContentNotBlank = it.content.isNotBlank()
                val isImportantContentNotBlank = it.importantContent.isNotBlank()

                if (isContentNotBlank || isImportantContentNotBlank)
                //Set content to the view
                {
                    with(binding) {
                        isCurrentItemContainRecommendation = true
                        scrollView.setScrollingEnabled(true)
                        refreshRecommendationConfirm(it.id)

                        crossfadeViews(displayedRecommendations, emptyRecommendationCard)

                        //Content
                        if (isContentNotBlank) {
                            recommendationText.text = it.content
                            recommendationCard.visibility = VISIBLE
                        } else {
                            recommendationCard.visibility = GONE
                        }

                        //Important content
                        if (isImportantContentNotBlank) {
                            binding.importantRecommendationText.text = it.importantContent
                            importantCard.visibility = VISIBLE
                        } else {
                            importantCard.visibility = GONE
                        }
                    }
                }
            }

            if (recommendation?.recommendationUnit == null) {
                with(binding) {
                    isCurrentItemContainRecommendation = false
                    scrollView.setScrollingEnabled(false)
                    crossfadeViews(emptyRecommendationCard, displayedRecommendations)
                }
            }

            updateRecommendationButton()
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
                true
            }
            R.id.chevron_right -> {
                viewModel.incSelectedDate()
                true
            }
            else -> false
        }
    }

    override fun onPause() {
        super.onPause()
        picker.dismiss()
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