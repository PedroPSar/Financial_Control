package com.development.pega.financialcontrol.views

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.marginBottom
import androidx.core.view.marginRight
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.adapter.ExpensesRecyclerViewAdapter
import com.development.pega.financialcontrol.adapter.IncomesRecyclerViewAdapter
import com.development.pega.financialcontrol.control.AppControl
import com.development.pega.financialcontrol.databinding.HomeFragmentBinding
import com.development.pega.financialcontrol.listener.ExpenseItemListener
import com.development.pega.financialcontrol.listener.IncomeItemListener
import com.development.pega.financialcontrol.listener.MainListener
import com.development.pega.financialcontrol.model.Expense
import com.development.pega.financialcontrol.model.Income
import com.development.pega.financialcontrol.service.Constants
import com.development.pega.financialcontrol.service.repository.Prefs
import com.development.pega.financialcontrol.viewmodels.HomeViewModel
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.math.roundToInt


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

    private lateinit var mPrefs: Prefs

    private var _binding: HomeFragmentBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        private lateinit var mMainListener: MainListener

        fun newInstance(mainListener: MainListener): HomeFragment{
            mMainListener = mainListener
            return HomeFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        root = binding.root
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mViewModelFactory = ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        mViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        tvLblMonth = root.findViewById(R.id.lbl_month_name)
        tvIncomes = root.findViewById(R.id.txt_incomes)
        tvExpenses = root.findViewById(R.id.txt_expenses)
        mViewModel.setCurrentDate()
        mPrefs = AppControl.getAppPrefs(requireContext())

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

        mExpensesItemListener = object : ExpenseItemListener {
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

            override fun onPay(expense: Expense) {
                mViewModel.payOrCancelExpensePayment(expense)
                updateHomeInfo()
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
        setColors()
        mViewModel.setIncomesOfMonth()
        mViewModel.setExpensesOfMonth()
        mViewModel.setIncomesInRecyclerView()
        mViewModel.setExpensesInRecyclerView()
        mViewModel.calcBalance()
        mViewModel.setDataInPayHorizontalChart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.btn_img_before -> mViewModel.btnBeforeClick()

            R.id.btn_img_next -> mViewModel.btnNextClick()

            R.id.ll_lbl_months -> showMonthsDialog()
        }
    }

    private fun setOnClick() {
        binding.btnImgBefore.setOnClickListener(this)
        binding.btnImgNext.setOnClickListener(this)
        binding.llLblMonths.setOnClickListener(this)
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
            binding.txtAccountBalance.text = balanceTxt
        })

        mViewModel.year.observe(viewLifecycleOwner, Observer {
            mMainListener.onSetYear(it.toString())
        })

        mViewModel.payHorizontalChart.observe(viewLifecycleOwner, Observer {
            binding.payInfoHorizontalChart.data = it
            val description = Description()
            description.text = ""
            binding.payInfoHorizontalChart.description = description
            binding.payInfoHorizontalChart.legend.isEnabled = false
            binding.payInfoHorizontalChart.setPinchZoom(false)
            binding.payInfoHorizontalChart.setDrawValueAboveBar(false)

            val xAxis = binding.payInfoHorizontalChart.xAxis
            xAxis.setDrawGridLines(false)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.isEnabled = true
            xAxis.setDrawAxisLine(false)
            xAxis.labelCount = 3
            xAxis.valueFormatter = PayInfoFormatter(requireContext())
            xAxis.textSize = 12f
            xAxis.textColor = Color.GRAY

            val yLeft = binding.payInfoHorizontalChart.axisLeft
            yLeft.isEnabled = false
            yLeft.axisMinimum = 0f

            val yRight = binding.payInfoHorizontalChart.axisRight
            yRight.isEnabled = false
            yRight.setDrawAxisLine(false)
            yRight.setDrawGridLines(false)

            binding.payInfoHorizontalChart.animateY(1000)
            binding.payInfoHorizontalChart.setFitBars(true)
            binding.payInfoHorizontalChart.invalidate()
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

        binding.clIncomes.maxWidth = maxWidth
        binding.clExpenses.maxWidth = maxWidth
        binding.clAccountBalance.maxWidth = maxWidth

    }

    private fun setColors() {
        binding.arrowIncomes.setColorFilter(mPrefs.incomesColor)
        binding.txtIncomes.setTextColor(mPrefs.incomesColor)
        binding.labelIncome.setTextColor(mPrefs.incomesColor)

        binding.arrowExpenses.setColorFilter(mPrefs.expensesColor)
        binding.txtExpenses.setTextColor(mPrefs.expensesColor)
        binding.labelExpense.setTextColor(mPrefs.expensesColor)
    }

    fun updateHomeInfo() {
        mViewModel.updateInfo()
    }

    private fun showMonthsDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.months_dialog_title))

        val months = resources.getStringArray(R.array.months_array)
        builder.setItems(months) { _, which ->
            AppControl.setSelectedMonthStartingZero(which)
            updateHomeInfo()
        }

        val dialog = builder.create()
        dialog.show()
    }

    class PayInfoFormatter(context: Context) : ValueFormatter() {
        private val xAxisLabels = arrayListOf(context.getString(R.string.paid), context.getString(R.string.unpaid), context.getString(R.string.overdue))

        override fun getFormattedValue(value: Float): String {
            return AppControl.Text.convertFloatToCurrencyText(value)
        }

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return xAxisLabels?.getOrNull(value.toInt()) ?: value.toString()
        }

    }

}