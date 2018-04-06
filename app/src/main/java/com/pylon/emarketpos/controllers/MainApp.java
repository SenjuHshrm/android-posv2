package com.pylon.emarketpos.controllers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pylon.emarketpos.MainView;
import com.pylon.emarketpos.R;

public class MainApp extends Fragment {
    public MainApp() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_app, container, false);

        return view;
    }
}
