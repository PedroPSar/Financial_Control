package com.development.pega.financialcontrol.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.listener.MainListener
import com.development.pega.financialcontrol.service.Data
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener,
        PopupMenu.OnMenuItemClickListener, View.OnClickListener{

    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var popupMenu: PopupMenu
    private var popupMenuIsInflated = false;
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
        supportActionBar?.setDisplayShowTitleEnabled(false)

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

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.image_btn_left_arrow -> btnLeftArrowYearClick()
            R.id.image_btn_right_arrow -> btnRightArrowYearClick()
        }
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

        R.id.open_settings_activity -> {
            openSettingsActivity()
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
            val transaction = supportFragmentManager.beginTransaction()

            when(item.itemId) {
            R.id.nav_savings -> {
                transaction.replace(R.id.nav_host_fragment, savingsFragment).commit()
            }

            R.id.nav_home -> {
                transaction.replace(R.id.nav_host_fragment, homeFragment).commit()
            }

            R.id.nav_chart -> {
                transaction.replace(R.id.nav_host_fragment, chartFragment).commit()
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
        homeFragment = HomeFragment.newInstance(mMainListener)
        savingsFragment = SavingsFragment()
        chartFragment = ChartFragment()

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.nav_host_fragment, chartFragment)
        transaction.add(R.id.nav_host_fragment, homeFragment).commit()
    }

    private fun setOnClicks() {
        popupMenu.setOnMenuItemClickListener(this)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
        image_btn_left_arrow.setOnClickListener(this)
        image_btn_right_arrow.setOnClickListener(this)
    }

    private fun setNavController() {
        navController = findNavController(R.id.nav_host_fragment)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setupWithNavController(navController)
    }

    private fun setAddMenu() {
        if(!popupMenuIsInflated) {
            popupMenu.inflate(R.menu.add_menu)
            popupMenuIsInflated = true
        }
        popupMenu.show()
    }

    private fun openSettingsActivity() {
        val intent = Intent(this, SettingsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun setCurrentYear() {
        val c = Calendar.getInstance()
        tvYear = toolbar.findViewById(R.id.tv_year)
        tvYear.text = c.get(Calendar.YEAR).toString()
    }

    private fun btnLeftArrowYearClick() {
        Data.selectedYear--
        tvYear.text = Data.selectedYear.toString()
        homeFragment.updateHomeInfo()
        chartFragment.updateChartInfo()
    }

    private fun btnRightArrowYearClick() {
        Data.selectedYear++
        tvYear.text = Data.selectedYear.toString()
        homeFragment.updateHomeInfo()
        chartFragment.updateChartInfo()
    }

}