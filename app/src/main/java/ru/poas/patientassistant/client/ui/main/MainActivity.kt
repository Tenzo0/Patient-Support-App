package ru.poas.patientassistant.client.ui.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*
import ru.poas.patientassistant.client.R
import ru.poas.patientassistant.client.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        drawerLayout = binding.drawerLayout

        //Set navigation drawer
        setupToolbarWithNavController()
    }

    private fun setupToolbarWithNavController() {
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
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when(destination.id) {
                R.id.recommendationsFragment -> {
                    binding.toolbar.apply {
                        title = "Рекомендации"
                    }
                    binding.calendarButton.visibility = View.VISIBLE
                }
                R.id.glossaryFragment -> {
                    binding.toolbar.apply {
                        title = "Словарь"
                    }
                    binding.calendarButton.visibility = View.GONE
                }
                else -> {
                    binding.calendarButton.visibility = View.GONE
                }
            }
        }
    }

}
