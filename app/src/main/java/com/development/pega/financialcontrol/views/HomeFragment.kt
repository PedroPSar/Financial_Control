package com.development.pega.financialcontrol.views

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.adapter.ExpensesRecyclerViewAdapter
import com.development.pega.financialcontrol.adapter.IncomesRecyclerViewAdapter
import com.development.pega.financialcontrol.listener.MainListener
import com.development.pega.financialcontrol.viewmodels.HomeViewModel
import kotlinx.android.synthetic.main.home_fragment.*

class HomeFragment() : Fragment(), View.OnClickListener {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var viewModel: HomeViewModel
    private lateinit var mViewModelFactory: ViewModelProvider.AndroidViewModelFactory
    private lateinit var root: View
    private lateinit var tvLblMonth: TextView
    private lateinit var tvIncomes: TextView
    private lateinit var tvExpenses: TextView
    private lateinit var mMainListener: MainListener

    private val mIncomesAdapter: IncomesRecyclerViewAdapter = IncomesRecyclerViewAdapter()
    private val mExpensesAdapter: ExpensesRecyclerViewAdapter = ExpensesRecyclerViewAdapter()

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
            tvIncomes.text = it.toString()
        })

        viewModel.expenses.observe(viewLifecycleOwner, Observer {
            tvExpenses.text = it.toString()
        })

        viewModel.recyclerViewIncomes.observe(viewLifecycleOwner, Observer {
            mIncomesAdapter.updateIncomesList(it)
        })

        viewModel.recyclerViewExpenses.observe(viewLifecycleOwner, Observer {
            mExpensesAdapter.updateExpensesList(it)
        })

        viewModel.balance.observe(viewLifecycleOwner, Observer {
            txt_account_balance.text = it.toString()
        })

        viewModel.year.observe(viewLifecycleOwner, Observer {
            Log.d("teste", "year: $it")
            if(::mMainListener.isInitialized) {

                mMainListener.onSetYear(it.toString())
            }
        })
    }

    private fun adapters() {
        setInfoIncomesRecyclerView()
        setInfoExpensesRecyclerView()
    }

    private fun setInfoIncomesRecyclerView() {
        val incomesRV = root.findViewById<RecyclerView>(R.id.rv_incomes)
        incomesRV.layoutManager = LinearLayoutManager(context)
        incomesRV.adapter = mIncomesAdapter
    }

    private fun setInfoExpensesRecyclerView() {
        val expensesRV = root.findViewById<RecyclerView>(R.id.rv_expenses)
        expensesRV.layoutManager = LinearLayoutManager(context)
        expensesRV.adapter = mExpensesAdapter
    }

    fun attachListener(mainListener: MainListener) {
        mMainListener = mainListener
    }

}