package com.dogebook.feed

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager.widget.ViewPager
import com.dogebook.R
import com.dogebook.databinding.ActivityFeedBinding
import com.dogebook.login.MainActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout

class FeedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar2
        setSupportActionBar(toolbar)
        val navController = findNavController(R.id.nav_host_fragment_feed)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        toolbar.setupWithNavController(navController, appBarConfiguration)
        toolbar.setNavigationOnClickListener {
            showTabs()
            navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
        }

        val viewPager: ViewPager = binding.viewPager
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)
        val fab: FloatingActionButton = binding.fab

        fab.setOnClickListener {
            applicationContext.getSharedPreferences(R.string.preferences.toString(), Context.MODE_PRIVATE)
                .edit().remove("TOKEN").apply()
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_feed)
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