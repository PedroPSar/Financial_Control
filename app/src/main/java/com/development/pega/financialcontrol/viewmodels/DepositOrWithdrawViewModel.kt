package com.development.pega.financialcontrol.viewmodels

import android.app.Application
import android.app.DatePickerDialog
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.model.Expense
import com.development.pega.financialcontrol.model.Income
import com.development.pega.financialcontrol.model.SavingsMoney
import com.development.pega.financialcontrol.service.Constants
import com.development.pega.financialcontrol.service.repository.expense.ExpenseRepository
import com.development.pega.financialcontrol.service.repository.income.IncomeRepository
import com.development.pega.financialcontrol.service.repository.savingsmoney.SavingsMoneyRepository
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.exp

class DepositOrWithdrawViewModel(application: Application): AndroidViewModel(application) {

    private val mContext = application.applicationContext
    private val savingsMoneyRepository = SavingsMoneyRepository(mContext)
    private val expenseRepository = ExpenseRepository(mContext)
    private val incomeRepository = IncomeRepository(mContext)

    private var mDepositOrWithdraw = MutableLiveData<Boolean>()
    val depositOrWithdraw: LiveData<Boolean> = mDepositOrWithdraw

    private var mCurrentTime = MutableLiveData<String>()
    val currentTime: LiveData<String> = mCurrentTime

    private var mDatePickerDialog = MutableLiveData<DatePickerDialog>()
    val datePickerDialog: LiveData<DatePickerDialog> = mDatePickerDialog

    fun saveDepositOrWithdraw(savingsMoney: SavingsMoney) {
        mDepositOrWithdraw.value = savingsMoneyRepository.save(savingsMoney)

        if(savingsMoney.type == Constants.SAVINGS_MONEY.DEPOSIT) {
            addExpense(savingsMoney)
        }else if(savingsMoney.type == Constants.SAVINGS_MONEY.WITHDRAW) {
            addIncome(savingsMoney)
        }
    }

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

    private fun addIncome(savingsMoney: SavingsMoney) {
        val income = Income()
        income.day = savingsMoney.day
        income.month = savingsMoney.month
        income.year = savingsMoney.year
        income.value = savingsMoney.money
        income.name = mContext.getString(R.string.withdraw_name)
        income.description = savingsMoney.description
        income.recurrence = Constants.RECURRENCE.NONE

        incomeRepository.save(income)
    }

    private fun addExpense(savingsMoney: SavingsMoney) {
        val expense = Expense()
        expense.type = Constants.TYPE.INVESTMENT
        expense.day = savingsMoney.day
        expense.month = savingsMoney.month
        expense.year = savingsMoney.year
        expense.value = savingsMoney.money
        expense.name = mContext.getString(R.string.deposit_name)
        expense.description = savingsMoney.description
        expense.recurrence = Constants.RECURRENCE.NONE

        expenseRepository.save(expense)
    }

}