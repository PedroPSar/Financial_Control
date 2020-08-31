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

    private val mMonth = MutableLiveData<String>()
    var month: LiveData<String> = mMonth

    private val mYear = MutableLiveData<Int>()
    var year: LiveData<Int> = mYear

    private val mIncomes = MutableLiveData<Float>()
    var incomes: LiveData<Float> = mIncomes

    private val mExpenses = MutableLiveData<Float>()
    var expenses: LiveData<Float> = mExpenses

    private val mRecyclerViewIncomes = MutableLiveData<List<Income>>()
    var recyclerViewIncomes: LiveData<List<Income>> = mRecyclerViewIncomes

    fun setCurrentMonth() {
        val c = Calendar.getInstance()
        val month = c.get(Calendar.MONTH)
        selectedMonth = month + 1
        mMonth.value = months[month]
    }

    fun setIncomesOfMonth() {
        val incomesList = incomeRepository.getIncomesFromMonth(selectedMonth)
        mIncomes.value = sumIncomes(incomesList)
    }

    fun setExpensesOfmMonth() {
        val expensesList = expenseRepository.getExpensesFromMonth(selectedMonth)
        mExpenses.value = sumExpenses(expensesList)
    }

    fun setIncomesInRecyclerView() {
        mRecyclerViewIncomes.value = incomeRepository.getIncomesFromMonth(selectedMonth)
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

}