package com.development.pega.financialcontrol.service.repository.income

import androidx.room.*
import com.development.pega.financialcontrol.model.Expense
import com.development.pega.financialcontrol.model.Income

@Dao
interface IncomeDAO {

    @Insert
    fun save(income: Income): Long

    @Update
    fun update(income: Income): Int

    @Delete
    fun delete(income: Income)

    @Query("SELECT * FROM Incomes WHERE id = :id")
    fun get(id: Int): Income

    @Query("SELECT * FROM Incomes")
    fun getIncomes(): List<Income>

    @Query("SELECT * FROM Incomes WHERE month = :month")
    fun getIncomesFromMonth(month: Int): List<Income>

    @Query("SELECT * FROM Incomes WHERE year = :year")
    fun getIncomesFromYear(year: Int): List<Income>

    @Query("SELECT * FROM Incomes WHERE year = :year AND month = :month")
    fun getIncomesFromYearAndMonth(year: Int, month: Int): List<Income>

    @Query("SELECT * FROM Incomes WHERE relationalID = :relationalID")
    fun getByRelationalId(relationalID: Int): Income

}