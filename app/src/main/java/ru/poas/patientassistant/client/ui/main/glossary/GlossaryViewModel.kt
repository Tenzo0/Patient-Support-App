package ru.poas.patientassistant.client.ui.main.glossary

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import okhttp3.Credentials
import ru.poas.patientassistant.client.db.glossary.GlossaryDatabase
import ru.poas.patientassistant.client.preferences.UserPreferences
import ru.poas.patientassistant.client.repository.GlossaryRepository
import ru.poas.patientassistant.client.viewmodel.BaseViewModel
import ru.poas.patientassistant.client.vo.GlossaryItem

class GlossaryViewModel(
    private val dataSource: GlossaryDatabase
) : BaseViewModel() {

    private val repository: GlossaryRepository = GlossaryRepository(dataSource)

    val glossaryItem: LiveData<List<GlossaryItem>> = repository.glossaryItems

    /**
     * Cancel all coroutines when the ViewModel is cleared
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun refreshGlossary() {
        viewModelScope.launch {
            _isProgressShow.value = true
            try {
                repository.refreshGlossary(
                    Credentials.basic(
                        UserPreferences.getPhone()!!,
                        UserPreferences.getPassword()!!
                    )
                )
                Log.i("tag3", repository.glossaryItems.value?.first()?.title)
                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false
            } catch (e: Exception) {
                Log.i("tag2", e.toString())
                _eventNetworkError.value = true
            }
            _isProgressShow.value = false
        }
    }

    /**
     * Factory for constructing [GlossaryViewModel] with parameters
     */
    class GlossaryViewModelFactory(
        private val dataSource: GlossaryDatabase
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GlossaryViewModel::class.java)) {
                return GlossaryViewModel(dataSource) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }

    }

    /**
     * Variable that tells the Fragment to navigate to a specific [GlossaryDetailsFragment]
     */
    private val _navigateToDefinitionDetails = MutableLiveData<GlossaryItem>()

    /**
     * If this is non-null, immediately navigate to [GlossaryDetailsFragment]
     */
    val navigateToDefinitionDetails
        get() = _navigateToDefinitionDetails

    /**
     * Call this immediately after navigating to [GlossaryDetailsFragment]
     *
     * It will clear the toast request, so if the user rotates phone it
     * won't show a duplicate toast.
     */
    fun onDefinitionNavigated() {
        _navigateToDefinitionDetails.value = null
    }

    /**
     * Navigation for the DefinitionDetails Fragment.
     */
    fun onDefinitionClicked(glossaryItem: GlossaryItem) {
        _navigateToDefinitionDetails.value = glossaryItem
    }


}