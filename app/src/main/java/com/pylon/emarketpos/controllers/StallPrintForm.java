package com.pylon.emarketpos.controllers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pylon.emarketpos.R;
import com.pylon.emarketpos.tasks.SavePayment;

public class StallPrintForm extends Fragment implements View.OnClickListener{
    private EditText StallNum, OwnerName, BusinessType, Amount;
    private Button stallPrint;
    private String[] SendInfo;
    public StallPrintForm() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stall_print_form, container, false);
        Bundle x = getArguments();
        StallNum = (EditText) view.findViewById(R.id.Print_StallNum);
        OwnerName = (EditText) view.findViewById(R.id.Print_OwnerName);
        BusinessType = (EditText) view.findViewById(R.id.Print_BusinessType);
        Amount = (EditText) view.findViewById(R.id.Print_Amt);
        stallPrint = (Button) view.findViewById(R.id.btnPrintStall);
        StallNum.setText(x.getString("StallNumber"));
        OwnerName.setText(x.getString("OwnerName"));
        BusinessType.setText(x.getString("BusinessType"));
        stallPrint.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        TextView DevUser = (TextView) getActivity().findViewById(R.id.DeviceUserDisplay);
        final String pStallNum = StallNum.getText().toString();
        final String pOwnerName = OwnerName.getText().toString();
        final String pBusiness = BusinessType.getText().toString();
        final String pAmount = Amount.getText().toString();
        SendInfo = new String[10];
        SendInfo[0] = "stall";
        SendInfo[1] = pStallNum;
        SendInfo[2] = pOwnerName;
        SendInfo[3] = pBusiness;
        SendInfo[4] = pAmount;
        SendInfo[5] = DevUser.getText().toString();
        new SavePayment(getContext(),this).execute(SendInfo);
    }
}
