package ru.poas.patientassistant.client.ui.main.recommendations

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.launch
import okhttp3.Credentials
import ru.poas.patientassistant.client.db.recommendations.RecommendationsDatabase
import ru.poas.patientassistant.client.preferences.UserPreferences
import ru.poas.patientassistant.client.repository.RecommendationsRepository
import ru.poas.patientassistant.client.viewmodel.BaseViewModel
import ru.poas.patientassistant.client.vo.Recommendation
import ru.poas.patientassistant.client.vo.UserRecommendation

class RecommendationsViewModel(private val dataSource: RecommendationsDatabase) : BaseViewModel() {
    private val repository: RecommendationsRepository = RecommendationsRepository(dataSource)

    val recommendationsList: LiveData<List<Recommendation>> = repository.recommendationsList

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
