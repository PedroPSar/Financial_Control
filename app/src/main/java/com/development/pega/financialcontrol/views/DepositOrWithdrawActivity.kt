package com.development.pega.financialcontrol.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.control.AppControl
import com.development.pega.financialcontrol.databinding.ActivityDepositOrWithdrawBinding
import com.development.pega.financialcontrol.model.SavingsMoney
import com.development.pega.financialcontrol.service.Constants
import com.development.pega.financialcontrol.viewmodels.DepositOrWithdrawViewModel
import java.util.*

class DepositOrWithdrawActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mViewModel: DepositOrWithdrawViewModel
    private lateinit var mViewModelFactory: ViewModelProvider.AndroidViewModelFactory

    private lateinit var toolbar: Toolbar
    private var type = Constants.SAVINGS_MONEY.DEPOSIT
    private lateinit var calendar: Calendar
    private var mItemId = 0
    private var mRelationalID = 0
    private var mInitialValue = "0.00"

    private lateinit var binding: ActivityDepositOrWithdrawBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDepositOrWithdrawBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        mViewModelFactory = ViewModelProvider.AndroidViewModelFactory(application)
        mViewModel = ViewModelProvider(this, mViewModelFactory).get(DepositOrWithdrawViewModel::class.java)

        mViewModel.getCurrentDate()

        setRequiredMarking()
        getInfoFromIntent()
        setListeners()
        observers()
        loadData()
        setInfo()
    }

    override fun onClick(v: View) {
        if(v.id == R.id.btn_deposit_or_withdraw) {

            if(binding.editDescription.text.toString().isEmpty() || binding.editValue.text.toString().isEmpty()) {
                AppControl.Validator.makeEmptyRequiredFieldToast(this)

            }else if(type == Constants.SAVINGS_MONEY.WITHDRAW) {
                // if mItem id == 0 is not edition
                if(mItemId == 0) {

                    if( AppControl.checkIfHaveEnoughMoney(binding.editValue.text.toString(), this) ) {
                        saveDepositOrWithdraw()
                    }else {
                        AppControl.Validator.makeNotEnoughMoneyToast(this)
                    }

                } else {

                    if( AppControl.checkIfHaveEnoughMoneyForWithdrawEdition(mInitialValue, binding.editValue.text.toString(), this) ) {
                        saveDepositOrWithdraw()
                    } else {
                        AppControl.Validator.makeNotEnoughMoneyToast(this)
                    }

                }

            }else if(type == Constants.SAVINGS_MONEY.DEPOSIT){
                // if mItem id == 0 is not edition
                if(mItemId == 0) {
                    saveDepositOrWithdraw()
                } else {
                    if( AppControl.checkIfHaveEnoughMoneyForDepositEdition(mInitialValue, binding.editValue.text.toString(), this)) {
                        saveDepositOrWithdraw()
                    } else {
                        AppControl.Validator.makeNotEnoughMoneyToast(this)
                    }
                }

            }

        }else if(v.id == R.id.btn_change_date) {
            changeDate()
        }
    }

    private fun observers() {
        mViewModel.currentTime.observe(this, Observer {
            binding.txtDate.text = it
        })

        mViewModel.datePickerDialog.observe(this, Observer {
            val datePickerDialog = it
            datePickerDialog.show()
        })

        mViewModel.depositOrWithdraw.observe(this, Observer {
            if(it) {
                AppControl.showToast(this, getString(R.string.save_success_message))
                finish()
            } else {
                AppControl.showToast(this, getString(R.string.save_failed_message))
                finish()
            }
        })

        mViewModel.getSavings.observe(this, Observer {
            mRelationalID = it.relationalID
            binding.editValue.setText(AppControl.Text.convertValueForCurrencyEditText(it.money))
            binding.txtDate.text = AppControl.Text.setDateText(it.day, it.month, it.year)
            binding.editDescription.setText(it.description)
            type = it.type

            mInitialValue = AppControl.Text.convertValueForCurrencyEditText(it.money)
        })
    }

    private fun setListeners() {
        binding.btnDepositOrWithdraw.setOnClickListener(this)
        binding.btnChangeDate.setOnClickListener(this)
    }

    private fun getInfoFromIntent() {
        val intent = intent
        type = intent.getIntExtra("type", Constants.SAVINGS_MONEY.DEPOSIT)
    }

    private fun setInfo() {
        when(type) {
            Constants.SAVINGS_MONEY.DEPOSIT -> {
                binding.tvDepositOrWithdraw.text = getString(R.string.btn_deposit_txt)
                binding.btnDepositOrWithdraw.text = getString(R.string.btn_deposit_txt)
            }

            Constants.SAVINGS_MONEY.WITHDRAW -> {
                binding.tvDepositOrWithdraw.text = getString(R.string.btn_withdraw_txt)
                binding.btnDepositOrWithdraw.text = getString(R.string.btn_withdraw_txt)
            }
        }
    }

    private fun saveDepositOrWithdraw() {
        val money = SavingsMoney()

        calendar = AppControl.calendarSetTime(binding.txtDate.text.toString())
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        val moneyValue = AppControl.Text.convertCurrencyTextToFloat( binding.editValue.text.toString() )

        if(mRelationalID == 0) {
            mRelationalID = AppControl.getNewRelationalID(this)
        }

        money.id = mItemId
        money.money = moneyValue
        money.day = day
        money.month = month
        money.year = year
        money.description = binding.editDescription.text.toString()
        money.type = type
        money.relationalID = mRelationalID

        mViewModel.saveDepositOrWithdraw(money)
    }

    private fun changeDate() {
        mViewModel.showDatePickerDialog(this)
    }

    private fun loadData() {
        val bundle = intent.extras
        if(bundle != null) {
            mItemId = bundle.getInt(Constants.ITEM_ID)
            if(mItemId != null && mItemId != 0) {
                mViewModel.loadSavingsMoney(mItemId)
            }
        }

    }

    private fun setRequiredMarking() {
        val lblValue = "${binding.lblDepositOrWithdrawValue.text} *"
        val lblDate = "${binding.lblDate.text} *"
        val lblDescription = "${binding.lblDescription.text} *"

        binding.lblDepositOrWithdrawValue.text = lblValue
        binding.lblDate.text = lblDate
        binding.lblDescription.text = lblDescription
    }
}