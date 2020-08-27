package com.development.pega.financialcontrol.viewmodels

import android.app.Application
import android.app.DatePickerDialog
import android.content.Context
import android.provider.SyncStateContract
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.development.pega.financialcontrol.model.Income
import com.development.pega.financialcontrol.service.Constants
import com.development.pega.financialcontrol.service.repository.income.IncomeRepository
import kotlinx.android.synthetic.main.activity_add_income.*
import java.text.SimpleDateFormat
import java.util.*

class AddIncomeViewModel(application: Application): AndroidViewModel(application) {

    private val mIncomeRepository = IncomeRepository(application.applicationContext)

    private var mCurrentTime = MutableLiveData<String>()
    val currentTime: LiveData<String> = mCurrentTime

    private var mDatePickerDialog = MutableLiveData<DatePickerDialog>()
    val datePickerDialog: LiveData<DatePickerDialog> = mDatePickerDialog

    private var mAddIncome = MutableLiveData<Boolean>()
    val addIncome: LiveData<Boolean> = mAddIncome

    fun getCurrentDate() {
        val sdf = SimpleDateFormat(Constants.PATTERNS.DATE_PATTERN)
        mCurrentTime.value =  sdf.format(Date())
    }

    fun showDatePickerDialog(context: Context) {
        val c = Calendar.getInstance()
        val mDay = c.get(Calendar.DAY_OF_MONTH)
        val mMonth = c.get(Calendar.MONTH)
        val mYear = c.get(Calendar.YEAR)

        mDatePickerDialog.value = DatePickerDialog(context, { view, year, month, dayOfMonth ->
            val sdf = SimpleDateFormat(Constants.PATTERNS.DATE_PATTERN)
            c.set(year, month, dayOfMonth)
            mCurrentTime.value = sdf.format(c.time)
        }, mYear, mMonth, mDay)
    }

    fun saveIncome(income: Income) {
        mAddIncome.value = mIncomeRepository.save(income)
    }
}