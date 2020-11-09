package com.development.pega.financialcontrol.listener

interface ItemListener {
    fun onEdit(id: Int)
    fun onDelete(id: Int)
}