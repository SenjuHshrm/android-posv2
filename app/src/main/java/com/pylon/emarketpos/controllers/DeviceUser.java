package com.pylon.emarketpos.controllers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pylon.emarketpos.R;

public class DeviceUser extends Fragment {


    public DeviceUser() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_user, container, false);
        TextView devUser = view.findViewById(R.id.device_user);
        Bundle user = getArguments();
        devUser.setText(user.getString("DevUser"));
        return view;
    }

}
