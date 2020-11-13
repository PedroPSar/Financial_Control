package com.development.pega.financialcontrol.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Savings")
class SavingsMoney() {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id = 0

    @ColumnInfo(name = "money")
    var money = 0.0f

    @ColumnInfo(name = "day")
    var day = 0

    @ColumnInfo(name = "month")
    var month = 0

    @ColumnInfo(name = "year")
    var year = 0

    @ColumnInfo(name = "description")
    var description = ""

    // type 1 deposit, 2 withdraw
    @ColumnInfo(name = "type")
    var type = 0

    @ColumnInfo(name = "relationalID")
    var relationalID = 0
}