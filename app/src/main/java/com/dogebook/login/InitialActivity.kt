package com.dogebook.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.dogebook.ExceptionHandler
import com.dogebook.Util
import com.dogebook.databinding.ActivityInitialBinding
import com.dogebook.feed.MainActivity
import com.dogebook.login.ui.main.SectionsPagerAdapter
import com.google.android.material.tabs.TabLayout

class InitialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInitialBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(baseContext))
        if (Util.getToken(applicationContext).isNotBlank()) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
        binding = ActivityInitialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)
    }
}