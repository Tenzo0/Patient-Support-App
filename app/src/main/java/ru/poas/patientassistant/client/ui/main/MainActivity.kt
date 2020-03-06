package ru.poas.patientassistant.client.ui.main

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import ru.poas.patientassistant.client.R
import ru.poas.patientassistant.client.databinding.ActivityMainBinding
import ru.poas.patientassistant.client.preferences.UserPreferences
import ru.poas.patientassistant.client.ui.login.LoginActivity
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
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
            .setDrawerLayout(drawerLayout)
            .build()
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

        //Setup menu in NavDrawer with navigation
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.exit -> exit() // exit from profile
                else ->
                    try             // navigate to fragment
                    {
                        navController.navigate(it.itemId)
                        drawerLayout.closeDrawers()
                    }
                    catch (e: IllegalArgumentException) {
                        Timber.e("Illegal navigation destination in MainActivity:\n$e")
                    }
            }
            true
        }

        binding.toolbar.apply {
            for (child in children) {
                if (child is TextView) {
                    setMarquee(child)
                }
            }
        }
    }

    private fun setMarquee(textView: TextView) {
        textView.ellipsize = TextUtils.TruncateAt.MARQUEE
        textView.isSelected = true
        textView.marqueeRepeatLimit = -1
    }

    /**
     * Exit from [MainActivity]: clear all info about user,
     * start [LoginActivity], finish [MainActivity]
     */
    private fun exit() {
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