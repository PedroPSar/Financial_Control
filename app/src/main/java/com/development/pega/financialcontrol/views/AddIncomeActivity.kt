package com.development.pega.financialcontrol.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.transition.Fade
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.R.*
import com.development.pega.financialcontrol.control.AppControl
import com.development.pega.financialcontrol.databinding.ActivityAddIncomeBinding
import com.development.pega.financialcontrol.model.Income
import com.development.pega.financialcontrol.service.Constants
import com.development.pega.financialcontrol.viewmodels.AddIncomeViewModel
import java.util.*

class AddIncomeActivity : AppCompatActivity(), View.OnClickListener, AdapterView.OnItemSelectedListener {

    private lateinit var mViewModel: AddIncomeViewModel
    private lateinit var mViewModelFactory: ViewModelProvider.AndroidViewModelFactory
    private var recurrenceOption = Constants.RECURRENCE.NONE
    private var everyMonth = 1
    private var numInstallmentMonths = 0
    private var mItemId = 0
    private var mRelationalID = 0
    private var mInitialValue = "0.00"

    private lateinit var spinner: Spinner
    private lateinit var spinnerEveryMonth: Spinner

    private lateinit var calendar: Calendar
    private lateinit var binding: ActivityAddIncomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddIncomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mViewModelFactory = ViewModelProvider.AndroidViewModelFactory(application)
        mViewModel = ViewModelProvider(this, mViewModelFactory).get(AddIncomeViewModel::class.java)

        mViewModel.getCurrentDate()

        setRequiredMarking()
        observers()
        setSpinner()
        setListeners()
        loadData()
    }

    override fun onClick(v: View) {
        if(v.id == id.btn_change_date) {
            mViewModel.showDatePickerDialog(this)
        } else if(v.id == id.btn_add) {
            if(binding.editIncomeName.text.toString().isEmpty() || binding.editIncomeValue.text.toString().isEmpty()) {
                AppControl.Validator.makeEmptyRequiredFieldToast(this)
            } else {
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
                binding.clPayInstallmentIncome.visibility = View.VISIBLE
            }else {
                val transition: Transition = Fade()
                transition.duration = 3000
                transition.addTarget(R.id.cl_pay_installment_income)

                TransitionManager.beginDelayedTransition(parent, transition)
                binding.clPayInstallmentIncome.visibility = View.GONE
            }

        } else if(parent.id == R.id.spinner_income_every_months) {
            everyMonth = position + 1
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    private fun addIncome() {
        calendar = AppControl.calendarSetTime(binding.txtIncomeDate.text.toString())
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        val incomeValue = AppControl.Text.convertCurrencyTextToFloat( binding.editIncomeValue.text.toString() )

        //If relationalID 0 == edit... != 0 is new save
        if(mRelationalID == 0) {
            mRelationalID = AppControl.getNewRelationalID(this)
        }

        val mIncome = Income()
        mIncome.id = mItemId
        mIncome.name = binding.editIncomeName.text.toString()
        mIncome.description = binding.editIncomeDescription.text.toString()
        mIncome.value = incomeValue
        mIncome.day = day
        mIncome.month = month
        mIncome.year = year
        mIncome.recurrence = recurrenceOption
        mIncome.payFrequency = everyMonth
        mIncome.relationalID = mRelationalID

        if(recurrenceOption == Constants.RECURRENCE.INSTALLMENT) {

            if(binding.editManyTimes.text.toString() != "") {
                mIncome.numInstallmentMonths = binding.editManyTimes.text.toString().toInt()

                if(mIncome.numInstallmentMonths < 2) {
                    AppControl.showToast(this, getString(string.minimum_number_installments))
                    return
                }
            } else {
                AppControl.showToast(this, getString(string.number_field_is_empty))
                return
            }
        }

        if(mIncome.name == getString(string.withdraw_name)) {

            if( AppControl.checkIfHaveEnoughMoneyForWithdrawEdition(mInitialValue, binding.editIncomeValue.text.toString(),  this) ) {
                mViewModel.saveIncome(mIncome)
            }else {
                AppControl.Validator.makeNotEnoughMoneyToast(this)
            }

        } else {
            mViewModel.saveIncome(mIncome)
        }
    }

    private fun setListeners() {
        binding.btnChangeDate.setOnClickListener(this)
        binding.btnAdd.setOnClickListener(this)
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
            binding.txtIncomeDate.text = it
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

            mRelationalID = it.relationalID
            binding.editIncomeName.setText(it.name)
            binding.editIncomeDescription.setText(it.description)
            binding.editIncomeValue.setText(AppControl.Text.convertValueForCurrencyEditText(it.value))
            binding.txtIncomeDate.text = AppControl.Text.setDateText(it.day, it.month, it.year)
            binding.spinnerIncomeRecurrence.setSelection(it.recurrence)

            getIncomeValueForCheckIfMoneyEnough()

            if(it.recurrence == Constants.RECURRENCE.INSTALLMENT) {
                binding.clPayInstallmentIncome.visibility = View.VISIBLE
                binding.editManyTimes.setText(it.numInstallmentMonths.toString())
                binding.spinnerIncomeEveryMonths.setSelection(it.payFrequency - 1)
            }

            if(it.name == getString(string.withdraw_name)) {
                binding.lblIncomeName.visibility = View.GONE
                binding.editIncomeName.visibility = View.GONE
                binding.lblRecurrence.visibility = View.GONE
                binding.spinnerIncomeRecurrence.visibility = View.GONE
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

    private fun getIncomeValueForCheckIfMoneyEnough() {
        if(binding.editIncomeValue.text.toString().isNotEmpty()) {
            mInitialValue = binding.editIncomeValue.text.toString()
        }
    }

    private fun setRequiredMarking() {
        val lblIncome = "${binding.lblIncomeName.text} *"
        val lblValue = "${binding.lblValue.text} *"
        val lblDate = "${binding.lblDate.text} *"
        val lblRecurrence = "${binding.lblRecurrence.text} *"

        binding.lblIncomeName.text = lblIncome
        binding.lblValue.text = lblValue
        binding.lblDate.text = lblDate
        binding.lblRecurrence.text = lblRecurrence
    }

}