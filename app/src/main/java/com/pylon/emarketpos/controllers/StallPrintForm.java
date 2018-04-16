package com.pylon.emarketpos.controllers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.pylon.emarketpos.R;
import com.pylon.emarketpos.tasks.SavePayment;

public class StallPrintForm extends Fragment {
    private EditText StallNum, OwnerName, BusinessType, Amount;
    private Button stallPrint;

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
        stallPrint.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                final String pStallNum = StallNum.getText().toString();
                final String pOwnerName = OwnerName.getText().toString();
                final String pBusiness = BusinessType.getText().toString();
                final String pAmount = Amount.getText().toString();
                new SavePayment(getContext()).execute(pStallNum,pOwnerName,pBusiness,pAmount);
            }
        });
        return view;
    }

}
