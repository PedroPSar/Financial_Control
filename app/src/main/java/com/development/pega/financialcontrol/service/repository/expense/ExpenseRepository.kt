package com.development.pega.financialcontrol.service.repository.expense

import android.content.Context
import com.development.pega.financialcontrol.model.Expense
import com.development.pega.financialcontrol.model.Income

class ExpenseRepository(context: Context) {
    private val mDatabase = ExpenseDatabase.getDatabase(context).expenseDao()

    fun getAll(): List<Expense> {
        return mDatabase.getExpenses()
    }

    fun getRequired(): List<Expense> {
        return mDatabase.getRequired()
    }

    fun getNotRequired(): List<Expense> {
        return mDatabase.getNotRequired()
    }

    fun getInvestment(): List<Expense> {
        return mDatabase.getInvestment()
    }

    fun get(id: Int): Expense {
        return mDatabase.get(id)
    }

    fun getExpensesFromMonth(month: Int): List<Expense> {
        return mDatabase.getExpensesFromMonth(month)
    }

    fun getExpensesFromYear(year: Int): List<Expense> {
        return mDatabase.getExpensesFromYear(year)
    }

    fun getExpensesFromYearAndMonth(year: Int, month: Int): List<Expense> {
        return mDatabase.getExpensesFromYearAndMonth(year, month)
    }

    fun save(expense: Expense): Boolean {
        return mDatabase.save(expense) > 0
    }

    fun update(expense: Expense): Boolean {
        return mDatabase.update(expense) > 0
    }

    fun delete(expense: Expense) {
        mDatabase.delete(expense)
    }

}