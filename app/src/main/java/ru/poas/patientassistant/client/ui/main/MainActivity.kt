package ru.poas.patientassistant.client.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import ru.poas.patientassistant.client.R
import ru.poas.patientassistant.client.databinding.ActivityMainBinding
import ru.poas.patientassistant.client.preferences.UserPreferences
import ru.poas.patientassistant.client.ui.login.LoginActivity


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        drawerLayout = binding.drawerLayout

        //Set action bar which changes own menu depending on fragment
        setSupportActionBar(binding.toolbar)

        //Set navigation drawer with 4 top level destinations
        val navController = findNavController(R.id.main_nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.recommendationsFragment,
            R.id.profileFragment,
            R.id.glossaryFragment,
            R.id.medicinesFragment)
            .setDrawerLayout(drawerLayout)
            .build()
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        NavigationUI.setupWithNavController(binding.navView, navController)

        //Setup exit button listener in the navigation drawer
        binding.navView.setNavigationItemSelectedListener(
            object : NavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when(item.itemId) {
                    R.id.exit -> {
                        //Clear all information about user
                        UserPreferences.clear()
                        //Start LoginActivity
                        startActivity(Intent(applicationContext, LoginActivity::class.java))
                        //Finish session with this activity
                        finish()
                    }
                }
                return true //display the item as the selected item
            }
        })
    }
}
