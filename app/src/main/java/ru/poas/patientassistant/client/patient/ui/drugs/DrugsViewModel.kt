package ru.poas.patientassistant.client.patient.ui.drugs

import kotlinx.coroutines.launch
import okhttp3.Credentials
import ru.poas.patientassistant.client.patient.repository.DrugsRepository
import ru.poas.patientassistant.client.preferences.UserPreferences
import ru.poas.patientassistant.client.viewmodel.BaseViewModel
import javax.inject.Inject

class DrugsViewModel @Inject constructor(
    private val drugsRepository: DrugsRepository
) : BaseViewModel() {

    val drugsList = drugsRepository.drugsList

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
}