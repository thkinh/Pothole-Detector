package com.example.doan;

import androidx.credentials.CredentialManager;
import androidx.annotation.NonNull;

import com.example.doan.feature.UserPreferences;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

    private PieChart pieChart;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        context = getActivity();

        // Assign variables
        ivImage = view.findViewById(R.id.avt);
        tvName = view.findViewById(R.id.txt_name);
        btLogout = view.findViewById(R.id.ic_power);
        btnUser = view.findViewById(R.id.switch_to_user);
        btnRoute = view.findViewById(R.id.switch_to_route);
        btnRoute.setTextColor(getResources().getColor(R.color.gray));

        // Add this block to ensure FragmentUser is displayed first
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_switching, new FragmentUser());
        ft.commit();
        btnUser.setBackgroundColor(getResources().getColor(R.color.darkpacificblue));
        btnUser.setTextColor(getResources().getColor(R.color.white));

//--------------------------Start of statistics--------------------------
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
                .requestIdToken(getString(R.string.default_web_client_id)) // Thay bằng ID client của bạn trong google-services.json
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

//------------------Cái này cho cái biểu đồ cột---------------------
    private DataPoint[] getDataPoint() {
        return new DataPoint[]{
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        };
    }
//------------------Cái này cho cái biểu đồ cột---------------------

}