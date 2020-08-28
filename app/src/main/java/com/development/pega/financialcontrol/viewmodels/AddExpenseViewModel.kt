package com.development.pega.financialcontrol.viewmodels

import android.app.Application
import android.app.DatePickerDialog
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.development.pega.financialcontrol.model.Expense
import com.development.pega.financialcontrol.service.Constants
import com.development.pega.financialcontrol.service.repository.expense.ExpenseRepository
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseViewModel(application: Application): AndroidViewModel(application) {

    private val mExpenseRepository = ExpenseRepository(application.applicationContext)

    private var mCurrentTime = MutableLiveData<String>()
    val currentTime: LiveData<String> = mCurrentTime

    private var mDatePickerDialog = MutableLiveData<DatePickerDialog>()
    val datePickerDialog: LiveData<DatePickerDialog> = mDatePickerDialog

    private var mSaveExpense = MutableLiveData<Boolean>()
    val saveExpense: LiveData<Boolean> = mSaveExpense

    fun getCurrentDate() {
        val sdf = SimpleDateFormat(Constants.PATTERNS.DATE_PATTERN)
        mCurrentTime.value =  sdf.format(Date())
    }

    fun showDatePickerDialog(context: Context) {
        val c = Calendar.getInstance()
        val mDay = c.get(Calendar.DAY_OF_MONTH)
        val mMonth = c.get(Calendar.MONTH)
        val mYear = c.get(Calendar.YEAR)

        mDatePickerDialog.value = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            val sdf = SimpleDateFormat(Constants.PATTERNS.DATE_PATTERN)
            c.set(year, month, dayOfMonth)
            mCurrentTime.value = sdf.format(c.time)
        }, mYear, mMonth, mDay)
    }

    fun addExpense(expense: Expense) {
        mSaveExpense.value = mExpenseRepository.save(expense)
    }

}