package com.dogebook.feed

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.dogebook.ExceptionHandler
import com.dogebook.R
import com.dogebook.databinding.ActivityMainBinding
import com.dogebook.login.InitialActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val toHideTabs = listOf(R.id.postFragment, R.id.remove, R.id.editProfileFragment,
        R.id.searchFragment, R.id.readProfileFragment, R.id.friendListFragment,
        R.id.friendRequestFragment, R.id.myPostsFragment)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(baseContext))

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        val navController = (supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment).navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        val appBarConfiguration = AppBarConfiguration
            .Builder(
                R.id.feedFragment,
                R.id.profileFragment
            )
            .build()

        setupActionBarWithNavController(navController, appBarConfiguration)
        toolbar.setupWithNavController(navController, appBarConfiguration)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (toHideTabs.contains(destination.id)) {
                hideTabs()
            } else {
                toolbar.visibility = View.VISIBLE
                showTabs()
            }
        }
        setupTabs(navController)

        val fab: FloatingActionButton = binding.fab

        fab.setOnClickListener {
            applicationContext.getSharedPreferences(R.string.preferences.toString(), Context.MODE_PRIVATE)
                .edit().remove("TOKEN").apply()
            startActivity(Intent(applicationContext, InitialActivity::class.java))
        }
    }

    private fun setupTabs(navController: NavController) {
        val tabs: TabLayout = binding.tabs
        tabs.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> navController.popBackStack()
                    1 -> navController.navigate(R.id.action_feedFragment_to_profileFragment)
                }
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {}
            override fun onTabReselected(p0: TabLayout.Tab?) {}
        })
    }


    private fun hideTabs() {
        binding.tabs.isVisible = false
    }
    private fun showTabs() {
        binding.tabs.isVisible = true
    }
}