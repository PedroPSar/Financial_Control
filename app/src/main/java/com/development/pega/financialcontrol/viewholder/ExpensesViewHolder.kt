package com.development.pega.financialcontrol.viewholder

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.control.AppControl
import com.development.pega.financialcontrol.listener.ExpenseItemListener
import com.development.pega.financialcontrol.model.Expense
import com.development.pega.financialcontrol.service.Constants
import com.development.pega.financialcontrol.service.repository.Prefs
import java.util.*

class ExpensesViewHolder(itemView: View, private val mItemListener: ExpenseItemListener): RecyclerView.ViewHolder(itemView), View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private lateinit var btnMenu: ImageView
    private lateinit var tvInstalmentNumber: TextView
    private lateinit var mExpense: Expense
    private val mContext = itemView.context
    private val mRecurrences = mContext.resources.getStringArray(R.array.spinner_recurrence_options)
    private val mTypes = mContext.resources.getStringArray(R.array.spinner_type_options)
    private var mExpenseRecurrence = ""
    private var txtDate = ""
    private var txtValue = ""
    private var txtName = ""

    private val mPrefs = AppControl.getAppPrefs(mContext)

    fun bind(expense: Expense) {

        mExpense = expense
        val day = checkAndChangeMaximumDayOfMonth(expense.year, expense.month, expense.day)
        val txtDay = putZeroIfNumberLessTen(day)
        val txtMonth = putZeroIfNumberLessTen(expense.month)

        txtDate = "$txtDay/$txtMonth/${expense.year}"
        txtValue = AppControl.Text.convertFloatToCurrencyText(expense.value)
        txtName = expense.name
        mExpenseRecurrence = getExpenseRecurrence(mExpense.recurrence)

        btnMenu = itemView.findViewById(R.id.img_btn_item_menu)
        tvInstalmentNumber = itemView.findViewById(R.id.tv_txt_installments)
        itemView.findViewById<TextView>(R.id.tv_txt_name).text = txtName
        itemView.findViewById<TextView>(R.id.tv_txt_date).text = txtDate
        itemView.findViewById<TextView>(R.id.tv_txt_value).text = txtValue

        if(mExpense.recurrence == Constants.RECURRENCE.INSTALLMENT) {
            tvInstalmentNumber.visibility = View.VISIBLE
            tvInstalmentNumber.text = AppControl.getExpenseInstallmentNumber(mExpense.id, itemView.context)
        } else {
            tvInstalmentNumber.visibility = View.GONE
        }

        setPaymentTextsAndColors()
        setOnClicks()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.img_btn_item_menu -> showItemMenu()
            R.id.ll_btn_pay -> payOrCancelPayment(mExpense)
        }

    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            R.id.edit_item -> {
                mItemListener.onEdit(mExpense.id)
                true
            }
            R.id.delete_item -> {
                AlertDialog.Builder(itemView.context)
                    .setMessage(R.string.confirm_delete_expense_dialog_message)
                    .setPositiveButton(R.string.confirm_delete_income_dialog_positive_button) { _, _ ->
                        mItemListener.onDelete(mExpense)
                    }
                    .setNeutralButton(R.string.confirm_delete_income_dialog_neutral_button, null)
                    .show()
                true
            }
            R.id.details_item -> {
                showDetailsDialog()
                true
            }
            else -> false
        }
    }

    private fun setOnClicks() {
        itemView.findViewById<ImageView>(R.id.img_btn_item_menu).setOnClickListener(this)
        itemView.findViewById<LinearLayout>(R.id.ll_btn_pay).setOnClickListener(this)
    }

    private fun showItemMenu() {
        val popUpMenu = PopupMenu(itemView.context, btnMenu)
        popUpMenu.menuInflater.inflate(R.menu.recycler_item_menu, popUpMenu.menu)
        popUpMenu.setOnMenuItemClickListener(this)
        popUpMenu.show()
    }

    private fun checkAndChangeMaximumDayOfMonth(year: Int, month: Int, day: Int): Int {
        val c = Calendar.getInstance()
        c.set(Calendar.YEAR, year)
        c.set(Calendar.MONTH, month - 1) //In calendar January is 0
        val maximum = c.getActualMaximum(Calendar.DATE)

        return if(day > maximum) {
            maximum
        }else {
            day
        }
    }

    private fun putZeroIfNumberLessTen(number: Int): String {
        return if(number < 10) {
            "0$number"
        }else {
            number.toString()
        }
    }

    private fun showDetailsDialog() {
        val builder = AlertDialog.Builder(mContext)
        builder.setTitle(mContext.getString(R.string.details_dialog_title))
        val view = LayoutInflater.from(mContext).inflate(R.layout.dialog_details_expenses, null)

        val tvLblNumber = view.findViewById<TextView>(R.id.tv_lbl_number)
        val tvTxtNumber = view.findViewById<TextView>(R.id.tv_txt_number)
        val tvLblEvery = view.findViewById<TextView>(R.id.tv_lbl_every)
        val tvTxtEvery = view.findViewById<TextView>(R.id.tv_txt_every)

        view.findViewById<TextView>(R.id.tv_txt_name).text = mExpense.name
        view.findViewById<TextView>(R.id.tv_txt_type).text = getExpenseType(mExpense.type)
        view.findViewById<TextView>(R.id.tv_txt_description).text = mExpense.description
        view.findViewById<TextView>(R.id.tv_txt_value).text = txtValue
        view.findViewById<TextView>(R.id.tv_txt_date).text = txtDate
        view.findViewById<TextView>(R.id.tv_txt_recurrence).text = mExpenseRecurrence

        // Installment == 1
        if(mExpenseRecurrence == mRecurrences[1]) {

            tvTxtNumber.text = mExpense.numInstallmentMonths.toString()

            val txtMonth = if(mExpense.payFrequency > 1) {
                mContext.getString(R.string.months)
            } else {
                mContext.getString(R.string.month)
            }

            val txt = "${mExpense.payFrequency} $txtMonth"
            tvTxtEvery.text = txt

            tvLblNumber.visibility = View.VISIBLE
            tvTxtNumber.visibility = View.VISIBLE
            tvLblEvery.visibility = View.VISIBLE
            tvTxtEvery.visibility = View.VISIBLE
        }

        builder.setView(view)
        builder.setPositiveButton(mContext.getString(R.string.txt_ok)) { dialog, _ -> dialog?.dismiss() }

        val detailsDialog = builder.create()
        detailsDialog.setOnShowListener { detailsDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK) }

        detailsDialog.show()
    }

    private fun getExpenseRecurrence(recurrence: Int): String {
        return when(recurrence) {
            Constants.RECURRENCE.NONE -> mRecurrences[0]
            Constants.RECURRENCE.INSTALLMENT -> mRecurrences[1]
            Constants.RECURRENCE.FIXED_MONTHLY -> mRecurrences[2]
            else -> mRecurrences[0]
        }
    }

    private fun getExpenseType(type: Int): String {
        return when(type) {
            Constants.TYPE.NOT_REQUIRED -> mTypes[0]
            Constants.TYPE.REQUIRED -> mTypes[1]
            Constants.TYPE.INVESTMENT -> mTypes[2]
            else -> mTypes[0]
        }
    }

    private fun setPaymentTextsAndColors() {
        val c = Calendar.getInstance()

        itemView.findViewById<TextView>(R.id.tv_txt_paid).text = getExpensePayText(c)
        setColorsInPayInfo(c)
    }

    private fun getExpensePayText(c: Calendar): String {

        return if(mExpense.paid == Constants.IS_PAID.YES) {
            mContext.getString(R.string.paid)
        }else if(mExpense.paid == Constants.IS_PAID.NO && mExpense.day < c.get(Calendar.DAY_OF_MONTH)) {
            mContext.getString(R.string.overdue)
        }else {
            mContext.getString(R.string.unpaid)
        }
    }

    private fun setColorsInPayInfo(c: Calendar) {

        if(mExpense.paid == Constants.IS_PAID.YES) {
            itemView.findViewById<TextView>(R.id.tv_txt_paid).setBackgroundColor(mPrefs.paidColor)
            itemView.findViewById<TextView>(R.id.txt_btn_pay).text = mContext.getString(R.string.cancel_payment)

        }else if(mExpense.paid == Constants.IS_PAID.NO && mExpense.day < c.get(Calendar.DAY_OF_MONTH)) {
            itemView.findViewById<TextView>(R.id.tv_txt_paid).setBackgroundColor(mPrefs.overdueColor)
            itemView.findViewById<TextView>(R.id.txt_btn_pay).text = mContext.getString(R.string.pay)

        }else {
            itemView.findViewById<TextView>(R.id.tv_txt_paid).setBackgroundColor(mPrefs.unPaidColor)
            itemView.findViewById<TextView>(R.id.txt_btn_pay).text = mContext.getString(R.string.pay)
        }

    }

    private fun payOrCancelPayment(expense: Expense) {
        Log.d("teste", "pay click")
        mItemListener.onPay(expense)
    }

}