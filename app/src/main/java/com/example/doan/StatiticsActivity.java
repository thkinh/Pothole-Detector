package com.example.doan;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import java.util.ArrayList;

public class StatiticsActivity extends AppCompatActivity {

    private PieChart pieChart;

    private ListAdapterRoute listAdapterRoute;
    private ArrayList<ListDataRoute> routeArrayList = new ArrayList<>();
    private ListDataRoute listDataRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.at_statitics);

        // Set up the graph
        GraphView graph = findViewById(R.id.graph);
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(getDataPoint());
        graph.addSeries(series);
        series.setSpacing(10);

        // Set up the chart
        pieChart = findViewById(R.id.piechart);
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

        // Set up the route list
        int[] routeImageList = {R.drawable.img_pothole, R.drawable.img_pothole2, R.drawable.img_pothole};
        String[] routeNameList = {"Đặng Văn Cẩn", "Võ Văn Ngân", "Nguyễn Thị Thập"};
        String[] quantityList = {"6 potholes", "9 potholes", "3 potholes"};
        for (int i = 0; i < routeImageList.length; i++) {
            listDataRoute = new ListDataRoute(routeNameList[i], quantityList[i], routeImageList[i]);
            routeArrayList.add(listDataRoute);
        }
        listAdapterRoute = new ListAdapterRoute(this, routeArrayList);
        ListView listViewRoute = findViewById(R.id.listroute);
        listViewRoute.setAdapter(listAdapterRoute);
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