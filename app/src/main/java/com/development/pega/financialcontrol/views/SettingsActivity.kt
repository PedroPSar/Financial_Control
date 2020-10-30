package com.development.pega.financialcontrol.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.viewmodels.SettingsViewModel

class SettingsActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var mSettingsViewModel: SettingsViewModel
    private lateinit var mViewModelFactory: ViewModelProvider.AndroidViewModelFactory

    private lateinit var mToolbar: Toolbar
    private lateinit var mCurrencySpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setViewModel()
        setToolbarOptions()
        setSpinners()
        observers()
        setListeners()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if(parent?.id == R.id.spinner_currency) {
            mSettingsViewModel.setSelectedCurrency(position)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    private fun setListeners() {
        mCurrencySpinner.onItemSelectedListener = this
    }

    private fun setSpinners() {
        mCurrencySpinner = findViewById(R.id.spinner_currency)
        ArrayAdapter.createFromResource(this, R.array.currencies, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            mCurrencySpinner.adapter = adapter
        }
    }

    private fun observers() {
        mSettingsViewModel.selectedCurrency.observe(this, Observer {
            mSettingsViewModel.saveSelectedCurrency(it)
        })
    }

    private fun setViewModel() {
        mViewModelFactory = ViewModelProvider.AndroidViewModelFactory(application)
        mSettingsViewModel = ViewModelProvider(this, mViewModelFactory).get(SettingsViewModel::class.java)
    }

    private fun setToolbarOptions() {
        mToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.settings_title)

        mToolbar.setNavigationOnClickListener{ onBackPressed() }
    }

}