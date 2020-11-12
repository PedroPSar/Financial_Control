package com.development.pega.financialcontrol.views

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.adapter.DepositOrWithdrawRecyclerViewAdapter
import com.development.pega.financialcontrol.control.AppControl
import com.development.pega.financialcontrol.listener.SavingsMoneyItemListener
import com.development.pega.financialcontrol.model.SavingsMoney
import com.development.pega.financialcontrol.service.Constants
import com.development.pega.financialcontrol.service.dialog.ObjectiveDescriptionDialogFragment
import com.development.pega.financialcontrol.service.dialog.ObjectiveValueDialogFragment
import com.development.pega.financialcontrol.viewmodels.SavingsViewModel
import kotlinx.android.synthetic.main.savings_fragment.*

class SavingsFragment : Fragment(), View.OnClickListener{

    companion object {
        fun newInstance() = SavingsFragment()
    }

    private lateinit var mViewModel: SavingsViewModel
    private lateinit var mViewModelFactory: ViewModelProvider.AndroidViewModelFactory

    private lateinit var mRoot: View
    private lateinit var mBtnDeposit: ImageView
    private lateinit var mBtnWithdraw: ImageView
    private val mDepositAdapter = DepositOrWithdrawRecyclerViewAdapter()
    private val mWithdrawalsAdapter = DepositOrWithdrawRecyclerViewAdapter()

    private lateinit var mSharedPreferences: SharedPreferences

    private lateinit var mValueDialogListener: ObjectiveValueDialogFragment.ObjectiveValueDialogListener
    private lateinit var mDescriptionDialogListener: ObjectiveDescriptionDialogFragment.ObjectiveDescriptionDialogListener
    private lateinit var mSavingsMoneyItemListener: SavingsMoneyItemListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mRoot = inflater.inflate(R.layout.savings_fragment, container, false)
        return mRoot
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val application = requireActivity().application
        mViewModelFactory = ViewModelProvider.AndroidViewModelFactory(application)
        mViewModel = ViewModelProvider(this).get(SavingsViewModel::class.java)

        mBtnDeposit = mRoot.findViewById(R.id.btn_deposit)
        mBtnWithdraw = mRoot.findViewById(R.id.btn_withdraw)

        mSharedPreferences = activity?.getPreferences(Context.MODE_PRIVATE)!!

        setListeners()
        adapters()
        setListenersOnAdapter()
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
            R.id.btn_objective_description -> editObjectiveDescription()
        }
    }

    private fun setListeners() {
        btn_deposit.setOnClickListener(this)
        btn_withdraw.setOnClickListener(this)
        btn_objective.setOnClickListener(this)
        btn_objective_description.setOnClickListener(this)

        mValueDialogListener = object : ObjectiveValueDialogFragment.ObjectiveValueDialogListener {
            override fun onObjValueDialogPositiveClick(dialog: DialogFragment, value: String) {

                if(value.isEmpty()) {
                    AppControl.Validator.makeEmptyRequiredFieldToast(requireContext())
                }else {
                    mViewModel.saveObjectiveValue(value)
                    mViewModel.setSavingsAmount()
                }

            }
        }

        mDescriptionDialogListener = object : ObjectiveDescriptionDialogFragment.ObjectiveDescriptionDialogListener {
            override fun onObjDescriptionDialogPositiveClick(dialog: DialogFragment, description: String) {
                mViewModel.saveObjectiveDescription(description)
                mViewModel.setObjectiveDescription()
            }
        }

        mSavingsMoneyItemListener = object : SavingsMoneyItemListener {
            override fun onEdit(id: Int) {
                val intent = Intent(requireContext(), DepositOrWithdrawActivity::class.java)
                val bundle = Bundle()
                bundle.putInt(Constants.ITEM_ID, id)

                intent.putExtras(bundle)
                startActivity(intent)
            }

            override fun onDelete(savingsMoney: SavingsMoney) {
                mViewModel.deleteSavingsMoney(savingsMoney)
                mViewModel.setDepositRecyclerViewInfo()
                mViewModel.setWithdrawalsRecyclerViewInfo()
            }

        }

    }

    private fun setListenersOnAdapter() {
        mDepositAdapter.attachListener(mSavingsMoneyItemListener)
        mWithdrawalsAdapter.attachListener(mSavingsMoneyItemListener)
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
        val depositRV = mRoot.findViewById<RecyclerView>(R.id.rv_deposits)
        depositRV.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        depositRV.layoutManager = LinearLayoutManager(context)
        depositRV.adapter = mDepositAdapter
    }

    private fun setInfoInWithdrawRecyclerView() {
        val withdrawRV = mRoot.findViewById<RecyclerView>(R.id.rv_withdrawals)
        withdrawRV.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        withdrawRV.layoutManager = LinearLayoutManager(context)
        withdrawRV.adapter = mWithdrawalsAdapter
    }

    private fun editObjectiveValue() {
        val objValueDialog = ObjectiveValueDialogFragment()
        objValueDialog.onAttach(mValueDialogListener)
        objValueDialog.show(requireActivity().supportFragmentManager, "ObjectiveValueDialog")
    }

    private fun editObjectiveDescription() {
        val objDescriptionDialog = ObjectiveDescriptionDialogFragment()
        objDescriptionDialog.onAttach(mDescriptionDialogListener)
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
            num_total_deposits.text = AppControl.Text.convertFloatToCurrencyText(it)
        })

        mViewModel.withdrawalsTotal.observe(viewLifecycleOwner, Observer {
            num_total_withdrawals.text = AppControl.Text.convertFloatToCurrencyText(it)
        })

        mViewModel.savingsAmount.observe(viewLifecycleOwner, Observer {
            tv_savings_amount.text = it
        })

        mViewModel.amountPercent.observe(viewLifecycleOwner, Observer {
            tv_amount_percent.text = it
        })

        mViewModel.amountProgressBar.observe(viewLifecycleOwner, Observer {
            progress_horizontal_savings_amount.progress = it
        })

        mViewModel.objectiveDescription.observe(viewLifecycleOwner, Observer {
            tv_objective_description.text = it
        })
    }

}