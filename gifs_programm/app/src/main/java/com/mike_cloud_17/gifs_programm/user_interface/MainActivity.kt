package com.mike_cloud_17.gifs_programm.user_interface

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.tabs.TabLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.mike_cloud_17.gifs_programm.R
import com.mike_cloud_17.gifs_programm.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)

        setSupportActionBar(binding.toolbar)
        setContentView(binding.root)

        val pagerAdapter = PagerAdapter(this)

        val viewPager: ViewPager2 = binding.viewPager.apply {
            adapter = pagerAdapter
        }

        val tabs: TabLayout = findViewById(R.id.tabs)

        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = pagerAdapter.getPageTitle(position)
            viewPager.setCurrentItem(tab.position, true)
        }.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings) {
            startActivity(Intent(applicationContext, SettingsActivity::class.java))
        } else if (item.itemId == R.id.action_about) {
            AlertDialogBuilder(this).build().show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

}