package com.development.pega.financialcontrol.service.repository.income

import android.content.Context
import com.development.pega.financialcontrol.model.Income

class IncomeRepository(context: Context) {

    private val mDatabase = IncomeDatabase.getDatabase(context).incomeDao()

    fun getAll(): List<Income> {
        return mDatabase.getIncomes()
    }

    fun get(id: Int): Income {
        return mDatabase.get(id)
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