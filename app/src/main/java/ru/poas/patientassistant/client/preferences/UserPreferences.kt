package ru.poas.patientassistant.client.preferences

import android.content.Context
import android.content.SharedPreferences

object UserPreferences {
    private lateinit var preferences: SharedPreferences

    private const val PREFERENCES_NAME = "user_preferences"

    private const val PREFERENCES_USER_PHONE = "user_phone"
    private const val PREFERENCES_USER_PASSWORD = "user_password"
    private const val PREFERENCES_USER_ID = "user_id"
    private const val PREFERENCES_USER_ROLE_ID = "user_role_id"
    private const val PREFERENCES_USER_FIRST_NAME = "user_first_name"
    private const val PREFERENCES_USER_LAST_NAME = "user_last_name"
    private const val PREFERENCES_USER_GROWTH = "user_growth"
    private const val PREFERENCES_USER_WEIGHT = "user_weight"
    private const val PREFERENCES_USER_RECOMMENDATION_ID = "user_recommendation_id"
    private const val PREFERENCES_USER_PATIENT_ID = "user_patient_id"
    private const val PREFERENCES_USER_DATE_OF_BIRTH = "user_date_of_birth"
    private const val PREFERENCES_USER_GENDER = "user_gender"
    private const val PREFERENCES_USER_DOCTOR_ID = "user_doctor_id"
    private const val PREFERENCES_USER_DOCTOR_FIRST_NAME = "user_doctor_first_name"
    private const val PREFERENCES_USER_DOCTOR_LAST_NAME = "user_doctor_last_name"

    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    fun saveUser() {

    }

    fun setUserRecommendationId(id: Long) {
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putLong(PREFERENCES_USER_RECOMMENDATION_ID, id)
        editor.apply()
    }

    fun getUserRecommendationId() =
        preferences.getLong(PREFERENCES_USER_RECOMMENDATION_ID, Long.MAX_VALUE)

    fun getUserDomainModel() {}

    fun getPhone() = preferences.getString(PREFERENCES_USER_PHONE, null)

    fun getPassword() = preferences.getString(PREFERENCES_USER_PASSWORD, null)

    fun getId() = preferences.getLong(PREFERENCES_USER_ID, Long.MAX_VALUE)

    fun getRoleId() = preferences.getLong(PREFERENCES_USER_ROLE_ID, Long.MAX_VALUE)

    fun getPatientId() = preferences.getString(PREFERENCES_USER_PATIENT_ID, "")!!.toLong()

    fun saveDoctorInfo() {

    }

    fun getDoctorInfo() {}

    fun clear() {
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.clear()
        editor.apply()
    }
}