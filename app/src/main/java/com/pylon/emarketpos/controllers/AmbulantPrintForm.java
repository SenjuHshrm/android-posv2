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

public class AmbulantPrintForm extends Fragment implements View.OnClickListener{
    private EditText OwnerName, Business, Amount;
    private Button ambPrint;
    private String[] SendInfo;
    public AmbulantPrintForm() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ambulant_print_form, container, false);
        Bundle x = getArguments();
        OwnerName = (EditText) view.findViewById(R.id.Print_AmbName);
        Business = (EditText) view.findViewById(R.id.Print_Business);
        Amount = (EditText) view.findViewById(R.id.Print_Amount);
        ambPrint = (Button) view.findViewById(R.id.btnPrintAmb);
        OwnerName.setText(x.getString("AmbOwner"));
        Business.setText(x.getString("AmbBusiness"));
        ambPrint.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        TextView DevUser = (TextView) getActivity().findViewById(R.id.DeviceUserDisplay);
        final String pOwnerName = OwnerName.getText().toString();
        final String pBusiness = Business.getText().toString();
        final String pAmount = Amount.getText().toString();
        SendInfo = new String[10];
        SendInfo[0] = "ambulant";
        SendInfo[1] = pOwnerName;
        SendInfo[2] = pBusiness;
        SendInfo[3] = pAmount;
        SendInfo[4] = DevUser.getText().toString();
        new SavePayment(getContext(),this).execute(SendInfo);
    }
}
