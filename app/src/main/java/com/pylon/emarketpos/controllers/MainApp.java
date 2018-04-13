package com.pylon.emarketpos.controllers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pylon.emarketpos.R;

public class MainApp extends Fragment{
    public MainApp() {
    }
    private Button StallColl, AmbColl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_app, container, false);
        StallColl = (Button) view.findViewById(R.id.StallColl);
        AmbColl = (Button) view.findViewById(R.id.AmbulantColl);
        StallColl.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Bundle Stat = new Bundle();
                Stat.putString("Stat",getString(R.string.stallCol));
                ToolbarFrag tb = new ToolbarFrag();
                tb.setArguments(Stat);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.StatToolbar,tb).commit();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new StallSearchForm(),"StallSearch").commit();
            }
        });
        AmbColl.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Bundle Stat = new Bundle();
                Stat.putString("Stat",getString(R.string.ambCol));
                ToolbarFrag tb = new ToolbarFrag();
                tb.setArguments(Stat);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.StatToolbar,tb).commit();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AmbulantSearchForm(),"AmbulantSearch").commit();
            }
        });
        ToolbarFrag tbFrag = new ToolbarFrag();
        Bundle bn = new Bundle();
        bn.putString("Stat",getString(R.string.toolbarTitleMenu));
        tbFrag.setArguments(bn);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.StatToolbar,tbFrag).commit();
        return view;
    }
}
