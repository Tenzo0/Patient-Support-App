/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.poas.patientassistant.client.patient.ui.recommendations

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.launch
import okhttp3.Credentials
import ru.poas.patientassistant.client.login.vo.User
import ru.poas.patientassistant.client.patient.db.recommendations.RecommendationsDatabase
import ru.poas.patientassistant.client.preferences.UserPreferences
import ru.poas.patientassistant.client.patient.repository.RecommendationsRepository
import ru.poas.patientassistant.client.viewmodel.BaseViewModel
import ru.poas.patientassistant.client.patient.vo.Recommendation
import ru.poas.patientassistant.client.patient.vo.RecommendationConfirmKey
import ru.poas.patientassistant.client.utils.DateUtils.databaseSimpleDateFormat
import timber.log.Timber
import java.security.spec.ECField
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class RecommendationsViewModel @Inject constructor(
    private val context: Context,
    private val recommendationsRepository: RecommendationsRepository
) : BaseViewModel() {

    val recommendationsList: LiveData<List<Recommendation>> = recommendationsRepository.recommendationsList
    val isRecommendationConfirmed: LiveData<Boolean> = recommendationsRepository.isRecommendationConfirmed

    private var _operationDate: Calendar = Calendar.getInstance()
    val operationDate: Calendar
        get() = _operationDate

    private var _selectedDate: Calendar = Calendar.getInstance()
    val selectedDate: Calendar
        get() = _selectedDate

    init {
        UserPreferences.init(context)
        _operationDate.timeInMillis = UserPreferences.getOperationDate()?.let {
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
                _operationDate.timeInMillis = UserPreferences.getOperationDate()?.let {
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

                UserPreferences.getOperationDate()?.let {
                    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                    _operationDate.time = DATABASE_DATE_FORMAT.parse(it)
                }

                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false
            } catch (e: Exception) {
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
