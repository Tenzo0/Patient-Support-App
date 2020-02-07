package ru.poas.patientassistant.client.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

abstract class BaseViewModel : ViewModel() {
    /**
     * This is the job for all coroutines started by this ViewModel.
     * Cancelling this job will cancel all coroutines started by this ViewModel.
     */
    protected val viewModelJob = SupervisorJob()

    /** A [CoroutineScope] keeps track of all coroutines started by this ViewModel. */
    protected val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    /**
     * Event triggered for network error. This is private to avoid exposing a
     * way to set this value to observers.
     */
    protected var _eventNetworkError = MutableLiveData<Boolean>()

    /**
     * Event triggered for network error. Views should use this to get access
     * to the data.
     */
    val eventNetworkError: LiveData<Boolean>
        get() = _eventNetworkError

    /**
     * Flag to display the error message. This is private to avoid exposing a
     * way to set this value to observers.
     */
    protected var _isNetworkErrorShown = MutableLiveData<Boolean>()

    /**
     * Flag to display the error message. Views should use this to get access
     * to the data.
     */
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown


    /**
     * Flag to display the progress bar. This is private to avoid exposing a
     * way to set this value to observers.
     */
    protected var _isProgressShow = MutableLiveData<Boolean>()

    /**
     * Flag to display the progress bar.
     */
    val isProgressShow: LiveData<Boolean>
        get() = _isProgressShow

    /**
     * Resets the network error flag.
     */
    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }

    /**
     * Cancel all coroutines when the ViewModel is cleared
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}