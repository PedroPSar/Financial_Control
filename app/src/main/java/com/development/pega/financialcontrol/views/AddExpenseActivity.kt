package com.development.pega.financialcontrol.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.transition.Fade
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.control.AppControl
import com.development.pega.financialcontrol.databinding.ActivityAddExpenseBinding
import com.development.pega.financialcontrol.model.Expense
import com.development.pega.financialcontrol.service.Constants
import com.development.pega.financialcontrol.viewmodels.AddExpenseViewModel
import java.util.*
import kotlin.math.exp

class AddExpenseActivity : AppCompatActivity(), View.OnClickListener, AdapterView.OnItemSelectedListener {

    private lateinit var mViewModel: AddExpenseViewModel
    private lateinit var mViewModelFactory: ViewModelProvider.AndroidViewModelFactory

    private lateinit var spinnerRecurrence: Spinner
    private lateinit var spinnerType: Spinner
    private lateinit var spinnerEveryMonth: Spinner

    private var typeOptions = Constants.TYPE.NOT_REQUIRED
    private var recurrenceOptions = Constants.RECURRENCE.NONE
    private var everyMonth = 1
    private var numInstallmentMonths = 0
    private var mItemId = 0
    private var mRelationalID = 0
    private var mInitialValue = "0.00"

    private lateinit var calendar: Calendar
    private lateinit var binding: ActivityAddExpenseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mViewModelFactory = ViewModelProvider.AndroidViewModelFactory(application)
        mViewModel = ViewModelProvider(this, mViewModelFactory).get(AddExpenseViewModel::class.java)

        mViewModel.getCurrentDate()

        setRequiredMarking()
        observers()
        setSpinners()
        setListeners()
        loadData()
        setTextWatcherInEditTexts()
    }

    override fun onClick(v: View) {
        if(v.id == R.id.btn_change_date) {
            mViewModel.showDatePickerDialog(this)

        } else if(v.id == R.id.btn_add) {

            if(binding.editExpenseName.text.toString().isEmpty() || binding.editExpenseValue.text.toString().isEmpty()) {
                AppControl.Validator.makeEmptyRequiredFieldToast(this)
            }else {
                addExpense()
            }

        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        if(parent.id == R.id.spinner_type) {
            typeOptions = AppControl.getType(position)
        }else if(parent.id == R.id.spinner_expense_recurrence) {
            recurrenceOptions = AppControl.getRecurrence(position)

            if(recurrenceOptions == Constants.RECURRENCE.INSTALLMENT) {
                val transition: Transition = Fade()
                transition.duration = 3000
                transition.addTarget(R.id.cl_pay_installment_expense)

                TransitionManager.beginDelayedTransition(parent, transition)
                binding.clPayInstallmentExpense.visibility = View.VISIBLE

                binding.lblIsPaid.visibility = View.GONE
                binding.checkboxIsPaid.visibility = View.GONE
                binding.clDatesFixedMonthExpense.visibility = View.GONE

            } else if(recurrenceOptions == Constants.RECURRENCE.FIXED_MONTHLY) {
                binding.lblIsPaid.visibility = View.GONE
                binding.checkboxIsPaid.visibility = View.GONE
                binding.clDatesFixedMonthExpense.visibility = View.VISIBLE

            } else {
                val transition: Transition = Fade()
                transition.duration = 3000
                transition.addTarget(R.id.cl_pay_installment_expense)

                TransitionManager.beginDelayedTransition(parent, transition)
                binding.clPayInstallmentExpense.visibility = View.GONE

                binding.lblIsPaid.visibility = View.VISIBLE
                binding.checkboxIsPaid.visibility = View.VISIBLE
                binding.clDatesFixedMonthExpense.visibility = View.GONE
            }
        }else if(parent.id == R.id.spinner_expense_every_months) {
            everyMonth = position + 1
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    private fun addExpense() {
        calendar = AppControl.calendarSetTime(binding.txtExpenseDate.text.toString())
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        val expenseValue = AppControl.Text.convertCurrencyTextToFloat( binding.editExpenseValue.text.toString() )

        if(mRelationalID == 0) {
            mRelationalID = AppControl.getNewRelationalID(this)
        }

        val isPaid = if(binding.checkboxIsPaid.isChecked) Constants.IS_PAID.YES else Constants.IS_PAID.NO

        val expense = Expense()
        expense.id = mItemId
        expense.type = typeOptions
        expense.day = day
        expense.month = month
        expense.year = year
        expense.value = expenseValue
        expense.name = binding.editExpenseName.text.toString()
        expense.description = binding.editExpenseDescription.text.toString()
        expense.recurrence = recurrenceOptions
        expense.payFrequency = everyMonth
        expense.relationalID = mRelationalID
        expense.paid = isPaid

        if(recurrenceOptions == Constants.RECURRENCE.INSTALLMENT) {

            val edManyTimesTxt = binding.editManyTimes.text.toString()
            var edNumPaidInstallments = binding.editNumPaidInstallments.text.toString()

            if(edManyTimesTxt.isNotEmpty()) {

                if(edNumPaidInstallments == "") edNumPaidInstallments = "0"

                var numPaidInstallments = edNumPaidInstallments.toInt()
                val numInstallmentMonths = edManyTimesTxt.toInt()

                if (numPaidInstallments >= numInstallmentMonths) {
                    numPaidInstallments = numInstallmentMonths
                    binding.checkboxIsPaid.isChecked = true
                    expense.paid = Constants.IS_PAID.YES
                } else {
                    binding.checkboxIsPaid.isChecked = false
                    expense.paid = Constants.IS_PAID.NO
                }

                expense.numInstallmentMonths = numInstallmentMonths
                expense.numPaidInstallments = numPaidInstallments

                if(numInstallmentMonths < 2) {
                    AppControl.showToast(this, getString(R.string.minimum_number_installments))
                    return
                }
            } else {
                AppControl.showToast(this, getString(R.string.number_field_is_empty))
                return
            }
        }

        Log.d("teste", "numPaid: ${expense.numPaidInstallments}")

        if(expense.name == getString(R.string.deposit_name)) {
            if( AppControl.checkIfHaveEnoughMoneyForDepositEdition(mInitialValue, binding.editExpenseValue.text.toString(), this) ) {
                mViewModel.saveExpense(expense)
            }else {
                AppControl.Validator.makeNotEnoughMoneyToast(this)
            }

        } else {
            mViewModel.saveExpense(expense)
        }
    }

    private fun setListeners() {
        binding.btnChangeDate.setOnClickListener(this)
        binding.btnAdd.setOnClickListener(this)
        spinnerRecurrence.onItemSelectedListener = this
        spinnerType.onItemSelectedListener = this
        spinnerEveryMonth.onItemSelectedListener = this
    }

    private fun setSpinners() {
        spinnerRecurrence = findViewById(R.id.spinner_expense_recurrence)
        ArrayAdapter.createFromResource(this, R.array.spinner_recurrence_options, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerRecurrence.adapter = adapter
        }

        spinnerType = findViewById(R.id.spinner_type)
        ArrayAdapter.createFromResource(this, R.array.spinner_type_options, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerType.adapter = adapter
        }

        spinnerEveryMonth = findViewById(R.id.spinner_expense_every_months)
        ArrayAdapter.createFromResource(this, R.array.every_month_array, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerEveryMonth.adapter = adapter
        }
    }

    private fun observers() {
        mViewModel.currentTime.observe(this, androidx.lifecycle.Observer {
            binding.txtExpenseDate.text = it
        })

        mViewModel.datePickerDialog.observe(this, androidx.lifecycle.Observer {
            val datePickerDialog = it
            datePickerDialog.show()
        })

        mViewModel.saveExpense.observe(this, Observer {
            if(it) {
                AppControl.showToast(this, getString(R.string.save_success_message))
                finish()
            } else {
                AppControl.showToast(this, getString(R.string.save_failed_message))
                finish()
            }
        })

        mViewModel.getExpense.observe(this, Observer {

            mRelationalID = it.relationalID
            binding.editExpenseName.setText(it.name)
            binding.spinnerType.setSelection(it.type)
            binding.editExpenseDescription.setText(it.description)
            binding.editExpenseValue.setText(AppControl.Text.convertValueForCurrencyEditText(it.value))
            binding.txtExpenseDate.text = AppControl.Text.setDateText(it.day, it.month, it.year)
            binding.spinnerExpenseRecurrence.setSelection(it.recurrence)
            binding.checkboxIsPaid.isChecked = it.paid == Constants.IS_PAID.YES // 1 is paid, 0 unpaid

            binding.editNumPaidInstallments.text = Editable.Factory.getInstance().newEditable(it.numPaidInstallments.toString())

            if(it.paid == Constants.IS_PAID.YES) binding.editNumPaidInstallments.text = Editable.Factory.getInstance().newEditable(it.numInstallmentMonths.toString())

            getExpenseValueForCheckIfMoneyEnough()

            if(it.recurrence == Constants.RECURRENCE.INSTALLMENT) {
                binding.clPayInstallmentExpense.visibility = View.VISIBLE
                binding.editManyTimes.setText(it.numInstallmentMonths.toString())
                binding.spinnerExpenseEveryMonths.setSelection(it.payFrequency - 1)
            }

            if(it.name == getString(R.string.deposit_name)) {
                binding.lblExpenseName.visibility = View.GONE
                binding.editExpenseName.visibility = View.GONE
                binding.lblType.visibility = View.GONE
                binding.spinnerType.visibility = View.GONE
                binding.lblRecurrence.visibility = View.GONE
                binding.spinnerExpenseRecurrence.visibility = View.GONE
            }
        })
    }

    private fun loadData() {
        val bundle = intent.extras
        if(bundle != null) {
            mItemId = bundle.getInt(Constants.ITEM_ID)
            mViewModel.loadExpense(mItemId)
        }
    }

    private fun getExpenseValueForCheckIfMoneyEnough() {
        if(binding.editExpenseValue.text.toString().isNotEmpty()) {
            mInitialValue = binding.editExpenseValue.text.toString()
        }
    }

    private fun setRequiredMarking() {
        val lblName = "${binding.lblExpenseName.text} *"
        val lblType = "${binding.lblType.text} *"
        val lblValue = "${binding.lblValue.text} *"
        val lblDate = "${binding.lblDate.text} *"
        val lblRecurrence = "${binding.lblRecurrence.text} *"
        val lblNumber = "${binding.lblNumber.text} *"

        binding.lblExpenseName.text = lblName
        binding.lblType.text = lblType
        binding.lblValue.text = lblValue
        binding.lblDate.text = lblDate
        binding.lblRecurrence.text = lblRecurrence
        binding.lblNumber.text = lblNumber
    }

    private fun setTextWatcherInEditTexts() {
        setTextWatcherInEditStartMonth()
        setTextWatcherInEditEndMonth()
    }

    private fun setTextWatcherInEditStartMonth() {
        binding.editStartMonth.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    setMonthLimit(binding.editStartMonth, s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    private fun setTextWatcherInEditEndMonth() {
        binding.editEndMonth.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setMonthLimit(binding.editEndMonth, s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    private fun setMonthLimit(editText: EditText, s: String) {
        if(s.isNotEmpty()) {
            if(s.toInt() > 12) {
                editText.setText("12")
            }
        }
    }

}