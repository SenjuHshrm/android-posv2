package com.pylon.emarketpos.controllers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pylon.emarketpos.R;

public class ToolbarFrag extends Fragment {

    public ToolbarFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_toolbar, container, false);
        TextView StatToolbar = (TextView) view.findViewById(R.id.TxtViewStatBar);
        String res = getArguments().getString("Stat");
        if(res.equals(getString(R.string.stallCol))){
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new StallSearchForm(),"StallSearch").commit();
        }else if(res.equals(getString(R.string.ambCol))){
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AmbulantSearchForm(),"AmbulantSearch").commit();
        }
        StatToolbar.setText(getArguments().getString("Stat"));
        return view;
    }

}
