package com.development.pega.financialcontrol.views

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.provider.SyncStateContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.adapter.DepositOrWithdrawRecyclerViewAdapter
import com.development.pega.financialcontrol.service.Constants
import com.development.pega.financialcontrol.service.dialog.ObjectiveDescriptionDialogFragment
import com.development.pega.financialcontrol.service.dialog.ObjectiveValueDialogFragment
import com.development.pega.financialcontrol.viewmodels.HomeViewModel
import com.development.pega.financialcontrol.viewmodels.SavingsViewModel
import kotlinx.android.synthetic.main.savings_fragment.*

class SavingsFragment : Fragment(), View.OnClickListener, ObjectiveValueDialogFragment.ObjectiveValueDialogListener {

    companion object {
        fun newInstance() = SavingsFragment()
    }

    private lateinit var mViewModel: SavingsViewModel
    private lateinit var mViewModelFactory: ViewModelProvider.AndroidViewModelFactory

    private lateinit var root: View
    private lateinit var btnDeposit: ImageView
    private lateinit var btnWithdraw: ImageView
    private val mDepositAdapter = DepositOrWithdrawRecyclerViewAdapter()
    private val mWithdrawalsAdapter = DepositOrWithdrawRecyclerViewAdapter()

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.savings_fragment, container, false)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val application = requireActivity().application
        mViewModelFactory = ViewModelProvider.AndroidViewModelFactory(application)
        mViewModel = ViewModelProvider(this).get(SavingsViewModel::class.java)

        btnDeposit = root.findViewById(R.id.btn_deposit)
        btnWithdraw = root.findViewById(R.id.btn_withdraw)

        sharedPreferences = activity?.getPreferences(Context.MODE_PRIVATE)!!

        setListeners()
        adapters()
        observers()
    }

    override fun onResume() {
        super.onResume()
        mViewModel.setDepositRecyclerViewInfo()
        mViewModel.setWithdrawalsRecyclerViewInfo()
        mViewModel.setDepositsTotal()
        mViewModel.setWithdrawalsTotal()
        mViewModel.setSavingsAmount()
        mViewModel.setObjectiveDescription()
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.btn_deposit -> setDepositWithdrawIntent(Constants.SAVINGS_MONEY.DEPOSIT)
            R.id.btn_withdraw -> setDepositWithdrawIntent(Constants.SAVINGS_MONEY.WITHDRAW)
            R.id.btn_objective -> editObjectiveValue()
        }
    }

    override fun onObjValueDialogPositiveClick(dialog: DialogFragment, value: String) {
        mViewModel.saveObjectiveValue(value)
        mViewModel.setSavingsAmount()
    }

    private fun setListeners() {
        btn_deposit.setOnClickListener(this)
        btn_withdraw.setOnClickListener(this)
        btn_objective.setOnClickListener(this)
        btn_objective_description.setOnClickListener(this)
    }

    private fun setDepositWithdrawIntent(type: Int) {
        val intent = Intent(context, DepositOrWithdrawActivity::class.java)
        intent.putExtra("type", type)
        startActivity(intent)
    }

    private fun adapters() {
        setInfoInDepositRecyclerView()
        setInfoInWithdrawRecyclerView()
    }

    private fun setInfoInDepositRecyclerView() {
        val depositRV = root.findViewById<RecyclerView>(R.id.rv_deposits)
        depositRV.layoutManager = LinearLayoutManager(context)
        depositRV.adapter = mDepositAdapter
    }

    private fun setInfoInWithdrawRecyclerView() {
        val withdrawRV = root.findViewById<RecyclerView>(R.id.rv_withdrawals)
        withdrawRV.layoutManager = LinearLayoutManager(context)
        withdrawRV.adapter = mWithdrawalsAdapter
    }

    private fun editObjectiveValue() {
        val objValueDialog = ObjectiveValueDialogFragment()
        objValueDialog.show(requireActivity().supportFragmentManager, "ObjectiveValueDialog")
    }

    private fun editObjectiveDescription() {
        val objDescriptionDialog = ObjectiveDescriptionDialogFragment()
        objDescriptionDialog.show(requireActivity().supportFragmentManager, "ObjectiveDescriptionDialog")
    }

    private fun observers() {
        mViewModel.depositRecyclerViewInfo.observe(viewLifecycleOwner, Observer {
            mDepositAdapter.updateSavingsMoneyList(it)
        })

        mViewModel.withdrawalsRecyclerViewInfo.observe(viewLifecycleOwner, Observer {
            mWithdrawalsAdapter.updateSavingsMoneyList(it)
        })

        mViewModel.depositsTotal.observe(viewLifecycleOwner, Observer {
            num_total_deposits.text = it.toString()
        })

        mViewModel.withdrawalsTotal.observe(viewLifecycleOwner, Observer {
            num_total_withdrawals.text = it.toString()
        })

        mViewModel.savingsAmount.observe(viewLifecycleOwner, Observer {
            tv_savings_amount.text = it
        })

        mViewModel.amountPercent.observe(viewLifecycleOwner, Observer {
            tv_amount_percent.text = it
        })

        mViewModel.objectiveDescription.observe(viewLifecycleOwner, Observer {
            tv_objective_description.text = it
        })
    }

}