package com.development.pega.financialcontrol.service.repository.expense

import androidx.room.*
import com.development.pega.financialcontrol.model.Expense

@Dao
interface ExpenseDAO {

    @Insert
    fun save(expense: Expense): Long

    @Update
    fun update(expense: Expense): Int

    @Delete
    fun delete(expense: Expense)

    @Query("SELECT * FROM Expenses WHERE id = :id")
    fun get(id: Int): Expense

    @Query("SELECT * FROM Expenses")
    fun getExpenses(): List<Expense>

    @Query("SELECT * FROM Expenses WHERE type = 1")
    fun getRequired(): List<Expense>

    @Query("SELECT * FROM Expenses WHERE type = 0")
    fun getNotRequired(): List<Expense>

    @Query("SELECT * FROM Expenses WHERE type = 2")
    fun getInvestment(): List<Expense>

}