package com.development.pega.financialcontrol.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.development.pega.financialcontrol.viewmodels.ChartViewModel
import com.development.pega.financialcontrol.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.properties.Delegates

class ChartFragment : Fragment() {

    companion object {

        private var mScreenWidth by Delegates.notNull<Float>()

        fun newInstance(screenWidth: Float): ChartFragment {
            mScreenWidth = screenWidth
            return ChartFragment()
        }
    }

    private lateinit var viewModel: ChartViewModel
    private lateinit var mViewModelFactory: ViewModelProvider.AndroidViewModelFactory
    private lateinit var root: View
    private lateinit var mYearMonthsLineChart: LineChart

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.chart_fragment, container, false)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val application = requireActivity().application
        mViewModelFactory = ViewModelProvider.AndroidViewModelFactory(application)
        viewModel = ViewModelProvider(this).get(ChartViewModel::class.java)

        mYearMonthsLineChart = root.findViewById(R.id.line_chart_year_months)

        observers()
    }

    override fun onResume() {
        super.onResume()

        viewModel.setDataInYearMonthsLineChart()
    }

    private fun observers() {
        viewModel.yearMonthsChart.observe(viewLifecycleOwner, Observer {
            mYearMonthsLineChart.data = it

            val description = mYearMonthsLineChart.description
            description.isEnabled = false

            val yAxisRight = mYearMonthsLineChart.axisRight
            yAxisRight.isEnabled = false
            yAxisRight.setDrawGridLines(false)

            val yAxisLeft = mYearMonthsLineChart.axisLeft
            yAxisLeft.setDrawZeroLine(true)
            yAxisLeft.zeroLineWidth = 8f
            yAxisLeft.zeroLineColor = R.color.colorPrimary
            yAxisLeft.setDrawGridLines(false)

            val xAxis = mYearMonthsLineChart.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM;
            xAxis.textSize = 10f
            xAxis.textColor = R.color.colorPrimary;
            xAxis.setDrawAxisLine(false)
            xAxis.setDrawGridLines(false)
            mYearMonthsLineChart.measure(0, 0);
            xAxis.valueFormatter = MonthsNamesFormatter()

            mYearMonthsLineChart.zoom(1.5f, 1f, 1f, 1f)
            mYearMonthsLineChart.invalidate()
        })
    }


    class MonthsNamesFormatter() : ValueFormatter() {
        private val days = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec")

        override fun getFormattedValue(value: Float): String {
            return value.toString()
        }

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return days.getOrNull(value.toInt()) ?: value.toString()
        }
    }

}