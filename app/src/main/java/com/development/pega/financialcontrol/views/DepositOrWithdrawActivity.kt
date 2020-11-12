package com.development.pega.financialcontrol.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.control.AppControl
import com.development.pega.financialcontrol.model.SavingsMoney
import com.development.pega.financialcontrol.service.Constants
import com.development.pega.financialcontrol.viewmodels.DepositOrWithdrawViewModel
import kotlinx.android.synthetic.main.activity_deposit_or_withdraw.*
import java.util.*

class DepositOrWithdrawActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mViewModel: DepositOrWithdrawViewModel
    private lateinit var mViewModelFactory: ViewModelProvider.AndroidViewModelFactory

    private lateinit var toolbar: Toolbar
    private var type = Constants.SAVINGS_MONEY.DEPOSIT
    private lateinit var calendar: Calendar
    private var mItemId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deposit_or_withdraw)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        mViewModelFactory = ViewModelProvider.AndroidViewModelFactory(application)
        mViewModel = ViewModelProvider(this, mViewModelFactory).get(DepositOrWithdrawViewModel::class.java)

        mViewModel.getCurrentDate()

        getInfoFromIntent()
        setListeners()
        observers()
        loadData()
        setInfo()
    }

    override fun onClick(v: View) {
        if(v.id == R.id.btn_deposit_or_withdraw) {

            if(edit_description.text.toString().isEmpty() || edit_value.text.toString().isEmpty()) {
                AppControl.Validator.makeEmptyRequiredFieldToast(this)

            }else if(type == Constants.SAVINGS_MONEY.WITHDRAW) {
                if( mViewModel.checkIfHaveEnoughMoney(edit_value.text.toString()) ) {
                    saveDepositOrWithdraw()
                }else {
                    AppControl.Validator.makeNotEnoughMoneyToast(this)
                }
            }else if(type == Constants.SAVINGS_MONEY.DEPOSIT){
                saveDepositOrWithdraw()
            }

        }else if(v.id == R.id.btn_change_date) {
            changeDate()
        }
    }

    private fun observers() {
        mViewModel.currentTime.observe(this, Observer {
            txt_date.text = it
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
            edit_value.setText(AppControl.Text.convertValueForCurrencyEditText(it.money))
            txt_date.text = AppControl.Text.setDateText(it.day, it.month, it.year)
            edit_description.setText(it.description)
            type = it.type
        })
    }

    private fun setListeners() {
        btn_deposit_or_withdraw.setOnClickListener(this)
        btn_change_date.setOnClickListener(this)
    }

    private fun getInfoFromIntent() {
        val intent = intent
        type = intent.getIntExtra("type", Constants.SAVINGS_MONEY.DEPOSIT)
    }

    private fun setInfo() {
        when(type) {
            Constants.SAVINGS_MONEY.DEPOSIT -> {
                tv_deposit_or_withdraw.text = getString(R.string.btn_deposit_txt)
                btn_deposit_or_withdraw.text = getString(R.string.btn_deposit_txt)
            }

            Constants.SAVINGS_MONEY.WITHDRAW -> {
                tv_deposit_or_withdraw.text = getString(R.string.btn_withdraw_txt)
                btn_deposit_or_withdraw.text = getString(R.string.btn_withdraw_txt)
            }
        }
    }

    private fun saveDepositOrWithdraw() {
        val money = SavingsMoney()

        calendar = AppControl.calendarSetTime(txt_date.text.toString())
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        val moneyValue = AppControl.Text.convertCurrencyTextToFloat( edit_value.text.toString() )
        money.id = mItemId
        money.money = moneyValue
        money.day = day
        money.month = month
        money.year = year
        money.description = edit_description.text.toString()
        money.type = type

        mViewModel.saveDepositOrWithdraw(money)
    }

    private fun changeDate() {
        mViewModel.showDatePickerDialog(this)
    }

    private fun loadData() {
        val bundle = intent.extras
        if(bundle != null) {
            mItemId = bundle.getInt(Constants.ITEM_ID)
            mViewModel.loadSavingsMoney(mItemId)
        }
    }
}