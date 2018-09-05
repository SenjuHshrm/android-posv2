package com.pylon.emarketpos.controllers;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pylon.emarketpos.R;
import com.pylon.emarketpos.tasks.SavePayment;

public class StallPrintForm extends Fragment implements OnClickListener{
    private EditText StallNum, OwnerName, BusinessType, Amount;
    private Button stallPrint;
    private String[] SendInfo;
    private String CustID;
    private InputMethodManager imm;
    public static String SEARCH_DATA;
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
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        StallNum.setText(x.getString("StallNumber"));
        OwnerName.setText(x.getString("OwnerName"));
        BusinessType.setText(x.getString("BusinessType"));
        CustID = x.getString("CustomerID");
        SEARCH_DATA = x.getString("DATA_SEARCH");
        Amount.requestFocus();
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        stallPrint.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        TextView DevUser = (TextView) getActivity().findViewById(R.id.device_user);
        final String pStallNum = StallNum.getText().toString();
        final String pOwnerName = OwnerName.getText().toString();
        final String pBusiness = BusinessType.getText().toString();
        final String pAmount = Amount.getText().toString();
        SendInfo = new String[10];
        SendInfo[0] = "stall";
        SendInfo[1] = pStallNum;
        SendInfo[2] = pBusiness;
        SendInfo[3] = pAmount;
        SendInfo[4] = DevUser.getText().toString();
        SendInfo[5] = CustID;
        SendInfo[6] = pOwnerName;
        if(pAmount.isEmpty()){
            Toast.makeText(getContext(), "Amount cannot be empty", Toast.LENGTH_SHORT).show();
        } else {
            new SavePayment(getContext(), this).execute(SendInfo);
        }
    }
}
