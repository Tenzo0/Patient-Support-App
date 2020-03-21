package ru.poas.patientassistant.client.patient.ui.recommendations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.launch
import okhttp3.Credentials
import ru.poas.patientassistant.client.patient.db.recommendations.RecommendationsDatabase
import ru.poas.patientassistant.client.preferences.UserPreferences
import ru.poas.patientassistant.client.patient.repository.RecommendationsRepository
import ru.poas.patientassistant.client.viewmodel.BaseViewModel
import ru.poas.patientassistant.client.patient.vo.Recommendation
import ru.poas.patientassistant.client.patient.vo.RecommendationConfirmKey
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class RecommendationsViewModel @Inject constructor(
    private val recommendationsRepository: RecommendationsRepository
) : BaseViewModel() {

    val recommendationsList: LiveData<List<Recommendation>> = recommendationsRepository.recommendationsList
    val operationDate: LiveData<Calendar> = recommendationsRepository.operationDate
    val isRecommendationConfirmed: LiveData<Boolean> = recommendationsRepository.isRecommendationConfirmed

    private var _selectedDate = MutableLiveData<Calendar>()
    val selectedDate: LiveData<Calendar>
        get() = _selectedDate

    init {
        _selectedDate.value = Calendar.getInstance()
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
            }
            catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    fun updateSelectedDate(year: Int, month: Int, day: Int) {
        _selectedDate.value!!.set(year, month, day)
        Timber.i("selected date is changed: ${_selectedDate.value!!.get(Calendar.DAY_OF_MONTH)}")
    }

    fun incSelectedDate() {
        _selectedDate.value!!.add(Calendar.DATE, 1)
    }

    fun decSelectedDate() {
        _selectedDate.value!!.add(Calendar.DATE, -1)
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

                recommendationsRepository.refreshRecommendations(
                    Credentials.basic(
                        UserPreferences.getPhone()!!,
                        UserPreferences.getPassword()!!
                    ),
                    UserPreferences.getRecommendationId()
                )

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
}
