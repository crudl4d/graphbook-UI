package com.dogebook.feed

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager.widget.ViewPager
import com.dogebook.R
import com.dogebook.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        val navController = (supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment).navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        toolbar.setupWithNavController(navController, appBarConfiguration)
        toolbar.setNavigationOnClickListener {
            showTabs()
            navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
        }

        setupTabs(navController)

        val fab: FloatingActionButton = binding.fab

        fab.setOnClickListener {
            applicationContext.getSharedPreferences(R.string.preferences.toString(), Context.MODE_PRIVATE)
                .edit().remove("TOKEN").apply()
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }
    }

    private fun setupTabs(navController: NavController) {
        val viewPager: ViewPager = binding.viewPager
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)
        tabs.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> navController.navigate(R.id.action_profileFragment_to_feedFragment)
                    1 -> navController.navigate(R.id.action_feedFragment_to_profileFragment)
                }
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabReselected(p0: TabLayout.Tab?) {

            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    fun hideTabs() {
        binding.tabs.isVisible = false
    }
    fun showTabs() {
        binding.tabs.isVisible = true
    }
}