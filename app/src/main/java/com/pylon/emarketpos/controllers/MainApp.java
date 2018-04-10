package com.pylon.emarketpos.controllers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.pylon.emarketpos.R;
import com.pylon.emarketpos.tasks.GetInfo;

public class MainApp extends Fragment implements View.OnClickListener{
    public MainApp() {
    }
    private Button StallColl, AmbColl;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_app, container, false);
        StallColl = (Button) view.findViewById(R.id.StallColl);
        AmbColl = (Button) view.findViewById(R.id.AmbulantColl);
        StallColl.setOnClickListener(this);
        AmbColl.setOnClickListener(this);
        return view;
    }
    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.StallColl:
                new GetInfo(getContext(),"STALL",MainApp.this);
                break;
            case R.id.AmbulantColl:
                new GetInfo(getContext(),"AMBULANT",MainApp.this);
                break;
        }
    }
}
