package ru.poas.patientassistant.client.patient.ui

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import ru.poas.patientassistant.client.R
import ru.poas.patientassistant.client.databinding.ActivityPatientBinding
import ru.poas.patientassistant.client.preferences.UserPreferences
import ru.poas.patientassistant.client.login.ui.LoginActivity
import ru.poas.patientassistant.client.preferences.PatientPreferences


class PatientActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPatientBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_patient)
        drawerLayout = binding.drawerLayout

        //Set action bar which changes own menu depending on fragment
        setSupportActionBar(binding.toolbar)

        //Set navigation drawer with 4 top level destinations
        navController = findNavController(R.id.main_nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.recommendationsFragment,
            R.id.profileFragment,
            R.id.glossaryFragment,
            R.id.medicinesFragment)
            .setOpenableLayout(drawerLayout)
            .build()
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.statusBarColor = Color.BLACK
        }

        //Setup menu in NavDrawer with navigation
        setupNavDrawerWithNavController()

        PatientPreferences.init(applicationContext)
    }

    private fun setupNavDrawerWithNavController() {
        with(binding) {
            navView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.exit -> exit() // exit from profile
                    else -> {
                        // navigate to fragment
                        NavigationUI.onNavDestinationSelected(it, navController)
                        drawerLayout.closeDrawers()
                    }
                }
                true
            }

            //set selected item highlighted in the drawable menu
            navController.addOnDestinationChangedListener { controller, destination, arguments ->
                val menu: Menu = navView.menu

                for (i in 0 until menu.size()) {
                    val item: MenuItem = menu.getItem(i)
                    item.isChecked = destination.id == item.itemId
                }
            }
        }
    }

    /**
     * Exit from [PatientActivity]: clear all info about user,
     * start [LoginActivity], finish [PatientActivity]
     */
    fun exit() {
        //Clear all information about user
        UserPreferences.clear()
        //Create animations for navigation
        val navOptions = NavOptions.Builder()
        navOptions
            .setEnterAnim(R.anim.nav_default_enter_anim)
            .setExitAnim(R.anim.nav_default_exit_anim)
            .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
            .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
        //Navigate to start (LoginActivity)
        navController.navigate(R.id.loginActivity, null, navOptions.build())
        //Finish session with this activity
        finish()
    }
}