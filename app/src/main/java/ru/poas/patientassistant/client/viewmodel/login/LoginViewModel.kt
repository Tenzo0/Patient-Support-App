package ru.poas.patientassistant.client.viewmodel.login

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.launch
import ru.poas.patientassistant.client.viewmodel.BaseViewModel

class LoginViewModel : BaseViewModel() {

    enum class LoginType {
        NOT_AUTHED, FIRTSLY_AUTHED, AUTHED
    }

    /**
     * Flag to display state of user auth. This is private to avoid exposing a
     * way to set this value to observers.
     */
    private var _isAuthed = MutableLiveData<LoginType>()

    /**
     * Flag to display state of user auth.
     */
    val isAuthed: LiveData<LoginType>
        get() = _isAuthed

    init {
        _eventNetworkError.value = false
        _isNetworkErrorShown.value = false
        _isProgressShow.value = false
        _isAuthed.value = LoginType.NOT_AUTHED

        //TODO if (UserPreferences.getPhone() != null) {
        //    authUser(UserPreferences.getPhone()!!, UserPreferences.getPassword()!!)
        //}
    }

    /**
     * Cancel all coroutines when the ViewModel is cleared
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun authUser(phone: String, password: String) {
        /* TODO
        viewModelScope.launch {
            //Show Progress bar
            _isProgressShow.value = true

            var prevPhone = ""

            if (UserPreferences.getPhone() != null)
                prevPhone = UserPreferences.getPhone()!!

            //Request for auth user
            try {
                val user =
                    UserNetwork.userService.login(getTokenFromPhoneAndPassword(phone, password))
                        .await().body()

                // Save the user to preferences
                UserPreferences.saveUser(user!!, password)

                Timber.i(user.toString())

                if (prevPhone == phone)
                    _isAuthed.value = LoginType.AUTHED
                else
                    _isAuthed.value = LoginType.FIRTSLY_AUTHED

                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false
            } catch (e: Exception) {

                // Clear the user preferences
                UserPreferences.clear()
                _isAuthed.value = LoginType.NOT_AUTHED
                _eventNetworkError.value = true
                Timber.e(e)
            }

            // Hide Progress Bar
            _isProgressShow.value = false
        }*/

        _eventNetworkError.value = false
        _isNetworkErrorShown.value = false
        _isAuthed.value = LoginType.FIRTSLY_AUTHED //TODO delete this lines
    }

    /**
     * Factory for constructing LoginViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel() as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}