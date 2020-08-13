package com.development.pega.financialcontrol.model

class Savings(var balance: Float, var inputs: List<Revenue> = listOf(), var outputs: List<Expense> = listOf(),
    var objective: String) {
}