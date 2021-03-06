package com.development.pega.financialcontrol.service

import android.content.res.Resources
import android.content.res.Resources.*
import com.development.pega.financialcontrol.R

class Constants private constructor() {

    companion object {
        const val ITEM_ID = "itemID"
    }

    object TYPE {
        const val NOT_REQUIRED = 0
        const val REQUIRED = 1
        const val INVESTMENT = 2
    }

    object RECURRENCE {
        const val NONE = 0
        const val INSTALLMENT = 1
        const val FIXED_MONTHLY = 2
    }

    object SAVINGS_MONEY {
        const val DEPOSIT = 1
        const val WITHDRAW = 2
    }

    object PATTERNS {
        const val DATE_PATTERN = "dd/MM/yyyy"
    }

    object LINE {
        const val LINE_WIDTH = 3f
        const val LINE_TEXT_SIZE = 8f
    }

    object PIE {
        const val PIE_VALUE_TEXT_SIZE = 8f
    }

    object IS_PAID {
        const val YES = 1
        const val NO = 0
    }

}