package com.development.pega.financialcontrol.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.development.pega.financialcontrol.model.Month
import com.development.pega.financialcontrol.service.Constants
import java.util.*

class HomeViewModel : ViewModel() {

    private val mMonthList = MutableLiveData<List<Month>>()
    var monthList: LiveData<List<Month>> = mMonthList

    fun updateMonthList() {

    }

    fun getMonths() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val dayOfMonth = c.get(Calendar.DAY_OF_MONTH)
    }
}