package ru.poas.patientassistant.client.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import ru.poas.patientassistant.client.R
import ru.poas.patientassistant.client.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(
             this, R.layout.activity_main)

        drawerLayout = binding.drawerLayout

        //Set navigation drawer
        val navController = findNavController(R.id.main_nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.recommendationsFragment,
                R.id.profileFragment,
                R.id.glossaryFragment,
                R.id.medicinesFragment),
                drawerLayout)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        NavigationUI.setupWithNavController(binding.navView, navController)
    }

}
