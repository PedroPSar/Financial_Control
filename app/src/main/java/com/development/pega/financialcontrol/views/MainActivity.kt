package com.development.pega.financialcontrol.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.listener.MainListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener,
        PopupMenu.OnMenuItemClickListener{

    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var popupMenu: PopupMenu
    private lateinit var tvYear: TextView
    private lateinit var toolbar: Toolbar
    private lateinit var mMainListener: MainListener

    private lateinit var homeFragment: HomeFragment
    private lateinit var savingsFragment: SavingsFragment
    private lateinit var chartFragment: ChartFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //Initialize popup menu
        val button = toolbar.getChildAt(0)
        popupMenu = PopupMenu(this, button)

        setCurrentYear()
        setNavController()
        setOnClicks()

        mMainListener = object: MainListener {
            override fun onSetYear(year: String) {
                tvYear.text = year
            }
        }

        instantiateFragments()
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
                supportFragmentManager.beginTransaction().add(R.id.nav_host_fragment, savingsFragment).commit()
            }

            R.id.nav_home -> {
                homeFragment.attachListener(mMainListener)
                supportFragmentManager.beginTransaction().add(R.id.nav_host_fragment, homeFragment).commit()
            }

            R.id.nav_chart -> {
                supportFragmentManager.beginTransaction().add(R.id.nav_host_fragment, chartFragment).commit()
            }
        }
        return true
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when(item!!.itemId) {
            R.id.add_income -> {
                val intent = Intent(this, AddIncomeActivity::class.java)
                startActivity(intent)
            }

            R.id.add_expense -> {
                val intent = Intent(this, AddExpenseActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }

    private fun instantiateFragments() {
        homeFragment = HomeFragment()
        homeFragment.attachListener(mMainListener)
        savingsFragment = SavingsFragment()
        chartFragment = ChartFragment()
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

    private fun setCurrentYear() {
        val c = Calendar.getInstance()
        tvYear = toolbar.findViewById(R.id.tv_year)
        tvYear.text = c.get(Calendar.YEAR).toString()

        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

}