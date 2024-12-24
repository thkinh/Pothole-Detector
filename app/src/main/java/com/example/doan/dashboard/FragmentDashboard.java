package com.example.doan.dashboard;

import com.example.doan.NotificationActivity;
import com.example.doan.R;
import com.example.doan.api.auth.AuthManager;
import com.example.doan.api.potholes.PotholeManager;
import com.example.doan.login.LoginActivity;
import com.example.doan.model.AppUser;
import com.example.doan.model.Pothole;
import com.example.doan.setting.ProfileActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.example.doan.feature.UserPreferences;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentDashboard extends Fragment {

    // For Fetching Data
    private PieChart pieChart;
    private ImageButton btNotify;
    private TextView totalDistance;

    // For Firebase Auth and Google Sign In
    private ImageView ivImage;
    private TextView tvName;
    private ImageButton btLogout;
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;
    // For Switching between User and Route in the middle CardView
    private Button btnUser;
    private Button btnRoute;

    public FragmentDashboard() {
        // Required empty public constructor
    }

    Activity context;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        context = getActivity();

        // Assign variables
        ivImage = view.findViewById(R.id.avt);
        tvName = view.findViewById(R.id.txt_name);
        btLogout = view.findViewById(R.id.ic_power);
        btNotify = view.findViewById(R.id.ic_notify);
        btnUser = view.findViewById(R.id.switch_to_user);
        btnRoute = view.findViewById(R.id.switch_to_route);
        btnRoute.setTextColor(getResources().getColor(R.color.gray));
        totalDistance = view.findViewById(R.id.total_distance);

        // Add this block to ensure FragmentUser is displayed first
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_switching, new FragmentUser());
        ft.commit();
        btnUser.setBackgroundColor(getResources().getColor(R.color.darkpacificblue));
        btnUser.setTextColor(getResources().getColor(R.color.white));

//--------------------------Start of statistics--------------------------
        // Set up the graph and chart
        BarChart graph = view.findViewById(R.id.graph);
        PieChart pieChart = view.findViewById(R.id.piechart);
        fetchPotholeData(graph, pieChart);

//---------------------End of statistics---------------------


// ---------------------Start of google sign in---------------------
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Initialize Firebase user
        FirebaseUser mUser = mAuth.getCurrentUser();
        // Check condition
        if (mUser != null) {
            // When Firebase user is not null, set image and name
            Glide.with(this).load(mUser.getPhotoUrl()).into(ivImage);
            tvName.setText(mUser.getDisplayName());
        }

        // Khởi tạo GoogleSignInClient
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Thay bằng ID client trong google-services.json
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        // Gắn sự kiện cho nút Logout
        btLogout = view.findViewById(R.id.ic_power);
        btLogout.setOnClickListener(v -> logout());
//----------------------------End of google sign in---------------------

        ImageButton notifyButton = view.findViewById(R.id.ic_notify);
        notifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NotificationActivity.class);
                startActivity(intent);
            }
        });

        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
            }
        });

//----------------------------Start of user and route---------------------
        btnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_switching, new FragmentUser());
                ft.commit();
                btnUser.setBackgroundColor(getResources().getColor(R.color.darkpacificblue));
                btnUser.setTextColor(getResources().getColor(R.color.white));
                btnRoute.setBackgroundColor(getResources().getColor(R.color.pacificblue));
                btnRoute.setTextColor(getResources().getColor(R.color.gray));
            }
        });

        btnRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_switching, new FragmentRoute());
                ft.commit();
                btnUser.setBackgroundColor(getResources().getColor(R.color.pacificblue));
                btnUser.setTextColor(getResources().getColor(R.color.gray));
                btnRoute.setBackgroundColor(getResources().getColor(R.color.darkpacificblue));
                btnRoute.setTextColor(getResources().getColor(R.color.white));
            }
        });
//----------------------------End of user and route---------------------

//Bấm vô chữ "trong 7 ngay...." hoặc "muc đo nguy hiem" sẽ chuyen sang fragment statitics
        TextView newRecentlyTxt = view.findViewById(R.id.txt_recently);
        if (newRecentlyTxt != null) {
            newRecentlyTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                    ft.replace(R.id.mainlayout, new FragmentStatistic());
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });
        }
        TextView newDangerLevelTxt = view.findViewById(R.id.txt_dangerlevel);
        if (newDangerLevelTxt != null) {
            newDangerLevelTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                    ft.replace(R.id.mainlayout, new FragmentStatistic());
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });
        }

//Bấm vô chữ "trong 7 ngay...." hoặc "muc đo nguy hiem" sẽ chuyen sang fragment statitics

//----------------------------Start of fetch data---------------------
        AppUser currentUser = AuthManager.getInstance().getAccount();
        if (currentUser != null && currentUser.getDistanceTraveled() != null
                && currentUser.getUsername() != null) {
            totalDistance.setText(String.valueOf(currentUser.getDistanceTraveled()));
            tvName.setText(String.valueOf(currentUser.getUsername()));
        } else {
            totalDistance.setText("0");
            tvName.setText("User");
        }
//----------------------------End of fetch data---------------------

        return view;
    }

    private void logout() {
        //Dang xuat cua thang thinh
        UserPreferences userPreferences = new UserPreferences(this.context);
        userPreferences.clearUserData();

        // Đăng xuất Firebase
        mAuth.signOut();

        // Đăng xuất tài khoản Google
        googleSignInClient.signOut().addOnCompleteListener(requireActivity(), task -> {
            if (task.isSuccessful()) {
                // Hiển thị thông báo và kết thúc Activity
                Toast.makeText(requireContext(), "Logout successful", Toast.LENGTH_SHORT).show();
                requireActivity().finish(); // Kết thúc Activity
            } else {
                // Xử lý khi có lỗi
                Toast.makeText(requireContext(), "Logout failed", Toast.LENGTH_SHORT).show();
            }
        });

        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

//------------------Cái này cho cái biểu đồ cột và tròn---------------------
private void fetchPotholeData(BarChart graph, PieChart pieChart) {
    PotholeManager.getInstance().getALLPotholes(new PotholeManager.GetPotholeCallBack() {
        @Override
        public void onSuccess(List<Pothole> potholes) {
            updateGraphView(graph, potholes);
            updatePieChart(pieChart, potholes);
            updateTotalPotholes(potholes);
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
        xAxis.setDrawLabels(false); // Disable x-axis labels
        xAxis.setDrawGridLines(false); // Optionally disable grid lines

        YAxis leftAxis = graph.getAxisLeft();
        leftAxis.setDrawLabels(false); // Disable y-axis labels
        leftAxis.setDrawGridLines(false); // Optionally disable grid lines
        leftAxis.setAxisMinimum(0f);

        YAxis rightAxis = graph.getAxisRight();
        rightAxis.setEnabled(false); // Disable right y-axis

        graph.getDescription().setEnabled(false);
        graph.invalidate(); // Refresh
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
        dataSet.setDrawValues(false); // Disable value labels

        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        pieChart.getLegend().setEnabled(false); // Disable legend
        pieChart.getDescription().setEnabled(false); // Disable description
        pieChart.invalidate(); // Refresh
    }
//------------------Cái này cho cái biểu đồ cột và tròn---------------------

    private void updateTotalPotholes(List<Pothole> potholes) {
        AppUser currentUser = AuthManager.getInstance().getAccount();
        if (currentUser != null && currentUser.getId() != null) {
            int userId = currentUser.getId();
            long totalPotholes = potholes.stream()
                    .filter(pothole -> pothole.getUserId() != null)
                    .filter(pothole -> pothole.getUserId().intValue() == userId)
                    .count();
            TextView totalPotholesTextView = getView().findViewById(R.id.total_potholes);
            if (totalPotholesTextView != null) {
                totalPotholesTextView.setText(String.valueOf(totalPotholes));
            }
        }
    }
}