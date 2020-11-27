package com.development.pega.financialcontrol.views

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.development.pega.financialcontrol.viewmodels.ChartViewModel
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.control.AppControl
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.ValueFormatter

class ChartFragment : Fragment(), View.OnClickListener {

    companion object {
        fun newInstance() = ChartFragment()
    }

    private lateinit var viewModel: ChartViewModel
    private lateinit var mViewModelFactory: ViewModelProvider.AndroidViewModelFactory
    private lateinit var root: View
    private lateinit var mYearMonthsLineChart: LineChart
    private lateinit var mMonthExpensesTypePieChart: PieChart
    private lateinit var mMonthExpensesRecurrenceChart: PieChart
    private lateinit var mMonthIncomesRecurrenceChart: PieChart
    private lateinit var mTvSelectedMonth: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.chart_fragment, container, false)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val application = requireActivity().application
        mViewModelFactory = ViewModelProvider.AndroidViewModelFactory(application)
        viewModel = ViewModelProvider(this).get(ChartViewModel::class.java)

        mTvSelectedMonth = root.findViewById(R.id.tv_selected_month)
        mYearMonthsLineChart = root.findViewById(R.id.line_chart_year_months)
        mMonthExpensesTypePieChart = root.findViewById(R.id.pie_chart_month_expenses_type)
        mMonthExpensesRecurrenceChart = root.findViewById(R.id.pie_chart_month_expenses_recurrence)
        mMonthIncomesRecurrenceChart = root.findViewById(R.id.pie_chart_month_incomes_recurrence)

        mTvSelectedMonth.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        observers()
        setListeners()
    }

    override fun onResume() {
        super.onResume()
        updateChartInfo()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.tv_selected_month -> showMonthsDialog()
        }
    }

    private fun setListeners() {
        mTvSelectedMonth.setOnClickListener(this)
    }

    private fun observers() {
        viewModel.selectedMonth.observe(viewLifecycleOwner, Observer {
            mTvSelectedMonth.text = resources.getStringArray(R.array.months_array)[it]
        })

        viewModel.yearMonthsChart.observe(viewLifecycleOwner, Observer {
            mYearMonthsLineChart.data = it
            setStyleInYearMonthsLineChart()
            mYearMonthsLineChart.notifyDataSetChanged()
            mYearMonthsLineChart.invalidate()
        })

        viewModel.expensesTypePieChart.observe(viewLifecycleOwner, Observer {
            mMonthExpensesTypePieChart.data = it
            setStyleInMonthExpensesTypePieChart()
            mMonthExpensesTypePieChart.notifyDataSetChanged()
            mMonthExpensesTypePieChart.invalidate()
        })

        viewModel.expensesRecurrencePieChart.observe(viewLifecycleOwner, Observer {
            mMonthExpensesRecurrenceChart.data = it
            setStyleInMonthExpensesRecurrencePieChart()
            mMonthExpensesRecurrenceChart.notifyDataSetChanged()
            mMonthExpensesRecurrenceChart.invalidate()
        })

        viewModel.incomesRecurrencePieChart.observe(viewLifecycleOwner, Observer {
            mMonthIncomesRecurrenceChart.data = it
            setStyleInMonthIncomesRecurrencePieChart()
            mMonthIncomesRecurrenceChart.notifyDataSetChanged()
            mMonthIncomesRecurrenceChart.invalidate()
        })
    }

    private fun setStyleInYearMonthsLineChart() {
        val description = mYearMonthsLineChart.description
        description.isEnabled = false

        val yAxisRight = mYearMonthsLineChart.axisRight
        yAxisRight.isEnabled = false
        yAxisRight.setDrawGridLines(false)

        val yAxisLeft = mYearMonthsLineChart.axisLeft
        yAxisLeft.setDrawZeroLine(true)
        yAxisLeft.setDrawGridLines(false)

        val xAxis = mYearMonthsLineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.TOP
        xAxis.setDrawGridLines(true)
        xAxis.textSize = 10f

        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(false)
        mYearMonthsLineChart.measure(0, 0)
        xAxis.valueFormatter = MonthsNamesFormatter(context)

        mYearMonthsLineChart.zoom(1.5f, 1f, 1f, 1f)
        mYearMonthsLineChart.setPinchZoom(false)
        mYearMonthsLineChart.setDrawBorders(true)
        mYearMonthsLineChart.setDrawGridBackground(true)
        mYearMonthsLineChart.setGridBackgroundColor(Color.WHITE)
    }

    private fun setStyleInMonthExpensesTypePieChart() {
        mMonthExpensesTypePieChart.setEntryLabelColor(Color.BLACK)
        mMonthExpensesTypePieChart.description.isEnabled = false
        mMonthExpensesTypePieChart.setTouchEnabled(false)
        mMonthExpensesTypePieChart.setDrawEntryLabels(false)

    }

    private fun setStyleInMonthExpensesRecurrencePieChart() {
        mMonthExpensesRecurrenceChart.setEntryLabelColor(Color.BLACK)
        mMonthExpensesRecurrenceChart.description.isEnabled = false
        mMonthExpensesRecurrenceChart.setTouchEnabled(false)
        mMonthExpensesRecurrenceChart.setDrawEntryLabels(false)

    }

    private fun setStyleInMonthIncomesRecurrencePieChart() {
        mMonthIncomesRecurrenceChart.setEntryLabelColor(Color.BLACK)
        mMonthIncomesRecurrenceChart.description.isEnabled = false
        mMonthIncomesRecurrenceChart.setTouchEnabled(false)
        mMonthIncomesRecurrenceChart.setDrawEntryLabels(false)

    }

    fun updateChartInfo() {
        viewModel.setSelectedMonth()
        viewModel.setDataInYearMonthsLineChart()
        viewModel.setExpensesTypePieChartData()
        viewModel.setExpensesRecurrencePieChartData()
        viewModel.setIncomesRecurrencePieChartData()

    }

    private fun showMonthsDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.months_dialog_title))

        val months = resources.getStringArray(R.array.months_array)
        builder.setItems(months) { dialog, which ->
            AppControl.setSelectedMonthStartingZero(which)
            updateChartInfo()
        }

        val dialog = builder.create()
        dialog.show()
    }

    class MonthsNamesFormatter(context: Context?) : ValueFormatter() {
        private val days = context?.resources?.getStringArray(R.array.month_abbreviations)

        override fun getFormattedValue(value: Float): String {
            return AppControl.Text.convertFloatToCurrencyText(value)
        }

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return days?.getOrNull(value.toInt()) ?: value.toString()
        }
    }

    class PieChartsValueFormatter(context: Context?) : ValueFormatter() {

        override fun getFormattedValue(value: Float): String {
            return AppControl.Text.convertFloatToCurrencyText(value)
        }
    }

}