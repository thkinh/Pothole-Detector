package com.example.doan;

import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class AllUsersActivity extends AppCompatActivity {

    private ListAdapterUser listAdapterUser;
    private ArrayList<ListDataUser> userArrayList = new ArrayList<>();
    private ListDataUser listDataUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.at_allusers);

        // Set up the user list
        int[] userImageList = {R.drawable.avt_linhxeom, R.drawable.avt_taichodien, R.drawable.avt_linhxeom};
        String[] userNameList = {"Linh xe ôm", "Tài chó điên", "Linh 14"};
        String[] dateList = {"05/11/2024", "20/10/2024", "01/04/2014"};
        for (int i = 0; i < userImageList.length; i++) {
            listDataUser = new ListDataUser(userNameList[i], dateList[i], userImageList[i]);
            userArrayList.add(listDataUser);
        }
        listAdapterUser = new ListAdapterUser(this, userArrayList);
        ListView listViewRoute = findViewById(R.id.listuser);
        listViewRoute.setAdapter(listAdapterUser);
    }
}