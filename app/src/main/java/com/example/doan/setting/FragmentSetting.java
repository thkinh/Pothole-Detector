package com.example.doan.setting;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.doan.R;
import com.example.doan.feature.Setting;

import java.util.Locale;


public class FragmentSetting extends Fragment {

    private RelativeLayout layou_vi, layout_en;

    public FragmentSetting() {
        // Required empty public constructor
    }

    public static FragmentSetting newInstance(String param1, String param2) {
        FragmentSetting fragment = new FragmentSetting();
        Bundle args = new Bundle();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        // Find the RelativeLayout by ID
        layou_vi = rootView.findViewById(R.id.st_lang_vi);
        layout_en = rootView.findViewById(R.id.st_lang_en);
        layou_vi.setOnClickListener(v -> {
            Setting.getInstance().setAppLanguage(Setting.AppLanguage.vi);
            Setting.getInstance().applyLanguage(requireContext());
            Setting.getInstance().saveToPreferences(requireContext()); // Save selection
            Toast.makeText(requireContext(), "Vietnamese", Toast.LENGTH_SHORT).show();
            refreshFragment(); // Refresh to apply changes
        });

        layout_en.setOnClickListener(view -> {
            Setting.getInstance().setAppLanguage(Setting.AppLanguage.en_US);
            Setting.getInstance().applyLanguage(requireContext());
            Setting.getInstance().saveToPreferences(requireContext()); // Save selection
            Toast.makeText(requireContext(), "English-US", Toast.LENGTH_SHORT).show();
            refreshFragment(); // Refresh to apply changes
        });
        return rootView;
    }

    private void refreshFragment() {
        Fragment currentFragment = getParentFragmentManager().findFragmentById(R.id.mainlayout);
        if (currentFragment != null) {
            getParentFragmentManager()
                    .beginTransaction()
                    .detach(currentFragment)
                    .attach(currentFragment)
                    .commit();
        }
    }
}