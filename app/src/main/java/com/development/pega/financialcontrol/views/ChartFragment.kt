package com.development.pega.financialcontrol.views

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.development.pega.financialcontrol.viewmodels.ChartViewModel
import com.development.pega.financialcontrol.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.properties.Delegates

class ChartFragment : Fragment() {

    companion object {
        fun newInstance() = ChartFragment()
    }

    private lateinit var viewModel: ChartViewModel
    private lateinit var mViewModelFactory: ViewModelProvider.AndroidViewModelFactory
    private lateinit var root: View
    private lateinit var selectedMonthTextView: TextView
    private lateinit var mYearMonthsLineChart: LineChart
    private lateinit var mMonthExpensesTypePieChart: PieChart
    private lateinit var mMonthExpensesRecurrenceChart: PieChart
    private lateinit var mMonthIncomesRecurrenceChart: PieChart

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.chart_fragment, container, false)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val application = requireActivity().application
        mViewModelFactory = ViewModelProvider.AndroidViewModelFactory(application)
        viewModel = ViewModelProvider(this).get(ChartViewModel::class.java)

        selectedMonthTextView = root.findViewById(R.id.lbl_selected_month)
        mYearMonthsLineChart = root.findViewById(R.id.line_chart_year_months)
        mMonthExpensesTypePieChart = root.findViewById(R.id.pie_chart_month_expenses_type)
        mMonthExpensesRecurrenceChart = root.findViewById(R.id.pie_chart_month_expenses_recurrence)
        mMonthIncomesRecurrenceChart = root.findViewById(R.id.pie_chart_month_incomes_recurrence)

        observers()
    }

    override fun onResume() {
        super.onResume()

        viewModel.setSelectedMonthTextView()
        viewModel.setDataInYearMonthsLineChart()
        viewModel.setExpensesTypePieChartData()
        viewModel.setExpensesRecurrencePieChartData()
        viewModel.setIncomesRecurrencePieChartData()
    }

    private fun observers() {
        viewModel.selectedMonthTextView.observe(viewLifecycleOwner, Observer {
            selectedMonthTextView.text = it
        })

        viewModel.yearMonthsChart.observe(viewLifecycleOwner, Observer {
            mYearMonthsLineChart.data = it
            setStyleInYearMonthsLineChart()
            mYearMonthsLineChart.invalidate()
        })

        viewModel.expensesTypePieChart.observe(viewLifecycleOwner, Observer {
            mMonthExpensesTypePieChart.data = it
            setStyleInMonthExpensesTypePieChart()
            mMonthExpensesTypePieChart.invalidate()
        })

        viewModel.expensesRecurrencePieChart.observe(viewLifecycleOwner, Observer {
            mMonthExpensesRecurrenceChart.data = it
            setStyleInMonthExpensesRecurrencePieChart()
            mMonthExpensesRecurrenceChart.invalidate()
        })

        viewModel.incomesRecurrencePieChart.observe(viewLifecycleOwner, Observer {
            mMonthIncomesRecurrenceChart.data = it
            setStyleInMonthIncomesRecurrencePieChart()
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
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 10f

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            xAxis.textColor = requireContext().getColor(R.color.colorPrimary)
        }else {
            xAxis.textColor = requireContext().resources.getColor(R.color.colorPrimary)
        }

        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(false)
        mYearMonthsLineChart.measure(0, 0)
        xAxis.valueFormatter = MonthsNamesFormatter(context)

        mYearMonthsLineChart.zoom(1.5f, 1f, 1f, 1f)
    }

    private fun setStyleInMonthExpensesTypePieChart() {
        mMonthExpensesTypePieChart.setEntryLabelColor(Color.BLACK)
    }

    private fun setStyleInMonthExpensesRecurrencePieChart() {
        mMonthExpensesRecurrenceChart.setEntryLabelColor(Color.BLACK)
    }

    private fun setStyleInMonthIncomesRecurrencePieChart() {
        mMonthIncomesRecurrenceChart.setEntryLabelColor(Color.BLACK)
    }

    class MonthsNamesFormatter(context: Context?) : ValueFormatter() {
        private val days = context!!.resources.getStringArray(R.array.month_abbreviations)

        override fun getFormattedValue(value: Float): String {
            return value.toString()
        }

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return days.getOrNull(value.toInt()) ?: value.toString()
        }
    }

}