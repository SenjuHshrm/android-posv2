package com.pylon.emarketpos.controllers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pylon.emarketpos.R;

public class MainApp extends Fragment{
    public MainApp() {
    }
    private Button StallColl, AmbColl;
    private TextView pageStat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_app, container, false);
        ImageButton settBtn = (ImageButton) getActivity().findViewById(R.id.openSettings);
        settBtn.setEnabled(true);
        pageStat = (TextView) getActivity().findViewById(R.id.pageStat);
        pageStat.setText(getResources().getString(R.string.toolbarTitleMenu));
        StallColl = (Button) view.findViewById(R.id.StallColl);
        AmbColl = (Button) view.findViewById(R.id.AmbulantColl);
        StallColl.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                TextView txtStat = getActivity().findViewById(R.id.pageStat);
                txtStat.setText(R.string.lblStall);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left).replace(R.id.fragment_container,new StallSearchForm(),"StallSearch").commit();
            }
        });
        AmbColl.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                TextView txtStat = getActivity().findViewById(R.id.pageStat);
                txtStat.setText(R.string.lblAmb);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left).replace(R.id.fragment_container,new AmbulantSearchForm(),"AmbulantSearch").commit();
            }
        });
        return view;
    }
}
