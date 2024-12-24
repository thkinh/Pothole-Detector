package com.example.doan.dashboard;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.doan.R;
import com.example.doan.api.potholes.PotholeManager;
import com.example.doan.model.Pothole;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentStatistic extends Fragment {

    private PieChart pieChart;

    private ListAdapterRoute listAdapterRoute;
    private ArrayList<ListDataRoute> routeArrayList = new ArrayList<>();
    private ListDataRoute listDataRoute;

    public FragmentStatistic() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);

        // Set up the graph and chart
        BarChart graph = view.findViewById(R.id.graph);
        PieChart pieChart = view.findViewById(R.id.piechart);
        fetchPotholeData(graph, pieChart);

        return view;
    }

    private void fetchPotholeData(BarChart graph, PieChart pieChart) {
        PotholeManager.getInstance().getALLPotholes(new PotholeManager.GetPotholeCallBack() {
            @Override
            public void onSuccess(List<Pothole> potholes) {
                updateGraphView(graph, potholes);
                updatePieChart(pieChart, potholes);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), "Failed to fetch pothole data: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateGraphView(BarChart graph, List<Pothole> potholes) {
        Map<String, Integer> potholeCountByDate = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Tính toán ngày bắt đầu và ngày kết thúc
        Calendar calendar = Calendar.getInstance();
        Date endDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        Date startDate = calendar.getTime();

        for (Pothole pothole : potholes) {
            Date dateFound = pothole.getDateFound();
            if (dateFound != null && !dateFound.before(startDate) && !dateFound.after(endDate)) {
                String dateString = dateFormat.format(dateFound);
                potholeCountByDate.put(dateString, potholeCountByDate.getOrDefault(dateString, 0) + 1);
            }
        }

        List<BarEntry> entries = new ArrayList<>();
        List<String> dateLabels = new ArrayList<>();
        int index = 0;
        for (Map.Entry<String, Integer> entry : potholeCountByDate.entrySet()) {
            entries.add(new BarEntry(index++, entry.getValue()));
            dateLabels.add(entry.getKey());
        }

        BarDataSet dataSet = new BarDataSet(entries, "Number of Potholes");
        BarData barData = new BarData(dataSet);
        graph.setData(barData);

        XAxis xAxis = graph.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dateLabels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        YAxis leftAxis = graph.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setGranularity(1f); // Set granularity to 1
        leftAxis.setGranularityEnabled(true); // Enable granularity

        YAxis rightAxis = graph.getAxisRight();
        rightAxis.setEnabled(false);

        graph.getDescription().setEnabled(false);
        graph.invalidate(); // refresh
    }

    private void updatePieChart(PieChart pieChart, List<Pothole> potholes) {
        Map<String, Integer> severityCount = new HashMap<>();
        for (Pothole pothole : potholes) {
            String severity = pothole.getSeverity();
            if (severity != null) {
                severityCount.put(severity, severityCount.getOrDefault(severity, 0) + 1);
            }
        }

        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : severityCount.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Pothole Severity");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data = new PieData(dataSet);
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);
        pieChart.setData(data);
        pieChart.invalidate(); // refresh
    }
}