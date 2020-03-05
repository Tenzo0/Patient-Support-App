package ru.poas.patientassistant.client.ui.main.recommendations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.launch
import okhttp3.Credentials
import ru.poas.patientassistant.client.db.recommendations.RecommendationsDatabase
import ru.poas.patientassistant.client.preferences.UserPreferences
import ru.poas.patientassistant.client.repository.RecommendationsRepository
import ru.poas.patientassistant.client.viewmodel.BaseViewModel
import ru.poas.patientassistant.client.vo.Recommendation
import ru.poas.patientassistant.client.vo.RecommendationConfirmKey
import timber.log.Timber
import java.util.*

class RecommendationsViewModel(private val dataSource: RecommendationsDatabase) : BaseViewModel() {
    private val repository: RecommendationsRepository = RecommendationsRepository(dataSource)

    val recommendationsList: LiveData<List<Recommendation>> = repository.recommendationsList
    val operationDate: LiveData<Calendar> = repository.operationDate
    private var _isRecommendationConfirmed = MutableLiveData<Boolean>()
    val isRecommendationConfirmed: LiveData<Boolean>
        get() = _isRecommendationConfirmed

    private var _selectedDate = MutableLiveData<Calendar>()
    val selectedDate: LiveData<Calendar>
        get() = _selectedDate

    init {
        _selectedDate.value = Calendar.getInstance()
        _isRecommendationConfirmed.value = false
    }

    fun confirmRecommendation() {
        viewModelScope.launch {
            try {
                repository.confirmRecommendation(
                    Credentials.basic(
                        UserPreferences.getPhone()!!,
                        UserPreferences.getPassword()!!
                    ),
                    RecommendationConfirmKey(
                        UserPreferences.getId(),
                        UserPreferences.getRecommendationId()
                    )
                )
                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false
                _isRecommendationConfirmed.value = true
            }
            catch (e: Exception) {
                Timber.e(e)
                _eventNetworkError.value = true
                _isNetworkErrorShown.value = true
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

    fun getSelectedDate() {
        val currentRecommendationCalendar = Calendar.getInstance()
        val day = currentRecommendationCalendar.get(Calendar.DAY_OF_MONTH)
        val month = currentRecommendationCalendar.get(Calendar.MONTH)
        val year = currentRecommendationCalendar.get(Calendar.YEAR)
    }

    fun refreshRecommendationsInfo() {
        viewModelScope.launch {
            _isProgressShow.value = true
            try {
                repository.refreshRecommendationInfo(
                    Credentials.basic(
                        UserPreferences.getPhone()!!,
                        UserPreferences.getPassword()!!
                    )
                )

                repository.refreshRecommendations(
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

    /**
     * Factory for constructing [RecommendationsViewModel] with parameters
     */
    class RecommendationsViewModelFactory(private val dataSource: RecommendationsDatabase)
        : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RecommendationsViewModel::class.java)) {
                return RecommendationsViewModel(dataSource) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }


}
