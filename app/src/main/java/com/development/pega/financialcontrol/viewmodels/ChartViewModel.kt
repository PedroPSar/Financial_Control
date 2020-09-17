package com.development.pega.financialcontrol.viewmodels

import android.app.Application
import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.development.pega.financialcontrol.R
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
        var dataSets = arrayListOf<ILineDataSet>()
        dataSets.add(balanceLine)

        val lineData = LineData(dataSets)
        mYearMonthsChart.value = lineData
    }

    private fun createYearMonthsArray(): List<Entry> {
        var data = arrayListOf<Entry>()
        data.add(Entry(1f, 5f))
        data.add(Entry(2f, 2f))
        data.add(Entry(3f, 11f))
        data.add(Entry(4f, 16f))
        data.add(Entry(5f, 7f))
        data.add(Entry(6f, 3f))
        data.add(Entry(7f, 8f))
        data.add(Entry(8f, 9f))
        data.add(Entry(9f, 9f))
        data.add(Entry(10f, 9f))
        data.add(Entry(11f, 9f))
        data.add(Entry(12f, 9f))

        return data
    }
}
