package com.development.pega.financialcontrol.control

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.model.Expense
import com.development.pega.financialcontrol.model.Income
import com.development.pega.financialcontrol.model.SavingsMoney
import com.development.pega.financialcontrol.service.Constants
import com.development.pega.financialcontrol.service.Data
import com.development.pega.financialcontrol.service.repository.Prefs
import com.development.pega.financialcontrol.service.repository.expense.ExpenseRepository
import com.development.pega.financialcontrol.service.repository.income.IncomeRepository
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

abstract class AppControl {

    object Text {
        fun convertCurrencyTextToFloat(value: String): Float {
            val regex = "[^0-9]".toRegex()
            var v = value.replace(regex, "")
            Log.d("teste", "apos retirar virgula: $v")
            v = v.substring(0, v.length - 2) + "." + v.substring(v.length - 2)
            Log.d("teste", "value after add . - $v")
            Log.d("teste", "value after convert: ${v.toFloat()}")
            return v.toFloat()
        }

        fun convertValueForCurrencyEditText(value: Float): String {
            var v = convertFloatToCurrencyText(value)
            val regex = "[^0-9]".toRegex()
            v = v.replace(regex, "")
            return v
        }

        fun convertFloatToCurrencyText(value: Float): String {
            val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
            return format.format(value)
        }

        fun setDateText(day: Int, month: Int, year: Int): String {
            return "$day/$month/$year"
        }
    }

    object Validator {
        fun makeEmptyRequiredFieldToast(context: Context) {
            Toast.makeText(context, context.getString(R.string.empty_required_field_toast_text), Toast.LENGTH_SHORT).show()
        }

        fun makeNotEnoughMoneyToast(context: Context) {
            Toast.makeText(context, context.getString(R.string.not_enough_money_toast_text), Toast.LENGTH_SHORT).show()
        }
    }

    companion object {

        private var mLastItemRecyclerView: View? = null

        fun showToast(context: Context, text: String) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }

        fun getCurrencyAbbreviation(context: Context): String {
            return Prefs(context).currencySelectedValue
        }

        fun orderIncomeList(incomeList: List<Income>): List<Income> {
            return incomeList.sortedBy { it.day }
        }

        fun orderExpenseList(expenseList: List<Expense>): List<Expense> {
            return expenseList.sortedBy { it.day }
        }

        fun orderSavingsMoney(savingsMoneyList: List<SavingsMoney>): List<SavingsMoney> {
            return savingsMoneyList.sortedWith(compareBy({it.year}, {it.month}, {it.day}))
        }

        fun recyclerItemTouch(action: Int, v: View): Boolean {
            when(action) {
                MotionEvent.ACTION_DOWN -> {
                    if(mLastItemRecyclerView != null){
                        mLastItemRecyclerView!!.background = mLastItemRecyclerView!!.context.getDrawable(android.R.color.transparent)
                    }
                    mLastItemRecyclerView = v
                    v?.background = v?.context.getDrawable(R.color.touch_recycler_view_item_color)
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    v?.background = v?.context.getDrawable(android.R.color.transparent)
                    return true
                }

                MotionEvent.ACTION_MOVE -> {
                    v?.background = v?.context.getDrawable(android.R.color.transparent)
                    return true
                }
                else -> return false
            }
        }

        fun getRecurrence(pos: Int): Int {
            return when(pos) {
                Constants.RECURRENCE.NONE -> {Constants.RECURRENCE.NONE}
                Constants.RECURRENCE.INSTALLMENT -> {Constants.RECURRENCE.INSTALLMENT}
                Constants.RECURRENCE.FIXED_MONTHLY -> {Constants.RECURRENCE.FIXED_MONTHLY}
                else -> {Constants.RECURRENCE.NONE}
            }
        }

        fun getType(pos: Int): Int {
            return when(pos) {
                Constants.TYPE.NOT_REQUIRED -> {Constants.TYPE.NOT_REQUIRED}
                Constants.TYPE.REQUIRED -> {Constants.TYPE.REQUIRED}
                Constants.TYPE.INVESTMENT -> {Constants.TYPE.INVESTMENT}
                else -> {Constants.TYPE.NOT_REQUIRED}
            }
        }

        fun calendarSetTime(date: String): Calendar {
            val c = Calendar.getInstance()
            val sdf = SimpleDateFormat(Constants.PATTERNS.DATE_PATTERN)
            c.time = sdf.parse(date)
            return c
        }

        // This method get number starting 0 then convert for starting 1 ( 1 = January )
        fun setSelectedMonthStartingZero(num: Int) {
            Data.selectedMonth = num + 1
        }

        fun getSelectedMonthForArrays(): Int {
            return Data.selectedMonth - 1
        }

        fun sumSelectedMonthIncomes(list: List<Income>): Float {
            var total = 0f
            for(income in list) {
                if(income.month == Data.selectedMonth && income.year == Data.selectedYear
                    || income.recurrence == Constants.RECURRENCE.FIXED_MONTHLY && income.year == Data.selectedYear && income.month < Data.selectedMonth
                    || income.recurrence == Constants.RECURRENCE.FIXED_MONTHLY && income.year < Data.selectedYear
                    || thisInstallmentIncomeIsFromThisMonth(income)) {
                    total += income.value
                }
            }
            return total
        }

        fun sumSelectedMonthExpenses(list: List<Expense>): Float {
            var total = 0f
            for(expense in list) {
                if(expense.month == Data.selectedMonth && expense.year == Data.selectedYear
                    || expense.recurrence == Constants.RECURRENCE.FIXED_MONTHLY && expense.year == Data.selectedYear && expense.month < Data.selectedMonth
                    || expense.recurrence == Constants.RECURRENCE.FIXED_MONTHLY && expense.year < Data.selectedYear
                    || thisInstallmentExpenseIsFromThisMonth(expense))  {
                    total += expense.value
                }
            }
            return total
        }

        fun thisInstallmentIncomeIsFromThisMonth(income: Income): Boolean{
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

        fun thisInstallmentExpenseIsFromThisMonth(expense: Expense): Boolean{
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

        fun calcAccountBalance(context: Context): Float {
            val incomeRepository = IncomeRepository(context)
            val expenseRepository = ExpenseRepository(context)
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

    }

}