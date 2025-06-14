package com.example.doan.setting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doan.DetectActivity;
import com.example.doan.R;
import com.example.doan.dashboard.FragmentDashboard;
import com.example.doan.dashboard.FragmentStatistic;
import com.example.doan.feature.Setting;
import com.example.doan.feature.UserPreferences;
import com.example.doan.login.LoginActivity;
import com.google.android.material.card.MaterialCardView;

import java.util.Locale;


public class FragmentSetting extends Fragment {

    private RelativeLayout layou_vi, layout_en;
    private Switch switchContribute;
    private ImageButton btn_destroy_setting;
    private Button btn_stLogout;
    private MaterialCardView profile;
    private TextView tv_chooseSense;
    private TextView txtVietnamese, txtEnglish;

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
        loadLanguage();
        Bundle args = getArguments();
        boolean isDetectActivity = args != null && args.getBoolean("isDetectActivity", false);

        // Find the RelativeLayout by ID
        layou_vi = rootView.findViewById(R.id.st_lang_vi);
        layout_en = rootView.findViewById(R.id.st_lang_en);
        txtVietnamese = rootView.findViewById(R.id.txt_vietnamese);
        txtEnglish = rootView.findViewById(R.id.txt_english);

        //Set switch state based on the current language
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("LANGUAGE_SETTINGS", Context.MODE_PRIVATE);
        String language = sharedPreferences.getString("language", "en");

        // Set text style based on the current language
        if (language.equals("vi")) {
            txtVietnamese.setTypeface(null, Typeface.BOLD);
            txtEnglish.setTypeface(null, Typeface.NORMAL);
        } else {
            txtVietnamese.setTypeface(null, Typeface.NORMAL);
            txtEnglish.setTypeface(null, Typeface.BOLD);
        }

        layou_vi.setOnClickListener(v -> {
            setLanguage("vi", 1);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.mainlayout, new FragmentSetting())
                    .commit();
        });

        layout_en.setOnClickListener(view -> {
            setLanguage("en", 0);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.mainlayout, new FragmentSetting())
                    .commit();
        });

        if (isDetectActivity){
            layout_en.setEnabled(false);
            layou_vi.setEnabled(false);
            layou_vi.setBackground(new ColorDrawable(ContextCompat.getColor(requireContext(), R.color.opacity_black)));
            layout_en.setBackground(new ColorDrawable(ContextCompat.getColor(requireContext(), R.color.opacity_black)));
        }

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

         btn_destroy_setting = rootView.findViewById(R.id.destroySetting);
         btn_destroy_setting.setOnClickListener(/*view -> {
             assert getActivity() != null;
             getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
         }*/new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                 ft.replace(R.id.mainlayout, new FragmentDashboard());
                 ft.addToBackStack(null);
                 ft.commit();
             }
         });


        profile.setOnClickListener(view -> {
            Intent intent = new Intent(this.getContext(), ProfileActivity.class );
            startActivity(intent);
        });

        switchContribute = rootView.findViewById(R.id.switch_contribute);
        // Initialize the switch state from the Setting instance
        switchContribute.setChecked(Setting.getInstance().getIsContributor());

        if (isDetectActivity) {
            RelativeLayout lo_contribute = rootView.findViewById(R.id.lo_contribute);
            lo_contribute.setEnabled(false);
            lo_contribute.setBackground(new ColorDrawable(ContextCompat.getColor(requireContext(), R.color.opacity_black)));
            switchContribute.setEnabled(false);
        }
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
        if (Setting.getInstance().getSensitiveConstance() == 0.9055){
            tv_chooseSense.setText("Low");
        }
        else if (Setting.getInstance().getSensitiveConstance() == 0.9030){
            tv_chooseSense.setText("Medium");
        }
        else if (Setting.getInstance().getSensitiveConstance() == 0.9000){
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
                        Setting.getInstance().setSensitiveConstance(0.9055);
                        break;
                    case "Medium":
                        // Perform action for "Medium"
                        Setting.getInstance().setSensitivity(Setting.Sensitivity.MEDIUM);
                        Setting.getInstance().setSensitiveConstance(0.9030);
                        break;
                    case "High":
                        // Perform action for "High"
                        Setting.getInstance().setSensitivity(Setting.Sensitivity.HIGH);
                        Setting.getInstance().setSensitiveConstance(0.9000);
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

    private void setLanguage(String language, int item) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        requireContext().getResources().updateConfiguration(configuration, requireContext().getResources().getDisplayMetrics());

        //save language
        SharedPreferences.Editor editor = requireContext().getSharedPreferences("LANGUAGE_SETTINGS", Context.MODE_PRIVATE).edit();
        editor.putString("language", language);
        editor.putInt("item", item);
        editor.apply();
    }// set language end here

    private void loadLanguage(){
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("LANGUAGE_SETTINGS", Context.MODE_PRIVATE);
        String language = sharedPreferences.getString("language", "en");
        int item = sharedPreferences.getInt("item", 0);
        setLanguage(language, 0);
    }
}