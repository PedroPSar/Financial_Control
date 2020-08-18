package com.development.pega.financialcontrol.service.repository.income

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.development.pega.financialcontrol.model.Income

@Database(entities = [Income::class], version = 1)
abstract class IncomeDatabase: RoomDatabase() {

    abstract fun incomeDao(): IncomeDAO

    companion object {
        private lateinit var INSTANCE: IncomeDatabase

        fun getDatabase(context: Context): IncomeDatabase {
            if(!::INSTANCE.isInitialized) {
                synchronized(IncomeDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context, IncomeDatabase::class.java, "incomesDB")
                        .allowMainThreadQueries().build()
                }
            }

            return INSTANCE
        }
    }
}