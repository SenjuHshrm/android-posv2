package com.pylon.emarketpos.controllers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
        ImageButton settBtn = (ImageButton) getActivity().findViewById(R.id.openSettings);
        settBtn.setEnabled(false);
        saveSettings.setOnClickListener(this);
        return view;
    }
    @Override
    public void onClick(View view) {
        EditText GetIPAdd = (EditText) getActivity().findViewById(R.id.SetIP);
        String ipAdd = GetIPAdd.getText().toString();
        if(ipAdd.isEmpty()){
            Toast.makeText(getActivity(), "Empty input field.", Toast.LENGTH_SHORT).show();
        } else {
            DatabaseHelper dbHelp = new DatabaseHelper(getActivity());
            boolean res = dbHelp.saveIP(ipAdd);
            if(res){
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CheckConnection()).commit();
            }else{
                Toast.makeText(getActivity(),"An error occured while saving data.",Toast.LENGTH_LONG).show();
            }
        }
    }
}
