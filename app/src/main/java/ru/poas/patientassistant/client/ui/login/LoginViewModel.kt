package ru.poas.patientassistant.client.ui.login

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.launch
import okhttp3.Credentials
import ru.poas.patientassistant.client.api.UserNetwork
import ru.poas.patientassistant.client.preferences.UserPreferences
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
        _isAuthed.value =
            LoginType.NOT_AUTHED

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
        viewModelScope.launch {
            //Show Progress bar
            _isProgressShow.value = true

            //Request for auth user
            try {
                val user =
                    UserNetwork.userService.login(Credentials.basic(phone, password))
                       .body()

                // Save the user to preferences
                //TODO replace roles[0]!!!
                //If roles[0] > 1 then ask the user what roles need to be chosen
                UserPreferences.saveUser(user!!, password, 0)

                if(UserPreferences.isTemporaryPassword())
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
            }

            // Hide Progress Bar
            _isProgressShow.value = false
        }
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