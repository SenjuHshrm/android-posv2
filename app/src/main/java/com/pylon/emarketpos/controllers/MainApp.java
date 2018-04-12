package com.pylon.emarketpos.controllers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pylon.emarketpos.R;

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
        ToolbarFrag tbFrag = new ToolbarFrag();
        Bundle bn = new Bundle();
        bn.putString("Stat",getString(R.string.toolbarTitleMenu));
        tbFrag.setArguments(bn);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.StatToolbar,tbFrag).commit();
        return view;
    }
    @Override
    public void onClick(View v){
        ToolbarFrag tbFrag = new ToolbarFrag();
        AmbulantSearchForm ambSeearch = new AmbulantSearchForm();
        StallSearchForm stallSearch = new StallSearchForm();

        Bundle StatText = new Bundle();
        switch(v.getId()){
            case R.id.StallColl:
                StatText.putString("Stat",getString(R.string.stallCol));
                tbFrag.setArguments(StatText);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.StatToolbar,tbFrag).addToBackStack("StatToolbar").commit();

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,stallSearch,"StallSearch").commit();
                break;
            case R.id.AmbulantColl:
                StatText.putString("Stat",getString(R.string.ambCol));
                tbFrag.setArguments(StatText);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.StatToolbar,tbFrag).addToBackStack("StatToolbar").commit();

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,ambSeearch,"AmbulantSearch").commit();
                break;
        }
    }
}
