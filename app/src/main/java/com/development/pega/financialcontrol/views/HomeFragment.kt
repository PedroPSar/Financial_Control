package com.development.pega.financialcontrol.views

import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.adapter.ExpensesRecyclerViewAdapter
import com.development.pega.financialcontrol.adapter.IncomesRecyclerViewAdapter
import com.development.pega.financialcontrol.control.AppControl
import com.development.pega.financialcontrol.listener.MainListener
import com.development.pega.financialcontrol.viewmodels.HomeViewModel
import kotlinx.android.synthetic.main.home_fragment.*
import kotlin.math.roundToInt

class HomeFragment() : Fragment(), View.OnClickListener {

    private lateinit var viewModel: HomeViewModel
    private lateinit var mViewModelFactory: ViewModelProvider.AndroidViewModelFactory
    private lateinit var root: View
    private lateinit var tvLblMonth: TextView
    private lateinit var tvIncomes: TextView
    private lateinit var tvExpenses: TextView

    private val mIncomesAdapter: IncomesRecyclerViewAdapter = IncomesRecyclerViewAdapter()
    private val mExpensesAdapter: ExpensesRecyclerViewAdapter = ExpensesRecyclerViewAdapter()

    companion object {
        private lateinit var mMainListener: MainListener

        fun newInstance(mainListener: MainListener): HomeFragment{
            mMainListener = mainListener
            return HomeFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.home_fragment, container, false)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val application = requireActivity().application
        mViewModelFactory = ViewModelProvider.AndroidViewModelFactory(application)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        tvLblMonth = root.findViewById(R.id.lbl_month_name)
        tvIncomes = root.findViewById(R.id.txt_incomes)
        tvExpenses = root.findViewById(R.id.txt_expenses)
        viewModel.setCurrentDate()

        setMaxWidthInLayout()
        setOnClick()
        adapters()
        observer()
    }

    override fun onResume() {
        super.onResume()
        viewModel.setIncomesOfMonth()
        viewModel.setExpensesOfMonth()
        viewModel.setIncomesInRecyclerView()
        viewModel.setExpensesInRecyclerView()
        viewModel.calcBalance()
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.btn_img_before -> {
                viewModel.btnBeforeClick()
            }

            R.id.btn_img_next -> {
                viewModel.btnNextClick()
            }
        }
    }

    private fun setOnClick() {
        btn_img_before.setOnClickListener(this)
        btn_img_next.setOnClickListener(this)
    }

    private fun observer() {
        viewModel.month.observe(viewLifecycleOwner, Observer {
            tvLblMonth.text = it
        })

        viewModel.incomes.observe(viewLifecycleOwner, Observer {
            val incomesTxt = AppControl.Text.convertFloatToCurrencyText(it)
            tvIncomes.text = incomesTxt
        })

        viewModel.expenses.observe(viewLifecycleOwner, Observer {
            val expensesTxt = AppControl.Text.convertFloatToCurrencyText(it)
            tvExpenses.text = expensesTxt
        })

        viewModel.recyclerViewIncomes.observe(viewLifecycleOwner, Observer {
            mIncomesAdapter.updateIncomesList(it)
        })

        viewModel.recyclerViewExpenses.observe(viewLifecycleOwner, Observer {
            mExpensesAdapter.updateExpensesList(it)
        })

        viewModel.balance.observe(viewLifecycleOwner, Observer {
            val balanceTxt = AppControl.Text.convertFloatToCurrencyText(it)
            txt_account_balance.text = balanceTxt
        })

        viewModel.year.observe(viewLifecycleOwner, Observer {
            mMainListener.onSetYear(it.toString())
        })
    }

    private fun adapters() {
        setInfoIncomesRecyclerView()
        setInfoExpensesRecyclerView()
    }

    private fun setInfoIncomesRecyclerView() {
        val incomesRV = root.findViewById<RecyclerView>(R.id.rv_incomes)
        incomesRV.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        incomesRV.layoutManager = LinearLayoutManager(context)
        incomesRV.adapter = mIncomesAdapter
    }

    private fun setInfoExpensesRecyclerView() {
        val expensesRV = root.findViewById<RecyclerView>(R.id.rv_expenses)
        expensesRV.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        expensesRV.layoutManager = LinearLayoutManager(context)
        expensesRV.adapter = mExpensesAdapter
    }

    private fun setMaxWidthInLayout() {
        val metrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(metrics)

        var width = metrics.widthPixels
        width = (width - (width * 0.10f) ).roundToInt()
        val maxWidth = width / 3

        cl_incomes.maxWidth = maxWidth
        cl_expenses.maxWidth = maxWidth
        cl_account_balance.maxWidth = maxWidth

    }
}