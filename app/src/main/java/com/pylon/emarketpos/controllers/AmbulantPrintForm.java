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

public class AmbulantPrintForm extends Fragment implements OnClickListener{
    private EditText OwnerName, Business, Amount;
    private Button ambPrint;
    private String[] SendInfo;
    private String CustID;
    private InputMethodManager imm;
    public static String SEARCH_DATA;
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
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        OwnerName.setText(x.getString("AmbOwner"));
        Business.setText(x.getString("AmbBusiness"));
        CustID = x.getString("CustomerID");
        SEARCH_DATA = x.getString("DATA_SEARCH");
        Amount.requestFocus();
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        ambPrint.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        TextView DevUser = (TextView) getActivity().findViewById(R.id.device_user);
        final String pOwnerName = OwnerName.getText().toString();
        final String pBusiness = Business.getText().toString();
        final String pAmount = Amount.getText().toString();
        SendInfo = new String[10];
        SendInfo[0] = "ambulant";
        SendInfo[1] = SEARCH_DATA;
        SendInfo[2] = pOwnerName;
        SendInfo[3] = pBusiness;
        SendInfo[4] = pAmount;
        SendInfo[5] = DevUser.getText().toString();
        SendInfo[6] = CustID;
        if(pAmount.isEmpty()){
            Toast.makeText(getContext(), "Amount cannot be empty", Toast.LENGTH_SHORT).show();
        } else {
            new SavePayment(getContext(), this).execute(SendInfo);
        }
    }
}
