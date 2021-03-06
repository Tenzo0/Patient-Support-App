/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.alexey.patientassistant.client.preferences

import android.content.Context
import android.content.SharedPreferences
import ru.alexey.patientassistant.client.patient.vo.Role
import ru.alexey.patientassistant.client.login.vo.User
import ru.alexey.patientassistant.client.patient.vo.UserRecommendation

object UserPreferences {
    private lateinit var preferences: SharedPreferences

    private const val PREFERENCES_NAME = "user_preferences"

    private const val PREFERENCES_USER_FIRST_NAME = "user_first_name"
    private const val PREFERENCES_USER_ID = "user_id"
    private const val PREFERENCES_USER_IS_TEMPORARY_PASSWORD = "user_is_temporary_password"
    private const val PREFERENCES_USER_LAST_NAME = "user_last_name"
    private const val PREFERENCES_USER_PASSWORD = "user_password"
    private const val PREFERENCES_USER_PHONE = "user_phone"
    private const val PREFERENCES_USER_ROLE_DESCRIPTION = "user_role_description"
    private const val PREFERENCES_USER_ROLE_ID = "user_role_id"
    private const val PREFERENCES_USER_ROLE_NAME = "user_role_name"
    private const val PREFERENCES_USER_OPERATION_DATE = "user_operation_date"
    private const val PREFERENCES_USER_RECOMMENDATION_ID = "user_recommendation_id"
    private const val PREFERENCES_USER_RELATED_DOCTOR_NAME = "user_related_doctor_name"

    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    fun saveUser(user: User, password: String) {
        val editor: SharedPreferences.Editor = preferences.edit()
        with(editor) {
            putString(PREFERENCES_USER_FIRST_NAME, user.firstName)
            putLong(PREFERENCES_USER_ID, user.id)
            putBoolean(PREFERENCES_USER_IS_TEMPORARY_PASSWORD, user.isTemporaryPassword)
            putString(PREFERENCES_USER_LAST_NAME, user.lastName)
            putString(PREFERENCES_USER_PASSWORD, password)
            putString(PREFERENCES_USER_PHONE, user.phone)
            putLong(PREFERENCES_USER_RECOMMENDATION_ID, 0)
            apply()
        }
    }

    fun saveRole(role: Role) {
        val editor: SharedPreferences.Editor = preferences.edit()
        with(editor) {
            putString(PREFERENCES_USER_ROLE_DESCRIPTION, role.description)
            putLong(PREFERENCES_USER_ROLE_ID, role.id)
            putString(PREFERENCES_USER_ROLE_NAME, role.name)
            apply()
        }
    }

    fun saveUserRecommendation(userRecommendation: UserRecommendation) {
        val editor: SharedPreferences.Editor = preferences.edit()
        with(editor) {
            putString(PREFERENCES_USER_OPERATION_DATE, userRecommendation.operationDate)
            putLong(PREFERENCES_USER_RECOMMENDATION_ID, userRecommendation.recommendationId)
            apply()
        }
    }

    fun savePassword(password: String) {
        val editor: SharedPreferences.Editor = preferences.edit()
        with(editor) {
            putString(PREFERENCES_USER_PASSWORD, password)
            apply()
        }
    }

    fun getPhone() = preferences.getString(PREFERENCES_USER_PHONE, null)

    fun getPassword() = preferences.getString(PREFERENCES_USER_PASSWORD, null)

    fun isTemporaryPassword() = preferences.getBoolean(PREFERENCES_USER_IS_TEMPORARY_PASSWORD, false)

    fun getRelatedDoctorName() = preferences.getString(PREFERENCES_USER_RELATED_DOCTOR_NAME, "")

    fun getUserFullName() = preferences.getString(PREFERENCES_USER_FIRST_NAME, "") +
            " " + preferences.getString(PREFERENCES_USER_LAST_NAME, "")

    fun getId() = preferences.getLong(PREFERENCES_USER_ID, Long.MAX_VALUE)

    fun getRoleId() = preferences.getLong(PREFERENCES_USER_ROLE_ID, Long.MAX_VALUE)

    fun clear() {
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.clear()
        editor.apply()
    }

    fun getRecommendationId() = preferences.getLong(PREFERENCES_USER_RECOMMENDATION_ID, Long.MAX_VALUE)

    fun getOperationDate() = preferences.getString(PREFERENCES_USER_OPERATION_DATE, null)
}