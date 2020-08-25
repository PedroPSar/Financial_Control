package com.development.pega.financialcontrol.viewmodels

import android.app.Application
import android.app.DatePickerDialog
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseViewModel(application: Application): AndroidViewModel(application) {

    private var mCurrentTime = MutableLiveData<String>()
    val currentTime: LiveData<String> = mCurrentTime

    private var mDatePickerDialog = MutableLiveData<DatePickerDialog>()
    val datePickerDialog: LiveData<DatePickerDialog> = mDatePickerDialog

    fun getCurrentDate() {
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        mCurrentTime.value =  sdf.format(Date())
    }

    fun showDatePickerDialog(context: Context) {
        val c = Calendar.getInstance()
        val mDay = c.get(Calendar.DAY_OF_MONTH)
        val mMonth = c.get(Calendar.MONTH)
        val mYear = c.get(Calendar.YEAR)

        mDatePickerDialog.value = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            c.set(year, month, dayOfMonth)
            mCurrentTime.value = sdf.format(c.time)
        }, mYear, mMonth, mDay)
    }

}