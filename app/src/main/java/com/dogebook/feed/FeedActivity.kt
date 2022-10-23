package com.dogebook.feed

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.dogebook.R
import com.dogebook.databinding.ActivityFeedBinding
import com.dogebook.feed.ui.main.SectionsPagerAdapter
import com.dogebook.login.MainActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout

class FeedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)
        val fab: FloatingActionButton = binding.fab

        fab.setOnClickListener {
            applicationContext.getSharedPreferences(R.string.preferences.toString(), Context.MODE_PRIVATE)
                .edit().remove("TOKEN").apply()
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }
    }
}