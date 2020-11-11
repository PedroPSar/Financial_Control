package com.development.pega.financialcontrol.views

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.fragment.app.Fragment
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
import com.development.pega.financialcontrol.listener.ExpenseItemListener
import com.development.pega.financialcontrol.listener.IncomeItemListener
import com.development.pega.financialcontrol.listener.MainListener
import com.development.pega.financialcontrol.model.Expense
import com.development.pega.financialcontrol.model.Income
import com.development.pega.financialcontrol.service.Constants
import com.development.pega.financialcontrol.viewmodels.HomeViewModel
import kotlinx.android.synthetic.main.home_fragment.*
import kotlin.math.roundToInt
import kotlin.reflect.KClass

class HomeFragment() : Fragment(), View.OnClickListener{

    private lateinit var mViewModel: HomeViewModel
    private lateinit var mViewModelFactory: ViewModelProvider.AndroidViewModelFactory
    private lateinit var root: View
    private lateinit var tvLblMonth: TextView
    private lateinit var tvIncomes: TextView
    private lateinit var tvExpenses: TextView
    private lateinit var incomesRV: RecyclerView
    private lateinit var expensesRV: RecyclerView

    private val mIncomesAdapter: IncomesRecyclerViewAdapter = IncomesRecyclerViewAdapter()
    private val mExpensesAdapter: ExpensesRecyclerViewAdapter = ExpensesRecyclerViewAdapter()

    private lateinit var mIncomesItemListener: IncomeItemListener
    private lateinit var mExpensesItemListener: ExpenseItemListener

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
        mViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        tvLblMonth = root.findViewById(R.id.lbl_month_name)
        tvIncomes = root.findViewById(R.id.txt_incomes)
        tvExpenses = root.findViewById(R.id.txt_expenses)
        mViewModel.setCurrentDate()

        mIncomesItemListener = object : IncomeItemListener {

            override fun onEdit(id: Int) {
                val intent = Intent(requireContext(), AddIncomeActivity::class.java)
                val bundle = Bundle()
                bundle.putInt(Constants.ITEM_ID, id)

                intent.putExtras(bundle)
                startActivity(intent)
            }

            override fun onDelete(income: Income) {
                mViewModel.deleteIncome(income)
                mViewModel.setIncomesInRecyclerView()
            }

        }

        mExpensesItemListener = object: ExpenseItemListener {
            override fun onEdit(id: Int) {
                val intent = Intent(requireContext(), AddExpenseActivity::class.java)
                val bundle = Bundle()
                bundle.putInt(Constants.ITEM_ID, id)

                intent.putExtras(bundle)
                startActivity(intent)
            }

            override fun onDelete(expense: Expense) {
                mViewModel.deleteExpense(expense)
                mViewModel.setExpensesInRecyclerView()
            }
        }

        setMaxWidthInLayout()
        setOnClick()
        setListenerOnAdapters()
        adapters()
        observer()
    }

    override fun onResume() {
        super.onResume()
        mViewModel.setIncomesOfMonth()
        mViewModel.setExpensesOfMonth()
        mViewModel.setIncomesInRecyclerView()
        mViewModel.setExpensesInRecyclerView()
        mViewModel.calcBalance()
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.btn_img_before -> {
                mViewModel.btnBeforeClick()
            }

            R.id.btn_img_next -> {
                mViewModel.btnNextClick()
            }
        }
    }

    private fun setOnClick() {
        btn_img_before.setOnClickListener(this)
        btn_img_next.setOnClickListener(this)
    }

    private fun observer() {
        mViewModel.month.observe(viewLifecycleOwner, Observer {
            tvLblMonth.text = it
        })

        mViewModel.incomes.observe(viewLifecycleOwner, Observer {
            val incomesTxt = AppControl.Text.convertFloatToCurrencyText(it)
            tvIncomes.text = incomesTxt
        })

        mViewModel.expenses.observe(viewLifecycleOwner, Observer {
            val expensesTxt = AppControl.Text.convertFloatToCurrencyText(it)
            tvExpenses.text = expensesTxt
        })

        mViewModel.recyclerViewIncomes.observe(viewLifecycleOwner, Observer {
            mIncomesAdapter.updateIncomesList(it)
        })

        mViewModel.recyclerViewExpenses.observe(viewLifecycleOwner, Observer {
            mExpensesAdapter.updateExpensesList(it)
        })

        mViewModel.balance.observe(viewLifecycleOwner, Observer {
            val balanceTxt = AppControl.Text.convertFloatToCurrencyText(it)
            txt_account_balance.text = balanceTxt
        })

        mViewModel.year.observe(viewLifecycleOwner, Observer {
            mMainListener.onSetYear(it.toString())
        })
    }

    private fun adapters() {
        setInfoIncomesRecyclerView()
        setInfoExpensesRecyclerView()
    }

    private fun setInfoIncomesRecyclerView() {
        incomesRV = root.findViewById(R.id.rv_incomes)
        incomesRV.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        incomesRV.layoutManager = LinearLayoutManager(context)
        incomesRV.adapter = mIncomesAdapter
    }

    private fun setInfoExpensesRecyclerView() {
        expensesRV = root.findViewById(R.id.rv_expenses)
        expensesRV.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        expensesRV.layoutManager = LinearLayoutManager(context)
        expensesRV.adapter = mExpensesAdapter
    }

    private fun setListenerOnAdapters() {
        mIncomesAdapter.attachListener(mIncomesItemListener)
        mExpensesAdapter.attachListener(mExpensesItemListener)
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