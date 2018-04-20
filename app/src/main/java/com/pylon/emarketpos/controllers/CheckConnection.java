package com.pylon.emarketpos.controllers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pylon.emarketpos.R;
import com.pylon.emarketpos.tasks.EstablishConn;

public class CheckConnection extends Fragment {

    public CheckConnection() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        new EstablishConn(getContext(),savedInstanceState, this).execute();
        return inflater.inflate(R.layout.fragment_check_connection, container, false);
    }

}
