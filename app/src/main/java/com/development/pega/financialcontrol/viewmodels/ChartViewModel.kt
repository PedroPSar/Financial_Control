package com.development.pega.financialcontrol.viewmodels

import android.app.Application
import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.development.pega.financialcontrol.R
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

class ChartViewModel(application: Application) : AndroidViewModel(application) {

    private val mContext = application.applicationContext
    private val months = mContext.resources.getStringArray(R.array.months_array)

    private val mYearMonthsChart = MutableLiveData<LineData>()
    var yearMonthsChart: LiveData<LineData> = mYearMonthsChart

    fun setDataInYearMonthsLineChart() {

        val balanceLine = LineDataSet(createYearMonthsArray(), mContext.getString(R.string.lbl_balance))
        balanceLine.axisDependency = YAxis.AxisDependency.LEFT

        var dataSets = arrayListOf<ILineDataSet>()
        dataSets.add(balanceLine)

        val lineData = LineData(dataSets)

        mYearMonthsChart.value = lineData
    }

    private fun createYearMonthsArray(): List<Entry> {
        var data = arrayListOf<Entry>()
        data.add(Entry(0f, 700f))
        data.add(Entry(1f, 1300f))
        data.add(Entry(2f, 1500f))
        data.add(Entry(3f, 1600f))
        data.add(Entry(4f, 700f))
        data.add(Entry(5f, 300f))
        data.add(Entry(6f, 900f))
        data.add(Entry(7f, 800f))
        data.add(Entry(8f, 900f))
        data.add(Entry(9f, 900f))
        data.add(Entry(10f, 1500f))
        data.add(Entry(11f, 995f))


        return data
    }
}
