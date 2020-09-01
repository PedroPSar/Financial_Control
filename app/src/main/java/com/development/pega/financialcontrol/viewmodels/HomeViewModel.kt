package com.development.pega.financialcontrol.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.model.Expense
import com.development.pega.financialcontrol.model.Income
import com.development.pega.financialcontrol.service.repository.expense.ExpenseRepository
import com.development.pega.financialcontrol.service.repository.income.IncomeRepository
import java.util.*

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val mContext = application.applicationContext
    private val months = mContext.resources.getStringArray(R.array.months_array)
    private val incomeRepository = IncomeRepository(mContext)
    private val expenseRepository = ExpenseRepository(mContext)
    private var selectedMonth = 0
    private var selectedYear = 0

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
        mBalance.value = calcSumOfMonths()
    }

    fun setCurrentDate() {
        val c = Calendar.getInstance()
        val month = c.get(Calendar.MONTH)
        val year = c.get(Calendar.YEAR)
        selectedMonth = month + 1
        selectedYear = year
        mMonth.value = months[month]
        mYear.value = year
    }

    fun setIncomesOfMonth() {
        val incomesList = incomeRepository.getIncomesFromMonth(selectedMonth)
        mIncomes.value = sumIncomes(incomesList)
    }

    fun setExpensesOfMonth() {
        val expensesList = expenseRepository.getExpensesFromMonth(selectedMonth)
        mExpenses.value = sumExpenses(expensesList)
    }

    fun setIncomesInRecyclerView() {
        mRecyclerViewIncomes.value = incomeRepository.getIncomesFromMonth(selectedMonth)
        val list = incomeRepository.getIncomesFromMonth(selectedMonth)
    }

    fun setExpensesInRecyclerView() {
        mRecyclerViewExpenses.value = expenseRepository.getExpensesFromMonth(selectedMonth)
        val list = expenseRepository.getExpensesFromMonth(selectedMonth)
    }

    fun btnBeforeClick() {
        selectedMonth--
        selectedMonth = checkMonthNumber(selectedMonth)
        updateInfo()
    }

    fun btnNextClick() {
        selectedMonth++
        selectedMonth = checkMonthNumber(selectedMonth)
        updateInfo()
    }

    fun setYear() {
        mYear.value = selectedYear
    }

    private fun sumIncomes(list: List<Income>): Float {
        var total = 0f
        for(income in list) {
            total += income.value
        }
        return total
    }

    private fun sumExpenses(list: List<Expense>): Float {
        var total = 0f
        for(expense in list) {
            total += expense.value
        }
        return total
    }

    private fun setMonth() {
        mMonth.value = months[selectedMonth - 1]
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
        calcBalance()
    }

    private fun calcSumOfMonths(): Float {
        val allIncomes = incomeRepository.getAll()
        val allExpenses = expenseRepository.getAll()
        var incomeSum = 0f
        var expenseSum = 0f

        for(income in allIncomes) {
            if(income.year <= selectedYear && income.month <= selectedMonth)
            incomeSum += income.value
        }

        for(expense in allExpenses) {
            if(expense.year <= selectedYear && expense.month <= selectedMonth) {
                expenseSum += expense.value
            }
        }

        return incomeSum - expenseSum
    }

}