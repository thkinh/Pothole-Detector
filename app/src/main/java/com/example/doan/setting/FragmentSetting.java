package com.example.doan.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.doan.R;
import com.example.doan.feature.UserPreferences;
import com.example.doan.login.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;

public class FragmentSetting extends Fragment {


    private MaterialCardView userProfileCard;
    private MaterialCardView logoutCard;

    Activity context;
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        mAuth = FirebaseAuth.getInstance();

        // Lấy tham chiếu đến các CardView
        userProfileCard = view.findViewById(R.id.userprofile);
        logoutCard = view.findViewById(R.id.logout);

        // Thiết lập OnClickListener cho userProfileCard
        userProfileCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        // Thiết lập OnClickListener cho logoutCard
        logoutCard.setOnClickListener(v -> logout());

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
}