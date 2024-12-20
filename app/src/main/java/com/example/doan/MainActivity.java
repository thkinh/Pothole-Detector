package com.example.doan;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.doan.databinding.AtMainBinding;
import com.example.doan.interfaceFragment.OnMapFragmentInteractionListener;

import java.util.NavigableMap;


public class MainActivity extends AppCompatActivity implements OnMapFragmentInteractionListener {

    AtMainBinding binding;
    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AtMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        replaceFragment(new FragmentDashboard());

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.btn_home) {
                replaceFragment(new FragmentDashboard());
            } else if (item.getItemId() == R.id.btn_map) {
                replaceFragment(new FragmentMap());
            } else if (item.getItemId() == R.id.btn_setting) {
                replaceFragment(new FragmentSetting());
            }
            return true;
        });
    }


    @Override
    public void onMapButtonClicked(Fragment fragment) {
        replaceFragment(fragment);
    }


    private void replaceFragment(Fragment fragment) {
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainlayout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Handle the configuration change if needed
    }

}