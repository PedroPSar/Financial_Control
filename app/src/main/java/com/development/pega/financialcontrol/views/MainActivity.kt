package com.development.pega.financialcontrol.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.development.pega.financialcontrol.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener,
        PopupMenu.OnMenuItemClickListener{

    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var popupMenu: PopupMenu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //Initialize popup menu
        val button = toolbar.getChildAt(0)
        popupMenu = PopupMenu(this, button)

        setNavController()
        setOnClicks()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId) {
        R.id.add_expense_or_revenue -> {
            setAddMenu()
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.nav_savings -> {
                val savingsFragment = SavingsFragment()
                supportFragmentManager.beginTransaction().add(R.id.nav_host_fragment, savingsFragment).commit()
            }

            R.id.nav_home -> {
                val homeFragment = HomeFragment()
                supportFragmentManager.beginTransaction().add(R.id.nav_host_fragment, homeFragment).commit()
            }

            R.id.nav_chart -> {
                val chartFragment = ChartFragment()
                supportFragmentManager.beginTransaction().add(R.id.nav_host_fragment, chartFragment).commit()
            }
        }
        return true
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when(item!!.itemId) {
            R.id.add_income -> {

            }

            R.id.add_expense -> {

            }
        }
        return true
    }

    private fun setOnClicks() {
        popupMenu.setOnMenuItemClickListener(this)
    }

    private fun setNavController() {
        navController = findNavController(R.id.nav_host_fragment)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setupWithNavController(navController)
    }

    private fun setAddMenu() {
        popupMenu.inflate(R.menu.add_menu)
        popupMenu.show()
    }

}