package com.development.pega.financialcontrol.service.repository.income

import android.content.Context
import com.development.pega.financialcontrol.model.Expense
import com.development.pega.financialcontrol.model.Income

class IncomeRepository(context: Context) {

    private val mDatabase = IncomeDatabase.getDatabase(context).incomeDao()

    fun getAll(): List<Income> {
        return mDatabase.getIncomes()
    }

    fun get(id: Int): Income {
        return mDatabase.get(id)
    }

    fun getIncomeByRelationalId(relationalID: Int): Income {
        return mDatabase.getByRelationalId(relationalID)
    }

    fun getIncomesFromMonth(month: Int): List<Income> {
        return mDatabase.getIncomesFromMonth(month)
    }

    fun getIncomesFromYear(year: Int): List<Income> {
        return mDatabase.getIncomesFromYear(year)
    }

    fun getIncomesFromYearAndMonth(year: Int, month: Int): List<Income> {
        return mDatabase.getIncomesFromYearAndMonth(year, month)
    }

    fun save(income: Income): Boolean {
        return mDatabase.save(income) > 0
    }

    fun update(income: Income): Boolean {
        return mDatabase.update(income) > 0
    }

    fun delete(income: Income) {
        mDatabase.delete(income)
    }

}