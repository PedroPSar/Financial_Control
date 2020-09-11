package com.development.pega.financialcontrol.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.model.Expense
import com.development.pega.financialcontrol.model.Income
import com.development.pega.financialcontrol.service.Constants
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
        val incomesList = incomeRepository.getAll()
        mIncomes.value = sumIncomes(incomesList)
    }

    fun setExpensesOfMonth() {
        val expensesList = expenseRepository.getAll()
        mExpenses.value = sumExpenses(expensesList)
    }

    fun setIncomesInRecyclerView() {
        val list = incomeRepository.getAll()
        var currentMonthList = mutableListOf<Income>()

        for(income in list) {
            if(checkIfIncomeHasToBeOnTheList(income)) {
                income.month = selectedMonth
                income.year = selectedYear
                currentMonthList.add(income)
            }
        }
        mRecyclerViewIncomes.value = currentMonthList
    }

    fun setExpensesInRecyclerView() {
        val list = expenseRepository.getAll()
        var currentMonthList = mutableListOf<Expense>()

        for(expense in list) {
            if(checkIfExpenseHasToBeOnTheList(expense)) {
                expense.month = selectedMonth
                expense.year = selectedYear
                currentMonthList.add(expense)
            }
        }
        mRecyclerViewExpenses.value = currentMonthList
    }

    fun btnBeforeClick() {
        selectedMonth--
        selectedMonth = checkMonthNumber(selectedMonth)
        downYearIfNecessary(selectedMonth)
        updateInfo()
    }

    fun btnNextClick() {
        selectedMonth++
        selectedMonth = checkMonthNumber(selectedMonth)
        upYearIfNecessary(selectedMonth)
        updateInfo()

    }

    private fun setYear() {
        mYear.value = selectedYear
    }

    private fun sumIncomes(list: List<Income>): Float {
        var total = 0f
        for(income in list) {
            if(income.month == selectedMonth
                || income.recurrence == Constants.RECURRENCE.FIXED_MONTHLY && income.year == selectedYear && income.month < selectedMonth
                || income.recurrence == Constants.RECURRENCE.FIXED_MONTHLY && income.year < selectedYear) {
                total += income.value
            }
        }
        return total
    }

    private fun sumExpenses(list: List<Expense>): Float {
        var total = 0f
        for(expense in list) {
            if(expense.month == selectedMonth
                || expense.recurrence == Constants.RECURRENCE.FIXED_MONTHLY && expense.year == selectedYear && expense.month < selectedMonth
                || expense.recurrence == Constants.RECURRENCE.FIXED_MONTHLY && expense.year < selectedYear)  {
                total += expense.value
            }
        }
        return total
    }

    private fun sumFixedIncomes(income: Income): Float {
        var sum = 0f
            if(income.recurrence == Constants.RECURRENCE.FIXED_MONTHLY) {
                if(income.year < selectedYear) {
                    var otherYearsDifference = selectedYear - income.year
                    otherYearsDifference = calcOtherYears(otherYearsDifference)
                    sum += income.value * ( otherYearsDifference + (12 - income.month) )

                }else if(income.month < selectedMonth && income.year == selectedYear) {
                    sum += income.value * (selectedMonth - income.month)
                }
            }
        return sum
    }

    private fun sumFixedExpenses(expense: Expense): Float {
        var sum = 0f
        if(expense.recurrence == Constants.RECURRENCE.FIXED_MONTHLY) {
            if(expense.year < selectedYear) {
                var otherYearsDifference = selectedYear - expense.year
                otherYearsDifference = calcOtherYears(otherYearsDifference)
                sum += expense.value * ( otherYearsDifference + (12 - expense.month) )

            }else if(expense.month < selectedMonth && expense.year == selectedYear) {
                sum += expense.value * (selectedMonth - expense.month)
            }
        }
        return sum
    }

    private fun calcOtherYears(yearDifference: Int): Int {
        var difference = yearDifference
        return if(yearDifference > 1) {
            difference--
            (difference * 12) + selectedMonth
        }else {
            selectedMonth
        }
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
        setYear()
        calcBalance()
    }

    private fun calcSumOfMonths(): Float {
        val allIncomes = incomeRepository.getAll()
        val allExpenses = expenseRepository.getAll()
        var incomeSum = 0f
        var expenseSum = 0f
        var incomeInstallmentSum = 0f
        var expenseInstallmentSum = 0f
        var total = 0f
        var fixedMonthsSum = 0f
        var installmentSum = 0f

        for(income in allIncomes) {
            if(income.year == selectedYear && income.month <= selectedMonth || income.year < selectedYear){
                if(income.recurrence == Constants.RECURRENCE.INSTALLMENT) {
                    incomeInstallmentSum += calcInstallmentIncome(income)
                }else {
                    incomeSum += income.value
                }
            }
        }

        for(expense in allExpenses) {
            if(expense.year == selectedYear && expense.month <= selectedMonth || expense.year < selectedYear) {
                if(expense.recurrence == Constants.RECURRENCE.INSTALLMENT) {
                    expenseInstallmentSum += calcInstallmentExpense(expense)
                }else {
                    expenseSum += expense.value
                }
            }
        }

        installmentSum = incomeInstallmentSum - expenseInstallmentSum
        total = incomeSum - expenseSum
        fixedMonthsSum = calcFixedMonths(allIncomes, allExpenses)

        return total + fixedMonthsSum + installmentSum
    }

    private fun calcFixedMonths(incomes: List<Income>, expenses: List<Expense>): Float {
        var incomeSum = 0f
        var expenseSum = 0f

        for(income in incomes) {
            incomeSum += sumFixedIncomes(income)
        }

        for(expense in expenses) {
            expenseSum += sumFixedExpenses(expense)
        }

        return incomeSum - expenseSum
    }

    private fun checkIfIncomeHasToBeOnTheList(income: Income): Boolean {
        return (income.month == selectedMonth ||
                income.recurrence == Constants.RECURRENCE.FIXED_MONTHLY && income.year == selectedYear && income.month < selectedMonth ||
                income.recurrence == Constants.RECURRENCE.FIXED_MONTHLY && income.year < selectedYear)
    }

    private fun checkIfExpenseHasToBeOnTheList(expense: Expense): Boolean {
        return (expense.month == selectedMonth ||
                expense.recurrence == Constants.RECURRENCE.FIXED_MONTHLY && expense.year == selectedYear && expense.month < selectedMonth ||
                expense.recurrence == Constants.RECURRENCE.FIXED_MONTHLY && expense.year < selectedYear)
    }

    private fun upYearIfNecessary(month: Int) {
        if(month == 1) {
            selectedYear++
        }
    }

    private fun downYearIfNecessary(month: Int) {
        if(month == 12) {
            selectedYear--
        }
    }

    private fun calcInstallmentIncome(income: Income):Float {
        var otherYearMonths = 0
        var numMonths = 0
        if(income.year < selectedYear) {
            otherYearMonths =  calcOtherYears(selectedYear - income.year)
            numMonths = otherYearMonths + (12 - income.month) + 1 // +1 for first month installment
        }else if(income.year == selectedYear) {
            numMonths = selectedMonth - income.month + 1 // +1 for first month installment
        }

        var paidInstallments = 0
        var sum = 0f

        for(i in 1..numMonths step income.payFrequency) {
            if(paidInstallments < income.numInstallmentMonths) {
                sum += income.value
                paidInstallments++
            }
        }

        return sum
    }

    private fun calcInstallmentExpense(expense: Expense):Float {
        var otherYearMonths = 0
        var numMonths = 0
        if(expense.year < selectedYear) {
            otherYearMonths =  calcOtherYears(selectedYear - expense.year)
            numMonths = otherYearMonths + (12 - expense.month) + 1 // +1 for first month installment
        }else if(expense.year == selectedYear) {
            numMonths = selectedMonth - expense.month + 1 // +1 for first month installment
        }

        var paidInstallments = 0
        var sum = 0f

        for(i in 1..numMonths step expense.payFrequency) {
            if(paidInstallments < expense.numInstallmentMonths) {
                sum += expense.value
                paidInstallments++
            }
        }

        return sum
    }
}