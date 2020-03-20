package ru.poas.patientassistant.client.patient.ui.glossary

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import okhttp3.Credentials
import ru.poas.patientassistant.client.preferences.UserPreferences
import ru.poas.patientassistant.client.patient.repository.GlossaryRepository
import ru.poas.patientassistant.client.viewmodel.BaseViewModel
import ru.poas.patientassistant.client.patient.vo.GlossaryItem
import javax.inject.Inject

class GlossaryViewModel @Inject constructor(
    private val glossaryRepository: GlossaryRepository
) : BaseViewModel() {

    val glossaryItemsList: LiveData<List<GlossaryItem>> = glossaryRepository.glossaryItemsList

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
                glossaryRepository.refreshGlossary(
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