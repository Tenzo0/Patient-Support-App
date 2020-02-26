package ru.poas.patientassistant.client.ui.login

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import ru.poas.patientassistant.client.R
import ru.poas.patientassistant.client.preferences.UserPreferences


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        UserPreferences.init(applicationContext)
        setContentView(R.layout.activity_login)
    }
}
