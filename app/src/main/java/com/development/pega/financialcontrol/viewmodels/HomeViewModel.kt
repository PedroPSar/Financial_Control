package com.development.pega.financialcontrol.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.control.AppControl
import com.development.pega.financialcontrol.model.Expense
import com.development.pega.financialcontrol.model.Income
import com.development.pega.financialcontrol.service.Constants
import com.development.pega.financialcontrol.service.Data
import com.development.pega.financialcontrol.service.repository.expense.ExpenseRepository
import com.development.pega.financialcontrol.service.repository.income.IncomeRepository
import java.util.*

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val mContext = application.applicationContext
    private val months = mContext.resources.getStringArray(R.array.months_array)
    private val incomeRepository = IncomeRepository(mContext)
    private val expenseRepository = ExpenseRepository(mContext)

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
        val month = c.get(Calendar.MONTH) // Get month int starting zero
        val year = c.get(Calendar.YEAR)

        AppControl.setSelectedMonthStartingZero(month)
        Data.selectedYear = year
        mMonth.value = months[AppControl.getSelectedMonthForArrays()]
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
                income.month = Data.selectedMonth
                income.year = Data.selectedYear
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
                expense.month = Data.selectedMonth
                expense.year = Data.selectedYear
                currentMonthList.add(expense)
            }
        }
        mRecyclerViewExpenses.value = currentMonthList
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

    private fun sumIncomes(list: List<Income>): Float {
        var total = 0f
        for(income in list) {
            if(income.month == Data.selectedMonth
                || income.recurrence == Constants.RECURRENCE.FIXED_MONTHLY && income.year == Data.selectedYear && income.month < Data.selectedMonth
                || income.recurrence == Constants.RECURRENCE.FIXED_MONTHLY && income.year < Data.selectedYear
                || thisInstallmentIncomeIsFromThisMonth(income)) {
                total += income.value
            }
        }
        return total
    }

    private fun sumExpenses(list: List<Expense>): Float {
        var total = 0f
        for(expense in list) {
            if(expense.month == Data.selectedMonth
                || expense.recurrence == Constants.RECURRENCE.FIXED_MONTHLY && expense.year == Data.selectedYear && expense.month < Data.selectedMonth
                || expense.recurrence == Constants.RECURRENCE.FIXED_MONTHLY && expense.year < Data.selectedYear
                || thisInstallmentExpenseIsFromThisMonth(expense))  {
                total += expense.value
            }
        }
        return total
    }

    private fun thisInstallmentIncomeIsFromThisMonth(income: Income): Boolean{
        var result = false

        if(income.recurrence == Constants.RECURRENCE.INSTALLMENT) {

            var month = income.month
            for(i in 2 .. income.numInstallmentMonths) {
                month += income.payFrequency
                if(month > 12) month-= 12

                if(Data.selectedMonth == month) {
                    result = true
                    break
                }
            }
        }
        return result
    }

    private fun thisInstallmentExpenseIsFromThisMonth(expense: Expense): Boolean{
        var result = false

        if(expense.recurrence == Constants.RECURRENCE.INSTALLMENT) {

            var month = expense.month
            for(i in 2 .. expense.numInstallmentMonths) {
                month += expense.payFrequency
                if(month > 12) month-= 12

                if(Data.selectedMonth == month) {
                    result = true
                    break
                }
            }
        }
        return result
    }

    private fun sumFixedIncomes(income: Income): Float {
        var sum = 0f
            if(income.recurrence == Constants.RECURRENCE.FIXED_MONTHLY) {
                if(income.year < Data.selectedYear) {
                    var otherYearsDifference = Data.selectedYear - income.year
                    otherYearsDifference = calcOtherYears(otherYearsDifference)
                    sum += income.value * ( otherYearsDifference + (12 - income.month) )

                }else if(income.month < Data.selectedMonth && income.year == Data.selectedYear) {
                    sum += income.value * (Data.selectedMonth - income.month)
                }
            }
        return sum
    }

    private fun sumFixedExpenses(expense: Expense): Float {
        var sum = 0f
        if(expense.recurrence == Constants.RECURRENCE.FIXED_MONTHLY) {
            if(expense.year < Data.selectedYear) {
                var otherYearsDifference = Data.selectedYear - expense.year
                otherYearsDifference = calcOtherYears(otherYearsDifference)
                sum += expense.value * ( otherYearsDifference + (12 - expense.month) )

            }else if(expense.month < Data.selectedMonth && expense.year == Data.selectedYear) {
                sum += expense.value * (Data.selectedMonth - expense.month)
            }
        }
        return sum
    }

    private fun calcOtherYears(yearDifference: Int): Int {
        var difference = yearDifference
        return if(yearDifference > 1) {
            difference--
            (difference * 12) + Data.selectedMonth
        }else {
            Data.selectedMonth
        }
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
            if(income.year == Data.selectedYear && income.month <= Data.selectedMonth || income.year < Data.selectedYear){
                if(income.recurrence == Constants.RECURRENCE.INSTALLMENT) {
                    incomeInstallmentSum += calcInstallmentIncome(income)
                }else {
                    incomeSum += income.value
                }
            }
        }

        for(expense in allExpenses) {
            if(expense.year == Data.selectedYear && expense.month <= Data.selectedMonth || expense.year < Data.selectedYear) {
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
        return (income.month == Data.selectedMonth ||
                income.recurrence == Constants.RECURRENCE.FIXED_MONTHLY && income.year == Data.selectedYear && income.month < Data.selectedMonth ||
                income.recurrence == Constants.RECURRENCE.FIXED_MONTHLY && income.year < Data.selectedYear
                || thisInstallmentIncomeIsFromThisMonth(income))
    }

    private fun checkIfExpenseHasToBeOnTheList(expense: Expense): Boolean {
        return (expense.month == Data.selectedMonth ||
                expense.recurrence == Constants.RECURRENCE.FIXED_MONTHLY && expense.year == Data.selectedYear && expense.month < Data.selectedMonth ||
                expense.recurrence == Constants.RECURRENCE.FIXED_MONTHLY && expense.year < Data.selectedYear
                || thisInstallmentExpenseIsFromThisMonth(expense))
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

    private fun calcInstallmentIncome(income: Income):Float {
        var otherYearMonths = 0
        var numMonths = 0
        if(income.year < Data.selectedYear) {
            otherYearMonths =  calcOtherYears(Data.selectedYear - income.year)
            numMonths = otherYearMonths + (12 - income.month) + 1 // +1 for first month installment
        }else if(income.year == Data.selectedYear) {
            numMonths = Data.selectedMonth - income.month + 1 // +1 for first month installment
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
        if(expense.year < Data.selectedYear) {
            otherYearMonths =  calcOtherYears(Data.selectedYear - expense.year)
            numMonths = otherYearMonths + (12 - expense.month) + 1 // +1 for first month installment
        }else if(expense.year == Data.selectedYear) {
            numMonths = Data.selectedMonth - expense.month + 1 // +1 for first month installment
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