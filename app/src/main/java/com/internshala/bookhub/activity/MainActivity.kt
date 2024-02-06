package com.internshala.bookhub.activity

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.internshala.bookhub.fragment.AboutAppFragment
import com.internshala.bookhub.fragment.DashboardFragment
import com.internshala.bookhub.fragment.FavouritesFragment
import com.internshala.bookhub.fragment.ProfileFragment
import com.internshala.bookhub.R

class MainActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var coordinatorLayout: CoordinatorLayout

    var prevMenuItem : MenuItem ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.nav_view)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        frameLayout = findViewById(R.id.frameLayout)
        toolbar = findViewById(R.id.toolbar)
        setUpToolbar()


        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        openDasboard()

        navView.setNavigationItemSelectedListener {

            if (prevMenuItem != null)
                prevMenuItem?.isChecked = false

            it.isCheckable = true
            it.isChecked = true
            prevMenuItem = it

            when (it.itemId) {
                R.id.dashboard -> {
                    openDasboard()
                    drawerLayout.close()
                }
                R.id.favourites -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, FavouritesFragment())
                        .commit()

                    supportActionBar?.title = "Favourites"
                    drawerLayout.close()
                }
                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, ProfileFragment())
                        .commit()

                    supportActionBar?.title = "Profile"
                    drawerLayout.close()
                }
                R.id.aboutApp -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, AboutAppFragment())
                        .commit()

                    supportActionBar?.title = "About App"
                    drawerLayout.close()
                }
            }
            return@setNavigationItemSelectedListener true
        }

    }

    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        return super.onOptionsItemSelected(item)
    }

    fun openDasboard () {
        val frame = DashboardFragment()
        val transection = supportFragmentManager.beginTransaction()

        transection.replace(R.id.frameLayout,frame)
        transection.commit()

        supportActionBar?.title = "Dashboard"

        navView.setCheckedItem(R.id.dashboard)
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frameLayout)

        when (frag) {
            !is DashboardFragment -> openDasboard()

            else -> super.onBackPressed()
        }
    }
}