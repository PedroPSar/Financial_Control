package com.development.pega.financialcontrol.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.control.AppControl
import com.development.pega.financialcontrol.model.Expense
import com.development.pega.financialcontrol.model.Income
import com.development.pega.financialcontrol.model.SavingsMoney
import com.development.pega.financialcontrol.service.Constants
import com.development.pega.financialcontrol.service.Data
import com.development.pega.financialcontrol.service.repository.expense.ExpenseRepository
import com.development.pega.financialcontrol.service.repository.income.IncomeRepository
import com.development.pega.financialcontrol.service.repository.savingsmoney.SavingsMoneyRepository
import java.util.*

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val mContext = application.applicationContext
    private val months = mContext.resources.getStringArray(R.array.months_array)
    private val mIncomeRepository = IncomeRepository(mContext)
    private val mExpenseRepository = ExpenseRepository(mContext)
    private val mSavingsMoneyRepository = SavingsMoneyRepository(mContext)

    private val mMonth = MutableLiveData<String>()
    var month: LiveData<String> = mMonth

    private val mYear = MutableLiveData<Int>()
    var year: LiveData<Int> = mYear

    private val mBalance = MutableLiveData<Float>()
    var balance: LiveData<Float> = mBalance

    private val mIncomes = MutableLiveData<Float>()
    var incomes: LiveData<Float> = mIncomes

    private val mExpenses = MutableLiveData<Float>()
    var expenses: LiveData<Float> = mExpenses

    private val mRecyclerViewIncomes = MutableLiveData<List<Income>>()
    var recyclerViewIncomes: LiveData<List<Income>> = mRecyclerViewIncomes

    private val mRecyclerViewExpenses = MutableLiveData<List<Expense>>()
    var recyclerViewExpenses: LiveData<List<Expense>> = mRecyclerViewExpenses

    fun calcBalance() {
        mBalance.value = AppControl.calcAccountBalance(mContext)
    }

    fun setCurrentDate() {
        if(Data.isFirstView) {
            val c = Calendar.getInstance()
            val month = c.get(Calendar.MONTH) // Get month int starting zero
            val year = c.get(Calendar.YEAR)

            AppControl.setSelectedMonthStartingZero(month)
            Data.selectedYear = year
            mMonth.value = months[AppControl.getSelectedMonthForArrays()]
            mYear.value = year
            Data.isFirstView = false
        }else {
            mMonth.value = months[AppControl.getSelectedMonthForArrays()]
            mYear.value = Data.selectedYear
        }
    }

    fun setIncomesOfMonth() {
        val incomesList = mIncomeRepository.getAll()
        mIncomes.value = AppControl.sumSelectedMonthIncomes(incomesList)
    }

    fun setExpensesOfMonth() {
        val expensesList = mExpenseRepository.getAll()
        mExpenses.value = AppControl.sumSelectedMonthExpenses(expensesList)
    }

    fun setIncomesInRecyclerView() {
        val list = mIncomeRepository.getAll()
        var currentMonthList = mutableListOf<Income>()

        for(income in list) {
            if(checkIfIncomeHasToBeOnTheList(income)) {
                income.month = Data.selectedMonth
                income.year = Data.selectedYear
                currentMonthList.add(income)
            }
        }
        val sortedList = AppControl.orderIncomeList(currentMonthList)
        mRecyclerViewIncomes.value = sortedList
    }

    fun setExpensesInRecyclerView() {
        val list = mExpenseRepository.getAll()
        var currentMonthList = mutableListOf<Expense>()

        for(expense in list) {
            if(checkIfExpenseHasToBeOnTheList(expense)) {
                expense.month = Data.selectedMonth
                expense.year = Data.selectedYear
                currentMonthList.add(expense)
            }
        }
        val sortedList = AppControl.orderExpenseList(currentMonthList)
        mRecyclerViewExpenses.value = sortedList
    }

    fun btnBeforeClick() {
        Data.selectedMonth--
        Data.selectedMonth = checkMonthNumber(Data.selectedMonth)
        downYearIfNecessary(Data.selectedMonth)
        updateInfo()
    }

    fun btnNextClick() {
        Data.selectedMonth++
        Data.selectedMonth = checkMonthNumber(Data.selectedMonth)
        upYearIfNecessary(Data.selectedMonth)
        updateInfo()

    }

    private fun setYear() {
        mYear.value = Data.selectedYear
    }

    private fun setMonth() {
        mMonth.value = months[Data.selectedMonth - 1]
    }

    private fun checkMonthNumber(num: Int): Int {
        return when(num) {
            0 -> {12}
            13 -> {1}
            else -> {num}
        }
    }

    private fun updateInfo() {
        setIncomesOfMonth()
        setExpensesOfMonth()
        setIncomesInRecyclerView()
        setExpensesInRecyclerView()
        setMonth()
        setYear()
        calcBalance()
    }

    private fun checkIfIncomeHasToBeOnTheList(income: Income): Boolean {
        return (income.month == Data.selectedMonth && income.year == Data.selectedYear ||
                income.recurrence == Constants.RECURRENCE.FIXED_MONTHLY && income.year == Data.selectedYear && income.month < Data.selectedMonth ||
                income.recurrence == Constants.RECURRENCE.FIXED_MONTHLY && income.year < Data.selectedYear
                || AppControl.thisInstallmentIncomeIsFromThisMonth(income))
    }

    private fun checkIfExpenseHasToBeOnTheList(expense: Expense): Boolean {
        return (expense.month == Data.selectedMonth && expense.year == Data.selectedYear ||
                expense.recurrence == Constants.RECURRENCE.FIXED_MONTHLY && expense.year == Data.selectedYear && expense.month < Data.selectedMonth ||
                expense.recurrence == Constants.RECURRENCE.FIXED_MONTHLY && expense.year < Data.selectedYear
                || AppControl.thisInstallmentExpenseIsFromThisMonth(expense))
    }

    private fun upYearIfNecessary(month: Int) {
        if(month == 1) {
            Data.selectedYear++
        }
    }

    private fun downYearIfNecessary(month: Int) {
        if(month == 12) {
            Data.selectedYear--
        }
    }

    fun deleteIncome(income: Income) {
        mIncomeRepository.delete(income)
        if(income.name == mContext.getString(R.string.withdraw_name)) {
            deleteRelationalSavingsMoney(income.relationalID)
        }
        updateInfo()
    }

    fun deleteExpense(expense: Expense) {
        mExpenseRepository.delete(expense)
        if(expense.name == mContext.getString(R.string.deposit_name)) {
            deleteRelationalSavingsMoney(expense.relationalID)
        }
        updateInfo()
    }

    private fun deleteRelationalSavingsMoney(relationalID: Int) {
        val savingsID = getSavingsMoneyIdByRelationalID(relationalID)
        val savingsMoney = mSavingsMoneyRepository.get(savingsID)
        mSavingsMoneyRepository.delete(savingsMoney)
    }

    private fun getSavingsMoneyIdByRelationalID(relationalID: Int): Int {
        return mSavingsMoneyRepository.getSavingsByRelationalId(relationalID).id
    }

}