package com.example.doan;

import androidx.credentials.CredentialManager;
import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;
import java.util.concurrent.Executor;

public class FragmentDashboard extends Fragment {

    private ListAdapterUser listAdapterUser;
    private ArrayList<ListDataUser> userArrayList = new ArrayList<>();
    private ListDataUser listDataUser;

    private ListAdapterRoute listAdapterRoute;
    private ArrayList<ListDataRoute> routeArrayList = new ArrayList<>();
    private ListDataRoute listDataRoute;

    private PieChart pieChart;

    // For Firebase Auth and Google Sign In
    private ImageView ivImage;
    private TextView tvName;
    private ImageButton btLogout;
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;

    public FragmentDashboard() {
        // Required empty public constructor
    }

    Activity context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        context = getActivity();

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

        // Set up the user list
        int[] userImageList = {R.drawable.avt_linhxeom, R.drawable.avt_taichodien, R.drawable.avt_linhxeom};
        String[] userNameList = {"Linh xe ôm", "Tài chó điên", "Linh 14"};
        String[] dateList = {"05/11/2024", "20/10/2024", "01/04/2014"};
        for (int i = 0; i < userImageList.length; i++) {
            listDataUser = new ListDataUser(userNameList[i], dateList[i], userImageList[i]);
            userArrayList.add(listDataUser);
        }
        listAdapterUser = new ListAdapterUser(getContext(), userArrayList);
        ListView listViewUser = view.findViewById(R.id.listuser);
        listViewUser.setAdapter(listAdapterUser);

        // Set up the route list
        int[] routeImageList = {R.drawable.img_pothole, R.drawable.img_pothole2, R.drawable.img_pothole};
        String[] routeNameList = {"Đặng Văn Cẩn", "Võ Văn Ngân", "Nguyễn Thị Thập"};
        String[] quantityList = {"6 potholes", "9 potholes", "3 potholes"};
        for (int i = 0; i < routeImageList.length; i++) {
            listDataRoute = new ListDataRoute(routeNameList[i], quantityList[i], routeImageList[i]);
            routeArrayList.add(listDataRoute);
        }
        listAdapterRoute = new ListAdapterRoute(getContext(), routeArrayList);
        ListView listViewRoute = view.findViewById(R.id.listroute);
        listViewRoute.setAdapter(listAdapterRoute);

        // Assign variables
        ivImage = view.findViewById(R.id.avt);
        tvName = view.findViewById(R.id.txt_name);
        btLogout = view.findViewById(R.id.ic_power);

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
                .requestIdToken(getString(R.string.default_web_client_id)) // Thay bằng ID client của bạn trong google-services.json
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        // Gắn sự kiện cho nút Logout
        btLogout = view.findViewById(R.id.ic_power);
        btLogout.setOnClickListener(v -> logout());

        return view;
    }

    private void logout() {
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

    @Override
    public void onStart() {
        super.onStart();

        if (context != null) {
            TextView newDetailUserTxt = context.findViewById(R.id.viewdetail_user);
            if (newDetailUserTxt != null) {
                newDetailUserTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, AllUsersActivity.class);
                        startActivity(intent);
                    }
                });
            }

            TextView newDetailRouteTxt = context.findViewById(R.id.viewdetail_route);
            if (newDetailRouteTxt != null) {
                newDetailRouteTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, StatiticsActivity.class);
                        startActivity(intent);
                    }
                });
            }

            TextView newRecentlyTxt = context.findViewById(R.id.txt_recently);
            if (newRecentlyTxt != null) {
                newRecentlyTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, StatiticsActivity.class);
                        startActivity(intent);
                    }
                });
            }
            TextView newDangerLevelTxt = context.findViewById(R.id.txt_dangerlevel);
            if (newDangerLevelTxt != null) {
                newDangerLevelTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, StatiticsActivity.class);
                        startActivity(intent);
                    }
                });
            }
        }
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