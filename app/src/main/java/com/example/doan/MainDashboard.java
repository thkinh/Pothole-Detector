package com.example.doan;

import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
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

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

public class MainDashboard extends AppCompatActivity {

    ListAdapterUser listAdapterUser;
    ArrayList<ListDataUser> userArrayList = new ArrayList<>();
    ListDataUser listDataUser;

    ListAdapterRoute listAdapterRoute;
    ArrayList<ListDataRoute> routeArrayList = new ArrayList<>();
    ListDataRoute listDataRoute;

    PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.at_dashboard);
        EdgeToEdge.enable(this);

        // Set up the graph
        GraphView graph = (GraphView) findViewById(R.id.graph);
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(getDataPoint());

        graph.addSeries(series);
        series.setSpacing(10);

        //Set up the chart
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


        int[] userImageList = {R.drawable.avt_linhxeom, R.drawable.avt_taichodien};
        String[] userNameList = {"Linh xe ôm", "Tài chó điên"};
        String[] dateList = {"05/11/2024", "20/10/2024"};

        for (int i = 0; i < userImageList.length; i++) {
            listDataUser = new ListDataUser(userNameList[i], dateList[i], userImageList[i]);
            userArrayList.add(listDataUser);
        }
        listAdapterUser = new ListAdapterUser(MainDashboard.this, userArrayList);
        ListView listViewUser = findViewById(R.id.listuser);
        listViewUser.setAdapter(listAdapterUser);



        int[] routeImageList = {R.drawable.img_pothole, R.drawable.img_pothole2};
        String[] routeNameList = {"Đặng Văn Cẩn", "Võ Văn Ngân"};
        String[] quantityList = {"6 potholes", "9 potholes"};

        for (int i = 0; i < routeImageList.length; i++) {
            listDataRoute = new ListDataRoute(routeNameList[i], quantityList[i], routeImageList[i]);
            routeArrayList.add(listDataRoute);
        }

        listAdapterRoute = new ListAdapterRoute(MainDashboard.this, routeArrayList);
        ListView listViewRoute = findViewById(R.id.listroute);
        listViewRoute.setAdapter(listAdapterRoute);
    }

    private DataPoint[] getDataPoint() {
        DataPoint[] dp = new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        };
        return dp;
    }
}