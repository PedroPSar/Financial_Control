package com.development.pega.financialcontrol.viewholder

import android.graphics.Color
import android.view.*
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.control.AppControl
import com.development.pega.financialcontrol.listener.IncomeItemListener
import com.development.pega.financialcontrol.model.Income
import com.development.pega.financialcontrol.service.Constants
import kotlinx.android.synthetic.main.income_recycler_view_row.view.*
import java.util.*

class IncomesViewHolder(itemView: View, private val mItemListener: IncomeItemListener): RecyclerView.ViewHolder(itemView), View.OnClickListener, PopupMenu.OnMenuItemClickListener{

    lateinit var btnMenu: ImageView
    private lateinit var mIncome: Income
    private val mContext = itemView.context
    private val mRecurrences = mContext.resources.getStringArray(R.array.spinner_recurrence_options)
    private var mIncomeRecurrence = ""
    private var txtDate = ""
    private var txtValue = ""
    private var txtName = ""

    fun bind(income: Income) {
        mIncome = income
        val day = checkAndChangeMaximumDayOfMonth(income.year, income.month, income.day)
        val txtDay = putZeroIfNumberLessTen(day)
        var txtMonth = putZeroIfNumberLessTen(income.month)

        txtDate = "$txtDay/$txtMonth/${income.year}"
        txtValue = AppControl.Text.convertFloatToCurrencyText(income.value)
        txtName = income.name
        mIncomeRecurrence = getIncomeRecurrence(mIncome.recurrence)

        btnMenu = itemView.findViewById(R.id.img_btn_item_menu)
        itemView.findViewById<TextView>(R.id.tv_txt_name).text = txtName
        itemView.findViewById<TextView>(R.id.tv_txt_date).text = txtDate
        itemView.findViewById<TextView>(R.id.tv_txt_value).text = txtValue

        itemView.img_btn_item_menu.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        if(v?.id == R.id.img_btn_item_menu) {
            showItemMenu()
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            R.id.edit_item -> {
                mItemListener.onEdit(mIncome.id)
                true
            }
            R.id.delete_item -> {
                val builder = AlertDialog.Builder(mContext)
                    .setMessage(R.string.confirm_delete_income_dialog_message)
                    .setPositiveButton(R.string.confirm_delete_income_dialog_positive_button) { dialog, which ->
                        mItemListener.onDelete(mIncome)
                    }
                    .setNeutralButton(R.string.confirm_delete_income_dialog_neutral_button, null)

                val deleteDialog = builder.create()
                deleteDialog.setOnShowListener { deleteDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                                                 deleteDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.BLACK) }

                deleteDialog.show()
                true
            }
            R.id.details_item -> {
                showDetailsDialog()
                true
            }
            else -> false
        }
    }

    private fun showItemMenu() {
        val popUpMenu = PopupMenu(mContext, btnMenu)
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
        val view = LayoutInflater.from(mContext).inflate(R.layout.dialog_details_incomes, null)

        val tvLblNumber = view.findViewById<TextView>(R.id.tv_lbl_number)
        val tvTxtNumber = view.findViewById<TextView>(R.id.tv_txt_number)
        val tvLblEvery = view.findViewById<TextView>(R.id.tv_lbl_every)
        val tvTxtEvery = view.findViewById<TextView>(R.id.tv_txt_every)

        view.findViewById<TextView>(R.id.tv_txt_name).text = mIncome.name
        view.findViewById<TextView>(R.id.tv_txt_description).text = mIncome.description
        view.findViewById<TextView>(R.id.tv_txt_value).text = txtValue
        view.findViewById<TextView>(R.id.tv_txt_date).text = txtDate
        view.findViewById<TextView>(R.id.tv_txt_recurrence).text = mIncomeRecurrence
        tvTxtNumber.text = mIncome.numInstallmentMonths.toString()
        tvTxtEvery.text = mIncome.payFrequency.toString()

        // Installment == 1
        if(mIncomeRecurrence == mRecurrences[1]) {
            tvLblNumber.visibility = View.VISIBLE
            tvTxtNumber.visibility = View.VISIBLE
            tvLblEvery.visibility = View.VISIBLE
            tvTxtEvery.visibility = View.VISIBLE
        }

        builder.setView(view)
        builder.setPositiveButton(mContext.getString(R.string.txt_ok)) { dialog, which -> dialog?.dismiss() }

        val detailsDialog = builder.create()
        detailsDialog.setOnShowListener { detailsDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK) }

        detailsDialog.show()
    }

    private fun getIncomeRecurrence(recurrence: Int): String {
        return when(recurrence) {
            Constants.RECURRENCE.NONE -> mRecurrences[0]
            Constants.RECURRENCE.INSTALLMENT -> mRecurrences[1]
            Constants.RECURRENCE.FIXED_MONTHLY -> mRecurrences[2]
            else -> mRecurrences[0]
        }
    }

}