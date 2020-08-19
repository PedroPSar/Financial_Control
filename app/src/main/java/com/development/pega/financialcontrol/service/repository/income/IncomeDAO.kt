package com.development.pega.financialcontrol.service.repository.income

import androidx.room.*
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

}