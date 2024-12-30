package com.example.doan.setting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doan.R;
import com.example.doan.feature.Setting;
import com.example.doan.feature.UserPreferences;
import com.example.doan.login.LoginActivity;
import com.google.android.material.card.MaterialCardView;

import java.util.Locale;


public class FragmentSetting extends Fragment {

    private RelativeLayout layou_vi, layout_en;
    private Switch switchContribute;
    //private Button btn_stProfile;
    private Button btn_stLogout;
    private MaterialCardView profile;
    private TextView tv_chooseSense;

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

        profile = rootView.findViewById(R.id.profile);
        //btn_stProfile = rootView.findViewById(R.id.btn_stprofile);
        btn_stLogout = rootView.findViewById(R.id.btn_stlogout);

        btn_stLogout.setOnClickListener(view -> {
            UserPreferences userPreferences = new UserPreferences(requireContext());
            userPreferences.clearUserData();
            Intent intent = new Intent(this.getContext(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });


        profile.setOnClickListener(view -> {
            Intent intent = new Intent(this.getContext(), ProfileActivity.class );
            startActivity(intent);
        });

        layout_en.setOnClickListener(view -> {
            Setting.getInstance().setAppLanguage(Setting.AppLanguage.en_US);
            Setting.getInstance().applyLanguage(requireContext());
            Setting.getInstance().saveToPreferences(requireContext()); // Save selection
            Toast.makeText(requireContext(), "English-US", Toast.LENGTH_SHORT).show();
            refreshFragment(); // Refresh to apply changes
        });

        switchContribute = rootView.findViewById(R.id.switch_contribute);
        // Initialize the switch state from the Setting instance
        switchContribute.setChecked(Setting.getInstance().getIsContributor());

        // Set OnCheckedChangeListener to handle click
        switchContribute.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // The switch is turned ON
                Toast.makeText(getContext(), "Contributor mode enabled", Toast.LENGTH_SHORT).show();
                // You can update the Setting instance here or save to SharedPreferences
                Setting.getInstance().setContributor(true);
                Setting.getInstance().saveToPreferences(requireContext());
            } else {
                // The switch is turned OFF
                Toast.makeText(getContext(), "Contributor mode disabled", Toast.LENGTH_SHORT).show();
                // Update the Setting instance or SharedPreferences
                Setting.getInstance().setContributor(false);
                Setting.getInstance().saveToPreferences(requireContext());
            }
        });

        tv_chooseSense = rootView.findViewById(R.id.choose_sensitivity);
        if (Setting.getInstance().getSensitiveConstant() == 0.9055){
            tv_chooseSense.setText("Low");
        }
        else if (Setting.getInstance().getSensitiveConstant() == 0.9030){
            tv_chooseSense.setText("Medium");
        }
        else if (Setting.getInstance().getSensitiveConstant() == 0.9000){
            tv_chooseSense.setText("High");
        }
        tv_chooseSense.setOnClickListener(view -> {
            // Create an array of options
            String[] options = {"Low", "Medium", "High"};
            // Build the AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Choose Sensitivity");
            builder.setItems(options, (dialog, which) -> {
                // Update TextView text based on selection
                tv_chooseSense.setText(options[which]);
                // Perform actions based on selected value
                switch (options[which]) {
                    case "Low":
                        // Perform action for "Low"
                        Setting.getInstance().setSensitivity(Setting.Sensitivity.LOW);
                        Setting.getInstance().setSensitiveConstant(0.9055);
                        break;
                    case "Medium":
                        // Perform action for "Medium"
                        Setting.getInstance().setSensitivity(Setting.Sensitivity.MEDIUM);
                        Setting.getInstance().setSensitiveConstant(0.9030);
                        break;
                    case "High":
                        // Perform action for "High"
                        Setting.getInstance().setSensitivity(Setting.Sensitivity.HIGH);
                        Setting.getInstance().setSensitiveConstant(0.9000);
                        break;
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            // Show the dialog
            builder.create().show();
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