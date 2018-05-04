package com.pylon.emarketpos.controllers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pylon.emarketpos.R;
import com.pylon.emarketpos.tasks.DatabaseHelper;

public class ConnSettings extends Fragment implements View.OnClickListener{
    public ConnSettings() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conn_settings, container, false);
        Button saveSettings = (Button) view.findViewById(R.id.btnSaveSett);
        saveSettings.setOnClickListener(this);
        return view;
    }
    @Override
    public void onClick(View view) {
        EditText GetIPAdd = (EditText) getActivity().findViewById(R.id.SetIP);
        String ipAdd = GetIPAdd.getText().toString();
        DatabaseHelper dbHelp = new DatabaseHelper(getActivity());
        boolean res = dbHelp.saveIP(ipAdd);
        if(res == true){
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CheckConnection()).commit();
        }else if(res == false){
            Toast.makeText(getActivity(),"An error occured while saving data.",Toast.LENGTH_LONG).show();
        }
    }
}
