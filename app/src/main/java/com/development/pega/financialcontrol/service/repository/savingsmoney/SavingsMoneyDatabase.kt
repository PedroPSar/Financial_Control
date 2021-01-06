package com.development.pega.financialcontrol.service.repository.savingsmoney

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.development.pega.financialcontrol.model.SavingsMoney
import com.development.pega.financialcontrol.service.repository.income.IncomeDAO
import com.development.pega.financialcontrol.service.repository.income.IncomeDatabase

@Database(entities = [SavingsMoney::class], version = 1, exportSchema = false)
abstract class SavingsMoneyDatabase: RoomDatabase() {

    abstract fun savingsMoneyDao(): SavingsMoneyDAO

    companion object {
        private lateinit var INSTANCE: SavingsMoneyDatabase

        fun getDatabase(context: Context): SavingsMoneyDatabase {
            if(!::INSTANCE.isInitialized) {
                synchronized(SavingsMoneyDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context, SavingsMoneyDatabase::class.java, "savingsMoneyDB")
                        .allowMainThreadQueries().build()
                }
            }

            return INSTANCE
        }
    }
}