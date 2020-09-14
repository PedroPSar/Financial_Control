package com.development.pega.financialcontrol.views

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.development.pega.financialcontrol.viewmodels.ChartViewModel
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.viewmodels.HomeViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

class ChartFragment : Fragment() {

    companion object {
        fun newInstance() = ChartFragment()
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

        val lineDataSet1 = LineDataSet(createArray(), "Months")
        var dataSets = arrayListOf<ILineDataSet>()
        dataSets.add(lineDataSet1)

        val lineData = LineData(dataSets)
        mYearMonthsLineChart.data = lineData
        mYearMonthsLineChart.invalidate()
    }

    private fun createArray(): List<Entry> {
        var dataVals = arrayListOf<Entry>()
        dataVals.add(Entry(1f, 5f))
        dataVals.add(Entry(2f, 2f))
        dataVals.add(Entry(3f, 11f))
        dataVals.add(Entry(4f, 16f))
        dataVals.add(Entry(5f, 7f))
        dataVals.add(Entry(6f, 3f))
        dataVals.add(Entry(7f, 8f))

        return dataVals
    }

}