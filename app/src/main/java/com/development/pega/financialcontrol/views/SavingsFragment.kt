package com.development.pega.financialcontrol.views

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.provider.SyncStateContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.service.Constants
import com.development.pega.financialcontrol.viewmodels.HomeViewModel
import com.development.pega.financialcontrol.viewmodels.SavingsViewModel
import kotlinx.android.synthetic.main.savings_fragment.*

class SavingsFragment : Fragment(), View.OnClickListener {

    companion object {
        fun newInstance() = SavingsFragment()
    }

    private lateinit var viewModel: SavingsViewModel
    private lateinit var mViewModelFactory: ViewModelProvider.AndroidViewModelFactory

    private lateinit var root: View
    private lateinit var btnDeposit: Button
    private lateinit var btnWithdraw: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.savings_fragment, container, false)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val application = requireActivity().application
        mViewModelFactory = ViewModelProvider.AndroidViewModelFactory(application)
        viewModel = ViewModelProvider(this).get(SavingsViewModel::class.java)

        btnDeposit = root.findViewById(R.id.btn_deposit)
        btnWithdraw = root.findViewById(R.id.btn_withdraw)

        setListeners()
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.btn_deposit -> setDepositWithdrawIntent(Constants.SAVINGS_MONEY.DEPOSIT)
            R.id.btn_withdraw -> setDepositWithdrawIntent(Constants.SAVINGS_MONEY.WITHDRAW)
        }
    }

    private fun setListeners() {
        btn_deposit.setOnClickListener(this)
        btn_withdraw.setOnClickListener(this)
    }

    private fun setDepositWithdrawIntent(type: Int) {
        val intent = Intent(context, DepositOrWithdrawActivity::class.java)
        intent.putExtra("type", type)
        startActivity(intent)
    }

}