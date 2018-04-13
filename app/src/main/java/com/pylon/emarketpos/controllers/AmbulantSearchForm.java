package com.pylon.emarketpos.controllers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.pylon.emarketpos.R;

public class AmbulantSearchForm extends Fragment{
    private EditText AmbSearch;
    public AmbulantSearchForm() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_ambulant_search_form, container, false);
        AmbSearch = (EditText) view.findViewById(R.id.AmbInputData);
        return view;
    }

}
