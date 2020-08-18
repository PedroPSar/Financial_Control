package com.development.pega.financialcontrol.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "Savings")
class SavingsMoney() {

    @ColumnInfo(name = "id")
    var id = 0

    @ColumnInfo(name = "money")
    var money = 0.0f

    @ColumnInfo(name = "date")
    var date = ""

    @ColumnInfo(name = "description")
    var description = ""

    @ColumnInfo(name = "type")
    var type = 0
}