package com.development.pega.financialcontrol.service.repository.expense

import androidx.room.*
import com.development.pega.financialcontrol.model.Expense
import com.development.pega.financialcontrol.model.Income

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

    @Query("SELECT * FROM Expenses WHERE month = :month")
    fun getExpensesFromMonth(month: Int): List<Expense>

    @Query("SELECT * FROM Expenses WHERE year = :year")
    fun getExpensesFromYear(year: Int): List<Expense>

    @Query("SELECT * FROM Expenses WHERE year = :year AND month = :month")
    fun getExpensesFromYearAndMonth(year: Int, month: Int): List<Expense>

    @Query("SELECT * FROM Expenses")
    fun getExpenses(): List<Expense>

    @Query("SELECT * FROM Expenses WHERE type = 1")
    fun getRequired(): List<Expense>

    @Query("SELECT * FROM Expenses WHERE type = 0")
    fun getNotRequired(): List<Expense>

    @Query("SELECT * FROM Expenses WHERE type = 2")
    fun getInvestment(): List<Expense>

    @Query("SELECT * FROM Expenses WHERE relationalID = :relationalID")
    fun getByRelationalId(relationalID: Int): Expense

    @Query("SELECT * FROM Expenses WHERE paid = 0")
    fun getNotPaidExpenses(): List<Expense>

    @Query("SELECT * FROM Expenses WHERE paid = 1")
    fun getPaidExpenses(): List<Expense>
}