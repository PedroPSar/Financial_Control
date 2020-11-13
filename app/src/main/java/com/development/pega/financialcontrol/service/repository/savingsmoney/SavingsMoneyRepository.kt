package com.development.pega.financialcontrol.service.repository.savingsmoney

import android.content.Context
import com.development.pega.financialcontrol.model.SavingsMoney

class SavingsMoneyRepository(context: Context) {
    private val mDatabase = SavingsMoneyDatabase.getDatabase(context).savingsMoneyDao()

    fun getAllSavingsMoney(): List<SavingsMoney> {
        return mDatabase.getAllSavingsMoney()
    }

    fun get(id: Int): SavingsMoney {
        return mDatabase.get(id)
    }

    fun getSavingsByRelationalId(relationalID: Int): SavingsMoney {
        return mDatabase.getByRelationalId(relationalID)
    }

    fun getDeposits(): List<SavingsMoney> {
        return mDatabase.getDepositMoney()
    }

    fun getWithdrawals(): List<SavingsMoney> {
        return mDatabase.getWithdrawMoney()
    }

    fun save(money: SavingsMoney): Boolean {
        return mDatabase.save(money) > 0
    }

    fun update(money: SavingsMoney): Boolean {
        return mDatabase.update(money) > 0
    }

    fun delete(money: SavingsMoney) {
        mDatabase.delete(money)
    }
}