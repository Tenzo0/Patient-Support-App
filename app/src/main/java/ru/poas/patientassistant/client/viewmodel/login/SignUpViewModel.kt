package ru.poas.patientassistant.client.viewmodel.login

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.poas.patientassistant.client.viewmodel.BaseViewModel

class SignUpViewModel : BaseViewModel() {
    /**
     * Flag to display state of User SignUp. This is private to avoid exposing a
     * way to set this value to observers.
     */
    private var _isSignUp = MutableLiveData<Boolean>()

    /**
     * Flag to display state of User SignUp.
     */
    val isSignUp: LiveData<Boolean>
        get() = _isSignUp


    /**
     * Flag to display state of exists phone. This is private to avoid exposing a
     * way to set this value to observers.
     */
    private var _phoneExists = MutableLiveData<Boolean>()

    /**
     * Flag to display state of exists phone.
     */
    val phoneExists: LiveData<Boolean>
        get() = _phoneExists


    init {
        _eventNetworkError.value = false
        _isNetworkErrorShown.value = false
        _isProgressShow.value = false
        _isSignUp.value = false
        _phoneExists.value = false
    }

    /**
     * Cancel all coroutines when the ViewModel is cleared
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    //Sign up with database
    //TODO(fun signUp(newUser: SignUpUserDto, password: String))
    fun signUp() {
        _phoneExists.value = true
        _isSignUp.value = true
    }

    /**
     * Factory for constructing LoginViewModel with parameter
     */
    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SignUpViewModel() as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}
