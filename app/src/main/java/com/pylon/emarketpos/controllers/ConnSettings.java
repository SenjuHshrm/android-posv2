package com.pylon.emarketpos.controllers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.pylon.emarketpos.R;
import com.pylon.emarketpos.tasks.DatabaseHelper;
import com.pylon.emarketpos.tasks.GetTransactions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ConnSettings extends Fragment implements OnClickListener {
    private DatabaseHelper dbHelper;
    private EditText GetIPAdd;
    private Button printColl, saveSettings;
    private TextView Inst;
    public ConnSettings() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conn_settings, container, false);
        dbHelper = new DatabaseHelper(getContext());
        String ip = dbHelper.selectIP();
        GetIPAdd = (EditText) view.findViewById(R.id.SetIP);
        GetIPAdd.setText(ip);
        saveSettings = (Button) view.findViewById(R.id.btnSaveSett);
        printColl = (Button) view.findViewById(R.id.btnPrintCollection);
        ImageButton settBtn = (ImageButton) getActivity().findViewById(R.id.openSettings);
        settBtn.setVisibility(View.INVISIBLE);
        saveSettings.setOnClickListener(this);
        printColl.setOnClickListener(this);
        checkAuth(view);
        return view;
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnSaveSett:
                GetIPAdd = (EditText) getActivity().findViewById(R.id.SetIP);
                String ipAdd = GetIPAdd.getText().toString();
                if(ipAdd.isEmpty()){
                    Toast.makeText(getActivity(), "Empty input field.", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseHelper dbHelp = new DatabaseHelper(getActivity());
                    boolean res = dbHelp.saveIP(ipAdd);
                    if(res){
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CheckConnection(), "TestConn").commit();
                    }else{
                        Toast.makeText(getActivity(),"An error occured while saving data.",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.btnPrintCollection:
                String user = new DatabaseHelper(getContext()).getDeviceUser();
                String currDate =getCurrDate();
                new GetTransactions(getContext()).execute(user, currDate);
                break;
        }
    }
    private String getCurrDate(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = df.format(c);
        return formattedDate;
    }
    private void checkAuth(View v){
        printColl = (Button) v.findViewById(R.id.btnPrintCollection);
        Inst = (TextView) v.findViewById(R.id.textView15);
        String user = new DatabaseHelper(getContext()).getDeviceUser();
        if(user.isEmpty()) {
            printColl.setVisibility(View.INVISIBLE);
            Inst.setVisibility(View.INVISIBLE);
        } else {
            printColl.setVisibility(View.VISIBLE);
            Inst.setVisibility(View.VISIBLE);
        }
    }
}
