package ru.poas.patientassistant.client.preferences

import android.content.Context
import android.content.SharedPreferences
import ru.poas.patientassistant.client.vo.User

object UserPreferences {
    private lateinit var preferences: SharedPreferences

    private const val PREFERENCES_NAME = "user_preferences"

    private const val PREFERENCES_USER_FIRST_NAME = "user_first_name"
    private const val PREFERENCES_USER_ID = "user_id"
    private const val PREFERENCES_USER_IS_TEMPORARY_PASSWORD = "user_is_temporary_password"
    private const val PREFERENCES_USER_LAST_NAME = "user_last_name"
    private const val PREFERENCES_USER_PASSWORD = "user_password"
    private const val PREFERENCES_USER_PHONE = "user_phone"
    private const val PREFERENCES_USER_ROLE_DESCRIPTION = "user_role_desctiption"
    private const val PREFERENCES_USER_ROLE_ID = "user_role_id"
    private const val PREFERENCES_USER_ROLE_NAME = "user_role_name"

    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    fun saveUser(user: User, password: String, chosenRole: Int) {
        val editor: SharedPreferences.Editor = preferences.edit()
        with(editor) {
            putString(PREFERENCES_USER_FIRST_NAME, user.firstName)
            putLong(PREFERENCES_USER_ID, user.id)
            putBoolean(PREFERENCES_USER_IS_TEMPORARY_PASSWORD, user.isTemporaryPassword)
            putString(PREFERENCES_USER_LAST_NAME, user.lastName)
            putString(PREFERENCES_USER_PASSWORD, password)
            putString(PREFERENCES_USER_PHONE, user.phone)
            putString(PREFERENCES_USER_ROLE_DESCRIPTION, user.roles[chosenRole].description)
            putLong(PREFERENCES_USER_ROLE_ID, user.roles[chosenRole].id)
            putString(PREFERENCES_USER_ROLE_NAME, user.roles[chosenRole].name)
            apply()
        }
    }

    fun getPhone() = preferences.getString(PREFERENCES_USER_PHONE, null)

    fun getPassword() = preferences.getString(PREFERENCES_USER_PASSWORD, null)

    fun getId() = preferences.getLong(PREFERENCES_USER_ID, Long.MAX_VALUE)

    fun getRoleId() = preferences.getLong(PREFERENCES_USER_ROLE_ID, Long.MAX_VALUE)

    fun clear() {
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.clear()
        editor.apply()
    }
}