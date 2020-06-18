/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.alexey.patientassistant.client.patient.ui.recommendations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import ru.alexey.patientassistant.client.patient.repository.RecommendationsRepository
import ru.alexey.patientassistant.client.patient.vo.Recommendation
import ru.alexey.patientassistant.client.patient.vo.RecommendationConfirmKey
import ru.alexey.patientassistant.client.preferences.UserPreferences
import ru.alexey.patientassistant.client.utils.DateUtils.databaseSimpleDateFormat
import ru.alexey.patientassistant.client.viewmodel.BaseViewModel
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class RecommendationsViewModel @Inject constructor(
    private val recommendationsRepository: RecommendationsRepository
) : BaseViewModel() {

    val recommendationsList: LiveData<List<Recommendation>> = recommendationsRepository.recommendationsList
    val isRecommendationConfirmed: LiveData<Boolean> = recommendationsRepository.isRecommendationConfirmed

    val selectedDate = MutableLiveData(Date())
    var operationDate = MutableLiveData<Date?>()

    private fun updateOperationDate() {
        val operationDateAsString = UserPreferences.getOperationDate()
        if (operationDateAsString != null)
            operationDate.postValue(databaseSimpleDateFormat.parse(operationDateAsString))
        Timber.i("OPERATION_DATE:$operationDateAsString")
    }

    fun confirmRecommendation(recommendationUnitId: Long) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
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
                    updateOperationDate()
                }
            }
            catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    fun updateSelectedDate(year: Int, month: Int, day: Int) {
        @Suppress("DEPRECATION")
        selectedDate.value = Date(year, month, day)
    }

    fun incSelectedDate() {
        val newDateInMillis = selectedDate.value!!.time + ONE_DAY_IN_MILLIS
        if (newDateInMillis >= 0)
            selectedDate.value = Date(newDateInMillis)
    }

    fun decSelectedDate() {
        val newDateInMillis = selectedDate.value!!.time - ONE_DAY_IN_MILLIS
        if (newDateInMillis >= 0)
            selectedDate.value = Date(newDateInMillis)
    }

    fun refreshRecommendationsInfo() {
        viewModelScope.launch {
            _isProgressShow.value = true
            try {
                withContext(Dispatchers.IO) {
                    recommendationsRepository.refreshRecommendationInfo(
                        Credentials.basic(
                            UserPreferences.getPhone()!!,
                            UserPreferences.getPassword()!!
                        )
                    )
                    updateOperationDate()
                }
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
        private const val ONE_DAY_IN_MILLIS = 86400000
    }
}
