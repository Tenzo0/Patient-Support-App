/*
 * Copyright (c) Alexey Barykin 2020.
 */

package ru.poas.patientassistant.client.patient.ui.drugs

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.launch
import okhttp3.Credentials
import retrofit2.HttpException
import ru.poas.patientassistant.client.patient.domain.DrugItem
import ru.poas.patientassistant.client.patient.repository.DrugsRepository
import ru.poas.patientassistant.client.preferences.DatePreferences
import ru.poas.patientassistant.client.preferences.UserPreferences
import ru.poas.patientassistant.client.utils.DateUtils.databaseSimpleDateFormat
import ru.poas.patientassistant.client.utils.DateUtils.syncDateWithServer
import ru.poas.patientassistant.client.viewmodel.BaseViewModel
import java.util.*
import javax.inject.Inject

class DrugsViewModel @Inject constructor(
    private val drugsRepository: DrugsRepository
) : BaseViewModel() {

    private var _selectedDate = Calendar.getInstance()
    val selectedDate: Calendar
        get() = _selectedDate

    private var _currentServerDate: String? = DatePreferences.getActualServerDate()
    val currentServerDate: String?
        get() = _currentServerDate

    val drugsList = drugsRepository.drugsList
    private var _drugsListForSelectedDate = MutableLiveData<List<DrugItem>>()
    val drugsListForSelectedDate: LiveData<List<DrugItem>>
        get() = _drugsListForSelectedDate

    fun updateDrugsListForSelectedDate() {
        _drugsListForSelectedDate.value = drugsList.value?.filter {
            it.dateOfDrugReception == databaseSimpleDateFormat.format(selectedDate.time)
        }
    }

    init {
        _drugsListForSelectedDate.value = emptyList()
    }

    fun incSelectedDate() {
        _selectedDate.add(Calendar.DATE, 1)
        updateDrugsListForSelectedDate()
    }

    fun decSelectedDate() {
        _selectedDate.add(Calendar.DATE, -1)
        updateDrugsListForSelectedDate()
    }

    fun updateSelectedDate(year: Int, month: Int, day: Int) {
        _selectedDate.set(year, month, day)
        updateDrugsListForSelectedDate()
    }

    fun refreshDrugs(context: Context) {
        viewModelScope.launch {
            _isProgressShow.value = true
            try {
                drugsRepository.refreshDrugs(
                    Credentials.basic(
                        UserPreferences.getPhone()!!,
                        UserPreferences.getPassword()!!
                    )
                )
                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false
                syncDateWithServer(context)
                _currentServerDate = DatePreferences.getActualServerDate()
            } catch (e: HttpException) {
                _eventNetworkError.value = true
            }
            _isProgressShow.value = false
        }
    }

    fun confirmDrug(id: Long) {
        viewModelScope.launch {
            _isProgressShow.value = true
            try {
                drugsRepository.confirmDrug(
                    Credentials.basic(
                        UserPreferences.getPhone()!!,
                        UserPreferences.getPassword()!!
                    ),
                    id
                )
                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false
            } catch (e: Exception) {
                _eventNetworkError.value = true
            }
            _isProgressShow.value = false
        }
    }
}