package com.pylon.emarketpos.controllers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pylon.emarketpos.R;
import com.pylon.emarketpos.tasks.EstablishConn;

public class CheckConnection extends Fragment {

    public CheckConnection() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_connection, container, false);
        new EstablishConn(getContext(),savedInstanceState, this).execute();
        ImageButton settBtn = (ImageButton) getActivity().findViewById(R.id.openSettings);
        settBtn.setVisibility(View.INVISIBLE);
        TextView stat = (TextView) getActivity().findViewById(R.id.pageStat);
        stat.setText("");
        return view;
    }

}
