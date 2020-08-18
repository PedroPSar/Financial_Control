package com.development.pega.financialcontrol.service.repository.savingsmoney

import androidx.room.*
import com.development.pega.financialcontrol.model.Income
import com.development.pega.financialcontrol.model.SavingsMoney

@Dao
interface SavingsMoneyDAO {

    @Insert
    fun save(money: SavingsMoney): Long

    @Update
    fun update(money: SavingsMoney): Int

    @Delete
    fun delete(money: SavingsMoney)

    @Query("SELECT * FROM Savings WHERE id = :id")
    fun get(id: Int): SavingsMoney

    @Query("SELECT * FROM Savings")
    fun getSavingsMoney(): List<SavingsMoney>

    @Query("SELECT * FROM Savings WHERE type = 1")
    fun getDepositMoney(): List<SavingsMoney>

    @Query("SELECT * FROM Savings WHERE type = 2")
    fun getWithdrawMoney(): List<SavingsMoney>
}