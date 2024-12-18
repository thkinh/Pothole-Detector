package com.example.doan;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class FragmentUser extends Fragment {

    private ListAdapterUser listAdapterUser;
    private ArrayList<ListDataUser> userArrayList = new ArrayList<>();
    private ListDataUser listDataUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

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

        return view;
    }


}