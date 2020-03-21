package ru.poas.patientassistant.client.patient.ui.drugs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.launch
import okhttp3.Credentials
import ru.poas.patientassistant.client.patient.repository.DrugItem
import ru.poas.patientassistant.client.patient.repository.DrugsRepository
import ru.poas.patientassistant.client.preferences.UserPreferences
import ru.poas.patientassistant.client.viewmodel.BaseViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class DrugsViewModel @Inject constructor(
    private val drugsRepository: DrugsRepository
) : BaseViewModel() {

    private var _selectedDate = Calendar.getInstance()
    val selectedDate: Calendar
        get() = _selectedDate

    val drugsList = drugsRepository.drugsList
    private var _drugsListForSelectedDate = MutableLiveData<List<DrugItem>>()
    val drugsListForSelectedDate: LiveData<List<DrugItem>>
        get() = _drugsListForSelectedDate

    fun updateDrugsListForSelectedDate() {
        _drugsListForSelectedDate.value = drugsList.value?.filter {
            it.dateOfMedicationReception == DATABASE_DATE_FORMAT.format(selectedDate.time)
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

    fun refreshDrugs() {
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
            } catch (e: Exception) {
                _eventNetworkError.value = true
            }
            _isProgressShow.value = false
        }
    }

    companion object {
        val DATABASE_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale("ru", "RU"))
    }
}