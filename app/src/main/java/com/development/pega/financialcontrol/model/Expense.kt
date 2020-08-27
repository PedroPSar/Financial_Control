package com.development.pega.financialcontrol.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "Expenses")
class Expense {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id = 0

    @ColumnInfo(name = "type")
    var type = 0

    @ColumnInfo(name = "day")
    var day = 0

    @ColumnInfo(name = "month")
    var month = 0

    @ColumnInfo(name = "year")
    var year = 0

    @ColumnInfo(name = "value")
    var value = 0.0f

    @ColumnInfo(name = "description")
    var description = ""

    @ColumnInfo(name = "recurrence")
    var recurrence = 0
}