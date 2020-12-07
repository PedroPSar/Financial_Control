package com.development.pega.financialcontrol.views

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.adapter.DepositOrWithdrawRecyclerViewAdapter
import com.development.pega.financialcontrol.control.AppControl
import com.development.pega.financialcontrol.databinding.SavingsFragmentBinding
import com.development.pega.financialcontrol.listener.SavingsMoneyItemListener
import com.development.pega.financialcontrol.model.SavingsMoney
import com.development.pega.financialcontrol.service.Constants
import com.development.pega.financialcontrol.service.dialog.ObjectiveDescriptionDialogFragment
import com.development.pega.financialcontrol.service.dialog.ObjectiveValueDialogFragment
import com.development.pega.financialcontrol.service.repository.Prefs
import com.development.pega.financialcontrol.viewmodels.SavingsViewModel

class SavingsFragment : Fragment(), View.OnClickListener{

    companion object {
        fun newInstance() = SavingsFragment()
    }

    private lateinit var mViewModel: SavingsViewModel
    private lateinit var mViewModelFactory: ViewModelProvider.AndroidViewModelFactory
    private lateinit var mPrefs: Prefs

    private val mDepositAdapter = DepositOrWithdrawRecyclerViewAdapter()
    private val mWithdrawalsAdapter = DepositOrWithdrawRecyclerViewAdapter()

    private lateinit var mValueDialogListener: ObjectiveValueDialogFragment.ObjectiveValueDialogListener
    private lateinit var mDescriptionDialogListener: ObjectiveDescriptionDialogFragment.ObjectiveDescriptionDialogListener
    private lateinit var mSavingsMoneyItemListener: SavingsMoneyItemListener

    private var _binding: SavingsFragmentBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = SavingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val application = requireActivity().application
        mViewModelFactory = ViewModelProvider.AndroidViewModelFactory(application)
        mViewModel = ViewModelProvider(this).get(SavingsViewModel::class.java)

        mPrefs = AppControl.getAppPrefs(requireContext())

        setListeners()
        adapters()
        setListenersOnAdapter()
        observers()
    }

    override fun onResume() {
        super.onResume()
        setColors()
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
        binding.btnDeposit.setOnClickListener(this)
        binding.btnWithdraw.setOnClickListener(this)
        binding.btnObjective.setOnClickListener(this)
        binding.btnObjectiveDescription.setOnClickListener(this)

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
                mViewModel.setDepositsTotal()
                mViewModel.setWithdrawalsTotal()
                mViewModel.setSavingsAmount()
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
        binding.rvDeposits.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        binding.rvDeposits.layoutManager = LinearLayoutManager(context)
        binding.rvDeposits.adapter = mDepositAdapter
    }

    private fun setInfoInWithdrawRecyclerView() {
        binding.rvWithdrawals.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        binding.rvWithdrawals.layoutManager = LinearLayoutManager(context)
        binding.rvWithdrawals.adapter = mWithdrawalsAdapter
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
            binding.numTotalDeposits.text = AppControl.Text.convertFloatToCurrencyText(it)
        })

        mViewModel.withdrawalsTotal.observe(viewLifecycleOwner, Observer {
            binding.numTotalWithdrawals.text = AppControl.Text.convertFloatToCurrencyText(it)
        })

        mViewModel.savingsAmount.observe(viewLifecycleOwner, Observer {
            binding.tvSavingsAmount.text = it
        })

        mViewModel.amountPercent.observe(viewLifecycleOwner, Observer {
            binding.tvAmountPercent.text = it
        })

        mViewModel.amountProgressBar.observe(viewLifecycleOwner, Observer {
            binding.progressHorizontalSavingsAmount.progress = it
        })

        mViewModel.objectiveDescription.observe(viewLifecycleOwner, Observer {
            binding.tvObjectiveDescription.text = it
        })
    }

    private fun setColors() {
        binding.lblDeposits.setTextColor(mPrefs.incomesColor)
        binding.btnDeposit.setColorFilter(mPrefs.incomesColor)

        binding.lblWithdrawals.setTextColor(mPrefs.expensesColor)
        binding.btnWithdraw.setColorFilter(mPrefs.expensesColor)
    }

}