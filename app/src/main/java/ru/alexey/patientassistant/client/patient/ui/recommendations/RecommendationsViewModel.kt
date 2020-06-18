/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.alexey.patientassistant.client.patient.ui.recommendations

import android.content.Context
import androidx.lifecycle.LiveData
import kotlinx.coroutines.launch
import okhttp3.Credentials
import ru.alexey.patientassistant.client.preferences.UserPreferences
import ru.alexey.patientassistant.client.patient.repository.RecommendationsRepository
import ru.alexey.patientassistant.client.viewmodel.BaseViewModel
import ru.alexey.patientassistant.client.patient.vo.Recommendation
import ru.alexey.patientassistant.client.patient.vo.RecommendationConfirmKey
import ru.alexey.patientassistant.client.utils.DateUtils.databaseSimpleDateFormat
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class RecommendationsViewModel @Inject constructor(
    private val context: Context,
    private val recommendationsRepository: RecommendationsRepository
) : BaseViewModel() {

    val recommendationsList: LiveData<List<Recommendation>> = recommendationsRepository.recommendationsList
    val isRecommendationConfirmed: LiveData<Boolean> = recommendationsRepository.isRecommendationConfirmed

    var operationDate: Calendar = Calendar.getInstance()

    private var _selectedDate: Calendar = Calendar.getInstance()
    val selectedDate: Calendar
        get() = _selectedDate

    init {
        UserPreferences.init(context)
        operationDate.timeInMillis = UserPreferences.getOperationDate()?.let {
            databaseSimpleDateFormat.parse(it)!!.time
        } ?: 0
    }
    fun confirmRecommendation(recommendationUnitId: Long) {
        viewModelScope.launch {
            try {
                recommendationsRepository.confirmRecommendation(
                    Credentials.basic(
                        UserPreferences.getPhone()!!,
                        UserPreferences.getPassword()!!
                    ),
                    RecommendationConfirmKey(
                        UserPreferences.getId(),
                        UserPreferences.getRecommendationId()
                    ),
                    recommendationUnitId
                )
                operationDate.timeInMillis = UserPreferences.getOperationDate()?.let {
                    databaseSimpleDateFormat.parse(it)!!.time
                } ?: 0
            }
            catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    fun updateSelectedDate(year: Int, month: Int, day: Int) {
        _selectedDate.set(year, month, day)
        Timber.i("selected date is changed: ${_selectedDate.get(Calendar.DAY_OF_MONTH)}")
    }

    fun incSelectedDate() {
        _selectedDate.add(Calendar.DATE, 1)
    }

    fun decSelectedDate() {
        _selectedDate.add(Calendar.DATE, -1)
    }

    fun refreshRecommendationsInfo() {
        viewModelScope.launch {
            _isProgressShow.value = true
            try {
                recommendationsRepository.refreshRecommendationInfo(
                    Credentials.basic(
                        UserPreferences.getPhone()!!,
                        UserPreferences.getPassword()!!
                    )
                )
                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false
            } catch (e: Exception) {
                Timber.e(e)
                _eventNetworkError.value = true
            }
            _isProgressShow.value = false
        }
    }

    fun refreshRecommendationConfirm(recommendationUnitId: Long) {
        viewModelScope.launch {
            try {
                recommendationsRepository.getIsRecommendationConfirmedById(
                    recommendationUnitId
                )
            }
            catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    companion object {
        val DATABASE_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale("ru", "RU"))
    }
}
