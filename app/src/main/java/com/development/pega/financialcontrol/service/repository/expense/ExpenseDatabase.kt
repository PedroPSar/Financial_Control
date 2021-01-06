package com.development.pega.financialcontrol.service.repository.expense

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.development.pega.financialcontrol.model.Expense

@Database(entities = [Expense::class], version = 1, exportSchema = false)
abstract class ExpenseDatabase: RoomDatabase() {

    abstract fun expenseDao(): ExpenseDAO

    companion object {
        private lateinit var INSTANCE: ExpenseDatabase

        fun getDatabase(context: Context): ExpenseDatabase {
            if(!::INSTANCE.isInitialized) {
                synchronized(ExpenseDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context, ExpenseDatabase::class.java, "expensesDB")
                        .allowMainThreadQueries().build()
                }
            }

            return INSTANCE
        }
    }
}