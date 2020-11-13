package com.development.pega.financialcontrol.viewmodels

import android.app.Application
import android.app.DatePickerDialog
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.development.pega.financialcontrol.model.Expense
import com.development.pega.financialcontrol.model.SavingsMoney
import com.development.pega.financialcontrol.service.Constants
import com.development.pega.financialcontrol.service.repository.expense.ExpenseRepository
import com.development.pega.financialcontrol.service.repository.savingsmoney.SavingsMoneyRepository
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseViewModel(application: Application): AndroidViewModel(application) {

    private val context = application.applicationContext
    private val mExpenseRepository = ExpenseRepository(context)
    private val mSavingsMoneyRepository = SavingsMoneyRepository(context)

    private var mCurrentTime = MutableLiveData<String>()
    val currentTime: LiveData<String> = mCurrentTime

    private var mDatePickerDialog = MutableLiveData<DatePickerDialog>()
    val datePickerDialog: LiveData<DatePickerDialog> = mDatePickerDialog

    private var mSaveExpense = MutableLiveData<Boolean>()
    val saveExpense: LiveData<Boolean> = mSaveExpense

    private var mGetExpense = MutableLiveData<Expense>()
    val getExpense: LiveData<Expense> = mGetExpense

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

    fun saveExpense(expense: Expense) {
        if(expense.id == 0) {
            mSaveExpense.value = mExpenseRepository.save(expense)
        }else {
            mSaveExpense.value = mExpenseRepository.update(expense)

            val money = changeFromExpenseToSavings(expense)
            money.id = getSavingsMoneyIdByRelationalID(expense.relationalID)
            mSavingsMoneyRepository.update(money)
        }

    }

    fun loadExpense(id: Int) {
        mGetExpense.value = mExpenseRepository.get(id)
    }

    private fun changeFromExpenseToSavings(expense: Expense): SavingsMoney {
        val money = SavingsMoney()
        money.money = expense.value
        money.day = expense.day
        money.month = expense.month
        money.year = expense.year
        money.description = expense.description
        money.type = Constants.SAVINGS_MONEY.DEPOSIT
        money.relationalID = expense.relationalID

        return money
    }

    private fun getSavingsMoneyIdByRelationalID(relationalID: Int): Int {
        return mSavingsMoneyRepository.getSavingsByRelationalId(relationalID).id
    }

}