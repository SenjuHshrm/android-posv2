package com.pylon.emarketpos.controllers;


import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pylon.emarketpos.R;
import com.pylon.emarketpos.interfaces.SearchDataResponse;
import com.pylon.emarketpos.tasks.GetList;

public class CustomerList extends Fragment implements SearchDataResponse{
    GetList getList;
    private String type;
    private Button searchReqData;
    private EditText searchIn;
    private String searchResData;
    private String RequestData;
    public CustomerList() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_customer_list, container, false);
        TextView listType = (TextView) view.findViewById(R.id.ListType);
        TextInputLayout txtInLayout = (TextInputLayout) view.findViewById(R.id.searchInputLayout);
        type = getArguments().getString("type");
        if(type.equalsIgnoreCase("stall")){
            listType.setText(getActivity().getString(R.string.lblStallList));
            txtInLayout.setHint(getActivity().getString(R.string.hintSearchStall));
        }else if(type.equalsIgnoreCase("ambulant")){
            listType.setText(getActivity().getString(R.string.lblAmbList));
            txtInLayout.setHint(getActivity().getString(R.string.hintSearchAmbulant));
        }
        txtInLayout.setEnabled(true);
        getList = new GetList(getContext(),type);
        getList.mCallBack = this;
        searchIn = (EditText) view.findViewById(R.id.SearchInputData);
        searchIn.setEnabled(true);
        searchReqData = (Button) view.findViewById(R.id.SearchData);
        searchReqData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestData = searchIn.getText().toString();
                if(RequestData.equals("")){
                    Toast.makeText(getContext(),"No input.",Toast.LENGTH_LONG).show();
                }else{
                    getList.execute(RequestData);
                }
            }
        });
        return view;
    }

    @Override
    public void responseData(String str) {
        this.searchResData = str;
    }
}
