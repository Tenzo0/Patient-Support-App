package ru.poas.patientassistant.client.ui.login

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.launch
import okhttp3.Credentials
import ru.poas.patientassistant.client.api.UserNetwork
import ru.poas.patientassistant.client.preferences.UserPreferences
import ru.poas.patientassistant.client.viewmodel.BaseViewModel
import ru.poas.patientassistant.client.vo.Role
import timber.log.Timber

class LoginViewModel : BaseViewModel() {

    private var _roles = MutableLiveData<List<Role>>(emptyList())

    val roles: LiveData<List<Role>>
        get() = _roles

    var chosenRole: Role? = null

    enum class LoginType {
        UNAUTHORIZED, FIRSTLY_AUTHORIZED, AUTHORIZED
    }

    private var _isAuthorized = MutableLiveData<LoginType>()

    val isAuthorized: LiveData<LoginType>
        get() = _isAuthorized

    init {
        _isAuthorized.value =
            LoginType.UNAUTHORIZED

        if (UserPreferences.getPhone() != null) {
            authUser(UserPreferences.getPhone()!!, UserPreferences.getPassword()!!)
        }
    }

    private var _isPasswordUpdated = MutableLiveData<Boolean>()

    val isPasswordUpdated: LiveData<Boolean>
        get() = _isPasswordUpdated

    fun authUser(phone: String, password: String) {
        viewModelScope.launch {
            //Show Progress bar
            _isProgressShow.value = true

            //Request for auth user
            try {
                val user =
                    UserNetwork.userService.login(Credentials.basic(phone, password))
                       .body()

                _roles.value = user!!.roles
                UserPreferences.saveUser(user, password)

                if(UserPreferences.isTemporaryPassword()) {
                    _isAuthorized.value = LoginType.FIRSTLY_AUTHORIZED
                    _isPasswordUpdated.value = false
                }
                else {
                    _isAuthorized.value = LoginType.AUTHORIZED
                    _isPasswordUpdated.value = true
                }

                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false
            } catch (e: Exception) {
                Timber.e(e)
                UserPreferences.clear()
                _roles.value = emptyList()
                _isAuthorized.value = LoginType.UNAUTHORIZED
                _eventNetworkError.value = true
            }

            // Hide Progress Bar
            _isProgressShow.value = false
        }
    }

    fun updatePassword(password: String) {
        viewModelScope.launch {
            //Show Progress bar
            _isProgressShow.value = true

            //Request for changing password
            try {
                val response =
                    UserNetwork.userService.updatePassword(Credentials.basic(
                        UserPreferences.getPhone(), UserPreferences.getPassword()), password)

                if (response.isSuccessful) {
                    UserPreferences.savePassword(password)
                    _isAuthorized.value = LoginType.AUTHORIZED
                    _eventNetworkError.value = false
                    _isNetworkErrorShown.value = false
                    _isPasswordUpdated.value = true
                }
            } catch (e: Exception) {
                Timber.e(e)
                _eventNetworkError.value = true
            }

            // Hide Progress Bar
            _isProgressShow.value = false
        }
    }

    fun chooseRole(role: Role) {
        UserPreferences.saveRole(role)
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