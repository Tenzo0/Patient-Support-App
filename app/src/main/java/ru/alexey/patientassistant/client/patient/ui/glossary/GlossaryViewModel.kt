/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.alexey.patientassistant.client.patient.ui.glossary

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import okhttp3.Credentials
import ru.alexey.patientassistant.client.preferences.UserPreferences
import ru.alexey.patientassistant.client.patient.repository.GlossaryRepository
import ru.alexey.patientassistant.client.viewmodel.BaseViewModel
import ru.alexey.patientassistant.client.patient.vo.GlossaryItem
import javax.inject.Inject

class GlossaryViewModel @Inject constructor(
    private val glossaryRepository: GlossaryRepository
) : BaseViewModel() {

    val glossaryItemsList: LiveData<List<GlossaryItem>> = glossaryRepository.glossaryItemsList

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
     * Variable that tells the Fragment to navigate to the [GlossaryDetailsFragment]
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
     * Navigation for the [GlossaryDetailsFragment].
     */
    fun onDefinitionClicked(glossaryItem: GlossaryItem) {
        _navigateToDefinitionDetails.value = glossaryItem
    }


}