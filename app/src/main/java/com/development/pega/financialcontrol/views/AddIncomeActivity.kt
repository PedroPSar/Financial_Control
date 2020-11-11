package com.development.pega.financialcontrol.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.transition.Fade
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.R.*
import com.development.pega.financialcontrol.control.AppControl
import com.development.pega.financialcontrol.model.Income
import com.development.pega.financialcontrol.service.Constants
import com.development.pega.financialcontrol.viewmodels.AddIncomeViewModel
import kotlinx.android.synthetic.main.activity_add_income.*
import kotlinx.android.synthetic.main.activity_add_income.btn_add
import kotlinx.android.synthetic.main.activity_add_income.btn_change_date
import kotlinx.android.synthetic.main.activity_add_income.edit_many_times
import java.util.*

class AddIncomeActivity : AppCompatActivity(), View.OnClickListener, AdapterView.OnItemSelectedListener {

    private lateinit var mViewModel: AddIncomeViewModel
    private lateinit var mViewModelFactory: ViewModelProvider.AndroidViewModelFactory
    private var recurrenceOption = Constants.RECURRENCE.NONE
    private var everyMonth = 1
    private var numInstallmentMonths = 0
    private var mItemId = 0

    private lateinit var spinner: Spinner
    private lateinit var spinnerEveryMonth: Spinner

    private lateinit var calendar: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_add_income)

        mViewModelFactory = ViewModelProvider.AndroidViewModelFactory(application)
        mViewModel = ViewModelProvider(this, mViewModelFactory).get(AddIncomeViewModel::class.java)

        mViewModel.getCurrentDate()

        observers()
        setSpinner()
        setListeners()
        loadData()
    }

    override fun onClick(v: View) {
        if(v.id == id.btn_change_date) {
            mViewModel.showDatePickerDialog(this)

        } else if(v.id == id.btn_add) {

            if(edit_income_name.text.toString().isEmpty() || edit_income_value.text.toString().isEmpty()) {
                AppControl.Validator.makeEmptyRequiredFieldToast(this)
            }else {
                addIncome()
            }

        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        if(parent.id == R.id.spinner_income_recurrence) {
            recurrenceOption = AppControl.getRecurrence(position)

            if(recurrenceOption == Constants.RECURRENCE.INSTALLMENT) {
                val transition: Transition = Fade()
                transition.duration = 3000
                transition.addTarget(R.id.cl_pay_installment_income)

                TransitionManager.beginDelayedTransition(parent, transition)
                cl_pay_installment_income.visibility = View.VISIBLE
            }else {
                val transition: Transition = Fade()
                transition.duration = 3000
                transition.addTarget(R.id.cl_pay_installment_income)

                TransitionManager.beginDelayedTransition(parent, transition)
                cl_pay_installment_income.visibility = View.GONE
            }

        } else if(parent.id == R.id.spinner_income_every_months) {
            everyMonth = position + 1
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    private fun addIncome() {
        calendar = AppControl.calendarSetTime(txt_income_date.text.toString())
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        val incomeValue = AppControl.Text.convertCurrencyTextToFloat(edit_income_value.text.toString())

        val mIncome = Income()
        mIncome.id = mItemId
        mIncome.name = edit_income_name.text.toString()
        mIncome.description = edit_income_description.text.toString()
        mIncome.value = incomeValue
        mIncome.day = day
        mIncome.month = month
        mIncome.year = year
        mIncome.recurrence = recurrenceOption
        mIncome.payFrequency = everyMonth

        if(edit_many_times.text.toString() != "") {
            mIncome.numInstallmentMonths = edit_many_times.text.toString().toInt()
        }

        mViewModel.saveIncome(mIncome)
    }

    private fun setListeners() {
        btn_change_date.setOnClickListener(this)
        btn_add.setOnClickListener(this)
        spinner.onItemSelectedListener = this
        spinnerEveryMonth.onItemSelectedListener = this
    }

    private fun setSpinner() {
        spinner = findViewById(id.spinner_income_recurrence)
        ArrayAdapter.createFromResource(this, array.spinner_recurrence_options, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinnerEveryMonth = findViewById(id.spinner_income_every_months)
        ArrayAdapter.createFromResource(this, array.every_month_array, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerEveryMonth.adapter = adapter
        }
    }

    private fun observers() {
        mViewModel.currentTime.observe(this, androidx.lifecycle.Observer {
            txt_income_date.text = it
        })

        mViewModel.datePickerDialog.observe(this, androidx.lifecycle.Observer {
            val datePickerDialog = it
            datePickerDialog.show()
        })

        mViewModel.addIncome.observe(this, Observer {
            if(it) {
                AppControl.showToast(this, getString(string.save_success_message))
                finish()
            } else {
                AppControl.showToast(this, getString(string.save_failed_message))
                finish()
            }
        })

        mViewModel.getIncome.observe(this, Observer {
            edit_income_name.setText(it.name)
            edit_income_description.setText(it.description)
            edit_income_value.setText(it.value.toString())
            txt_income_date.text = AppControl.Text.setDateText(it.day, it.month, it.year)
            spinner_income_recurrence.setSelection(it.recurrence)

            if(it.recurrence == Constants.RECURRENCE.INSTALLMENT) {
                cl_pay_installment_income.visibility = View.VISIBLE
                edit_many_times.setText(it.numInstallmentMonths)
                spinner_income_every_months.setSelection(it.payFrequency)
            }
        })
    }

    private fun loadData() {
        val bundle = intent.extras
        if(bundle != null) {
            mItemId = bundle.getInt(Constants.ITEM_ID)
            mViewModel.loadIncome(mItemId)
        }
    }

}