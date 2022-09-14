package com.example.runningmate2.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.runningmate2.MyApplication
import com.example.runningmate2.R
import com.example.runningmate2.databinding.FragmentRecordGraphBinding
import com.example.runningmate2.viewModel.MainViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import java.lang.Float.max


class RecordGraphFragment : Fragment() {
    private val mainViewModel: MainViewModel by activityViewModels()
    val entries = ArrayList<BarEntry>()
    private val binding: FragmentRecordGraphBinding by lazy {
        FragmentRecordGraphBinding.inflate(layoutInflater)
    }
    var count = 1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e("TAG", "그래프 뷰 진입")
        mainViewModel.runningData.observe(viewLifecycleOwner){datas->
            if(datas.size>0){
                var monday = 0F
                var tuesday = 0F
                var wednesday = 0F
                var thursday = 0F
                var friday = 0F
                var saturday = 0F
                var sunday = 0F
                try {
                    for(data in datas){
                        when(data.dayOfWeek) {
                            "월" -> monday += data.distance.split(" ")[0].toFloat()
                            "화" -> tuesday += data.distance.split(" ")[0].toFloat()
                            "수" -> wednesday += data.distance.split(" ")[0].toFloat()
                            "목" -> thursday += data.distance.split(" ")[0].toFloat()
                            "금" -> friday += data.distance.split(" ")[0].toFloat()
                            "토" -> saturday += data.distance.split(" ")[0].toFloat()
                            "일" -> sunday += data.distance.split(" ")[0].toFloat()
                            else -> Log.e("TAG", "해당 요일이 없는데요?")
                        }
                        count++
                    }
                    entries.add(BarEntry(1F,monday))
                    entries.add(BarEntry(2F,tuesday))

                    entries.add(BarEntry(3F,wednesday))
//                    entries.add(BarEntry(3F,0.2F))

                    entries.add(BarEntry(4F,thursday))
//                    entries.add(BarEntry(4F,0.3F))

                    entries.add(BarEntry(5F,friday))
//                    entries.add(BarEntry(5F,0.1F))
                    entries.add(BarEntry(6F,saturday))
                    entries.add(BarEntry(7F,sunday))

                    val myChart = binding.chart
                    myChart.run {
                        description.isEnabled = false // 차트 옆에 별도로 표기되는 description을 안보이게 설정 (false)
                        setMaxVisibleValueCount(7) // 최대 보이는 그래프 개수를 7개로 지정
                        setPinchZoom(false) // 핀치줌(두손가락으로 줌인 줌 아웃하는것) 설정
                        setDrawBarShadow(false) //그래프의 그림자
                        setDrawGridBackground(false)//격자구조 넣을건지
                        axisLeft.run { //왼쪽 축. 즉 Y방향 축을 뜻한다.
                            axisMaximum = maxOf(monday,tuesday,wednesday,thursday,friday,saturday,sunday)+1 //100 위치에 선을 그리기 위해 101f로 맥시멈값 설정
                            axisMinimum = 0f // 최소값 0
                            granularity = 1f //  단위마다 선을 그리려고 설정.
                            setDrawLabels(true) // 값 적는거 허용 (0, 50, 100)
                            setDrawGridLines(true) //격자 라인 활용
                            setDrawAxisLine(false) // 축 그리기 설정
                            textSize = 13f //라벨 텍스트 크기
                        }
                        xAxis.run {
                            position = XAxis.XAxisPosition.BOTTOM // X축을 아래에다가 둔다.
                            granularity = 1f // 1 단위만큼 간격 두기
                            setDrawAxisLine(true) // 축 그림
                            setDrawGridLines(false) // 격자
                            textSize = 11f // 텍스트 크기
                            valueFormatter = MyXAxisFormatter() // X축 라벨값(밑에 표시되는 글자) 바꿔주기 위해 설정
                        }
                        axisRight.isEnabled = false // 오른쪽 Y축을 안보이게 해줌.
                        setTouchEnabled(false) // 그래프 터치해도 아무 변화없게 막음
                        animateY(1000) // 밑에서부터 올라오는 애니매이션 적용
                        legend.isEnabled = false //차트 범례 설정
                    }

                    val set = BarDataSet(entries,"DataSet") // 데이터셋 초기화
                    set.color = ContextCompat.getColor(MyApplication.getApplication(), R.color.orange) // 바 그래프 색 설정

                    val dataSet :ArrayList<IBarDataSet> = ArrayList()
                    dataSet.add(set)
                    val data = BarData(dataSet)
                    data.barWidth = 0.7f //막대 너비 설정
                    myChart.run {
                        this.data = data //차트의 데이터를 data로 설정해줌.
                        setFitBars(true)
                        invalidate()
                    }
                }catch (e:Exception){
                    Log.e("TAG", "Err")
                }
            }
        }
//        entries.add(BarEntry(6.0f,30.0f))
//        entries.add(BarEntry(7.0f,90.0f))
        return binding.root
    }


    inner class MyXAxisFormatter : ValueFormatter() {
        private val days = arrayOf("월","화","수","목","금","토","일")
//        private val days = dateList
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return days.getOrNull(value.toInt()-1) ?: value.toString()
        }
    }


}