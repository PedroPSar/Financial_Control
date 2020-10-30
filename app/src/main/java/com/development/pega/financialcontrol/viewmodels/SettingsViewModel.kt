package com.development.pega.financialcontrol.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.service.repository.Prefs

class SettingsViewModel(application: Application): AndroidViewModel(application) {

    private val mContext = application.applicationContext
    private val currencies = mContext.resources.getStringArray(R.array.currencies)

    private var mSelectedCurrency = MutableLiveData<String>()
    var selectedCurrency: LiveData<String> = mSelectedCurrency

    fun setSelectedCurrency(position: Int) {
        mSelectedCurrency.value = currencies[position]
    }

    fun saveSelectedCurrency(value: String) {
        val prefs = Prefs(mContext)
        prefs.currencySelectedValue = value
    }
}