package com.running.runningmate2.utils

import android.util.Log
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.running.domain.model.RunningData
import com.running.runningmate2.R

/**
 * 2023-12-05
 * pureum
 */
object GraphHelper {
    private val entries = ArrayList<BarEntry>()
    private var count = 1
    operator fun invoke(datas: List<RunningData>, barChart: BarChart){
        var monday = 0F
        var tuesday = 0F
        var wednesday = 0F
        var thursday = 0F
        var friday = 0F
        var saturday = 0F
        var sunday = 0F
        try {
            for (data in datas) {
                when (data.dayOfWeek) {
                    "월" -> monday += data.distance.split(" ")[0].toFloat()
                    "화" -> tuesday += data.distance.split(" ")[0].toFloat()
                    "수" -> wednesday += data.distance.split(" ")[0].toFloat()
                    "목" -> thursday += data.distance.split(" ")[0].toFloat()
                    "금" -> friday += data.distance.split(" ")[0].toFloat()
                    "토" -> saturday += data.distance.split(" ")[0].toFloat()
                    "일" -> sunday += data.distance.split(" ")[0].toFloat()
                    else -> {}
                }
                count++
            }
            entries.add(BarEntry(1F, monday))
            entries.add(BarEntry(2F, tuesday))
            entries.add(BarEntry(3F, wednesday))
            entries.add(BarEntry(4F, thursday))
            entries.add(BarEntry(5F, friday))
            entries.add(BarEntry(6F, saturday))
            entries.add(BarEntry(7F, sunday))

            barChart.run {
                description.isEnabled = false
                setMaxVisibleValueCount(7)
                setPinchZoom(false)
                setDrawBarShadow(false)
                setDrawGridBackground(false)
                axisLeft.run {
                    axisMaximum = maxOf(
                        monday,
                        tuesday,
                        wednesday,
                        thursday,
                        friday,
                        saturday,
                        sunday
                    ) + 1
                    axisMinimum = 0f
                    granularity = 1f
                    setDrawLabels(true)
                    setDrawGridLines(true)
                    setDrawAxisLine(false)
                    textSize = 13f
                }
                xAxis.run {
                    position = XAxis.XAxisPosition.BOTTOM
                    granularity = 1f
                    setDrawAxisLine(true)
                    setDrawGridLines(false)
                    textSize = 11f
                    valueFormatter = MyXAxisFormatter()
                }
                axisRight.isEnabled = false
                setTouchEnabled(false)
                animateY(1000)
                legend.isEnabled = false
            }

            val set = BarDataSet(entries, "DataSet")
            set.color =
                ContextCompat.getColor(MyApplication.getApplication(), R.color.orange)
            val dataSet: ArrayList<IBarDataSet> = ArrayList()
            dataSet.add(set)
            val data = BarData(dataSet)
            data.barWidth = 0.7f
            barChart.run {
                this.data = data
                setFitBars(true)
                invalidate()
            }
        } catch (e: Exception) {
            Log.e("TAG", "Err")
        }
    }
    class MyXAxisFormatter : ValueFormatter() {
        private val days = arrayOf("월","화","수","목","금","토","일")
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return days.getOrNull(value.toInt()-1) ?: value.toString()
        }
    }
}