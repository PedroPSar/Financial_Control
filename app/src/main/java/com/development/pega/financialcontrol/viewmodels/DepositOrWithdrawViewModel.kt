package com.development.pega.financialcontrol.viewmodels

import android.app.Application
import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.control.AppControl
import com.development.pega.financialcontrol.model.Expense
import com.development.pega.financialcontrol.model.Income
import com.development.pega.financialcontrol.model.SavingsMoney
import com.development.pega.financialcontrol.service.Constants
import com.development.pega.financialcontrol.service.repository.expense.ExpenseRepository
import com.development.pega.financialcontrol.service.repository.income.IncomeRepository
import com.development.pega.financialcontrol.service.repository.savingsmoney.SavingsMoneyRepository
import java.text.SimpleDateFormat
import java.util.*

class DepositOrWithdrawViewModel(application: Application): AndroidViewModel(application) {

    private val mContext = application.applicationContext
    private val mSavingsMoneyRepository = SavingsMoneyRepository(mContext)
    private val mExpenseRepository = ExpenseRepository(mContext)
    private val mIncomeRepository = IncomeRepository(mContext)

    private var mDepositOrWithdraw = MutableLiveData<Boolean>()
    val depositOrWithdraw: LiveData<Boolean> = mDepositOrWithdraw

    private var mCurrentTime = MutableLiveData<String>()
    val currentTime: LiveData<String> = mCurrentTime

    private var mDatePickerDialog = MutableLiveData<DatePickerDialog>()
    val datePickerDialog: LiveData<DatePickerDialog> = mDatePickerDialog

    private var mGetSavings = MutableLiveData<SavingsMoney>()
    var getSavings: LiveData<SavingsMoney> = mGetSavings

    fun saveDepositOrWithdraw(savingsMoney: SavingsMoney) {
        if(savingsMoney.id == 0) {
            mDepositOrWithdraw.value = mSavingsMoneyRepository.save(savingsMoney)

            if(savingsMoney.type == Constants.SAVINGS_MONEY.DEPOSIT) {
                val expense = changeFromSavingsToExpense(savingsMoney)
                mExpenseRepository.save(expense)
            }else if(savingsMoney.type == Constants.SAVINGS_MONEY.WITHDRAW) {
                val income = changeFromSavingsToIncome(savingsMoney)
                mIncomeRepository.save(income)
            }

        }else {
            mDepositOrWithdraw.value = mSavingsMoneyRepository.update(savingsMoney)

            if(savingsMoney.type == Constants.SAVINGS_MONEY.DEPOSIT) {
                val expense = changeFromSavingsToExpense(savingsMoney)
                expense.id = getExpenseIdByRelationalID(savingsMoney.relationalID)
                mExpenseRepository.update(expense)
            }else if(savingsMoney.type == Constants.SAVINGS_MONEY.WITHDRAW) {
                val income = changeFromSavingsToIncome(savingsMoney)
                income.id = getIncomeIdByRelationalID(savingsMoney.relationalID)
                mIncomeRepository.update(income)
            }
        }

    }

    fun getCurrentDate() {
        val sdf = SimpleDateFormat(Constants.PATTERNS.DATE_PATTERN)
        mCurrentTime.value =  sdf.format(Date())
    }

    fun loadSavingsMoney(id: Int) {
        mGetSavings.value = mSavingsMoneyRepository.get(id)
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

    private fun changeFromSavingsToIncome(savingsMoney: SavingsMoney): Income {
        val income = Income()
        income.day = savingsMoney.day
        income.month = savingsMoney.month
        income.year = savingsMoney.year
        income.value = savingsMoney.money
        income.name = mContext.getString(R.string.withdraw_name)
        income.description = savingsMoney.description
        income.recurrence = Constants.RECURRENCE.NONE
        income.relationalID = savingsMoney.relationalID

        return income
    }

    private fun changeFromSavingsToExpense(savingsMoney: SavingsMoney): Expense {
        val expense = Expense()
        expense.type = Constants.TYPE.INVESTMENT
        expense.day = savingsMoney.day
        expense.month = savingsMoney.month
        expense.year = savingsMoney.year
        expense.value = savingsMoney.money
        expense.name = mContext.getString(R.string.deposit_name)
        expense.description = savingsMoney.description
        expense.recurrence = Constants.RECURRENCE.NONE
        expense.relationalID = savingsMoney.relationalID
        expense.paid = Constants.IS_PAID.YES

       return expense
    }

    private fun getIncomeIdByRelationalID(relationalID: Int): Int {
        return mIncomeRepository.getIncomeByRelationalId(relationalID).id
    }

    private fun getExpenseIdByRelationalID(relationalID: Int): Int {
        return mExpenseRepository.getExpenseByRelationalId(relationalID).id
    }


}