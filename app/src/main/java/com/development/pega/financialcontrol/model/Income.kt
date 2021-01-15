package com.development.pega.financialcontrol.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Incomes")
class Income() {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id = 0

    @ColumnInfo(name = "day")
    var day = 0

    @ColumnInfo(name = "month")
    var month = 0

    @ColumnInfo(name = "year")
    var year = 0

    @ColumnInfo(name = "value")
    var value = 0.0f

    @ColumnInfo(name = "name")
    var name = ""

    @ColumnInfo(name = "description")
    var description = ""

    @ColumnInfo(name = "recurrence")
    var recurrence = 0

    @ColumnInfo(name = "numInstallmentMonths")
    var numInstallmentMonths = 0

    @ColumnInfo(name = "payFrequency")
    var payFrequency = 0

    @ColumnInfo(name = "relationalID")
    var relationalID = 0

    @ColumnInfo(name = "startDate")
    var startDate = ""

    @ColumnInfo(name = "endDate")
    var endDate = ""

    @ColumnInfo(name = "paidMonths")
    var paidMonths = ""
}