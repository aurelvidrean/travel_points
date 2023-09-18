package com.example.travelpoints.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.travelpoints.databinding.FragmentChartsBinding
import com.example.travelpoints.ui.viewmodels.ChartsViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.utils.ColorTemplate

class ChartsFragment : Fragment() {

    private lateinit var binding: FragmentChartsBinding
    private lateinit var lineChart: LineChart
    private lateinit var barChart: BarChart
    private lateinit var pieChart: PieChart

    private val chartsViewModel by lazy {
        ViewModelProvider(this)[ChartsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChartsBinding.inflate(inflater, container, false)

        lineChart = binding.lineChart
        barChart = binding.barChart
        pieChart = binding.pieChart

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chartsViewModel.getLineChartEntries()
        chartsViewModel.lineChartEntries.observe(viewLifecycleOwner) { lineEntries ->
            if (lineEntries != null) {
                val lineDataSet = LineDataSet(lineEntries, "Ratings")
                val colorList: MutableList<Int> = mutableListOf()
                for (materialColor in ColorTemplate.MATERIAL_COLORS) {
                    colorList.add(materialColor)
                }
                lineDataSet.colors = colorList
                lineDataSet.valueTextColor = Color.BLACK
                lineDataSet.valueTextSize = 16f

                lineChart.data = LineData(lineDataSet)
                lineChart.animate()
                lineChart.description = Description().apply {
                    text = "Chart based on sites rating"
                }
                lineChart.invalidate()
            } else {
                lineChart.setNoDataText("Problem");
            }
        }

        chartsViewModel.getPieChartEntries()
        chartsViewModel.pieChartEntries.observe(viewLifecycleOwner) { pieEntries ->
            if (pieEntries != null) {
                val pieDataSet = PieDataSet(pieEntries, "Categories")
                val colorList: MutableList<Int> = mutableListOf()
                for (joyfulColor in ColorTemplate.COLORFUL_COLORS) {
                    colorList.add(joyfulColor)
                }
                pieDataSet.colors = colorList
                pieDataSet.valueTextColor = Color.BLACK
                pieDataSet.valueTextSize = 16f

                pieChart.data = PieData(pieDataSet)
                pieChart.animate()
                pieChart.description = Description().apply {
                    text = "Chart based on number of sites of each category"
                }
                pieChart.invalidate()
            }
        }

        chartsViewModel.getBarChartEntries()
        chartsViewModel.barChartEntries.observe(viewLifecycleOwner) { barEntries ->
            if (barEntries != null) {
                val barDataSet = BarDataSet(barEntries, "Comments")
                val colorList: MutableList<Int> = mutableListOf()
                for (pasterColor in ColorTemplate.PASTEL_COLORS) {
                    colorList.add(pasterColor)
                }
                barDataSet.colors = colorList
                barDataSet.valueTextColor = Color.BLACK
                barDataSet.valueTextSize = 16f

                barChart.data = BarData(barDataSet)
                barChart.animate()
                barChart.description = Description().apply {
                    text = "Chart based on comments for each site"
                }
                barChart.invalidate()
            }
        }
    }
}