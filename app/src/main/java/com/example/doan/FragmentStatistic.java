package com.example.doan;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentStatistic#newInstance} factory method to
 * create an instance of this fragment.
 */
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

        // Set up the graph
        GraphView graph = view.findViewById(R.id.graph);
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(getDataPoint());
        graph.addSeries(series);
        series.setSpacing(10);

        // Set up the chart
        pieChart = view.findViewById(R.id.piechart);
        ArrayList<PieEntry> yValues = new ArrayList<>();
        yValues.add(new PieEntry(34f));
        yValues.add(new PieEntry(23f));
        yValues.add(new PieEntry(14f));
        PieDataSet dataSet = new PieDataSet(yValues, null);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data = new PieData(dataSet);
        data.setValueTextSize(2f);
        data.setValueTextColor(R.color.black);
        pieChart.setData(data);

        return view;
    }

    private DataPoint[] getDataPoint() {
        return new DataPoint[]{
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        };
    }
}