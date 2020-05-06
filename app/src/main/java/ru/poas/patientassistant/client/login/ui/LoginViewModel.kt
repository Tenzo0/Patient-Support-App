/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.poas.patientassistant.client.login.ui

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.launch
import okhttp3.Credentials
import ru.poas.patientassistant.client.login.api.UserNetwork
import ru.poas.patientassistant.client.preferences.UserPreferences
import ru.poas.patientassistant.client.viewmodel.BaseViewModel
import ru.poas.patientassistant.client.patient.vo.Role
import timber.log.Timber

class LoginViewModel : BaseViewModel() {

    private var _roles = emptyList<Role>()

    val roles: List<Role>
        get() = _roles

    var chosenRole: Role? = null

    enum class LoginType {
        UNAUTHORIZED, FIRSTLY_AUTHORIZED, AUTHORIZED
    }

    private var _isIncorrectLoginOrPassword = false
    val isIncorrectLoginOrPassword
        get() = _isIncorrectLoginOrPassword

    private var _isAuthorized = MutableLiveData<LoginType>()

    val isAuthorized: LiveData<LoginType>
        get() = _isAuthorized

    private var _isTemporaryPassword = MutableLiveData<Boolean>()
    val isTemporaryPassword: LiveData<Boolean>
        get() = _isTemporaryPassword

    init {
        _isAuthorized.value =
            LoginType.UNAUTHORIZED

        _isTemporaryPassword.value = UserPreferences.isTemporaryPassword()

        if (UserPreferences.getPhone() != null) {
            authUser(UserPreferences.getPhone()!!, UserPreferences.getPassword()!!)
        }
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

                user?.roles?.let {
                    _roles = it
                }
                UserPreferences.saveUser(user!!, password)

                if(UserPreferences.isTemporaryPassword()) {
                    _isAuthorized.value =
                        LoginType.FIRSTLY_AUTHORIZED
                    _isTemporaryPassword.value = true
                }
                else {
                    _isAuthorized.value =
                        LoginType.AUTHORIZED
                    _isTemporaryPassword.value = false
                }

                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false
            } catch (e: KotlinNullPointerException) {
                Timber.e(e)
                _isAuthorized.value =
                    LoginType.UNAUTHORIZED
                _eventNetworkError.value = true
                _isIncorrectLoginOrPassword = true
            }
            catch (e: Exception) {
                Timber.e(e)
                UserPreferences.clear()
                _roles = emptyList<Role>()
                _isAuthorized.value =
                    LoginType.UNAUTHORIZED
                _eventNetworkError.value = true
            }
            _isIncorrectLoginOrPassword = false
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
                    _isAuthorized.value =
                        LoginType.AUTHORIZED
                    _eventNetworkError.value = false
                    _isNetworkErrorShown.value = false
                    _isTemporaryPassword.value = false
                }
                else
                    throw Exception("Response is not correct!")
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