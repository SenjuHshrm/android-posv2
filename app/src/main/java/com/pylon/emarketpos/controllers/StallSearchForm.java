package com.pylon.emarketpos.controllers;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.pylon.emarketpos.R;

public class StallSearchForm extends Fragment {
    private String ResponseData;
    private EditText inputReq;
    private Button searchBtn;
    public StallSearchForm() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stall_search_form, container, false);
        inputReq = (EditText) view.findViewById(R.id.StallInputData);
        searchBtn = (Button) view.findViewById(R.id.StallSearchBtn);

        return view;
    }

    private class SearchData extends AsyncTask<String,String,String> {
        private Context mContext;
        private ProgressDialog pLoading;
        public SearchData(Context context){
            this.mContext = context;
        }
        @Override
        protected void onPreExecute(){
            super.onPreExecute();

        }
        @Override
        protected String doInBackground(String... strings) {
            String xhrRes = "";

            return xhrRes;
        }
        @Override
        protected void onPostExecute(String res){
            ResponseData = res;
        }
    }

}
