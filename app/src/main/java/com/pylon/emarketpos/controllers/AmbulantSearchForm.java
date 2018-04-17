package com.pylon.emarketpos.controllers;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.pylon.emarketpos.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;

public class AmbulantSearchForm extends Fragment{
    private EditText inputReq;
    private Button searchBtn;
    private ListView AmbListView;
    public AmbulantSearchForm() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_ambulant_search_form, container, false);
        inputReq = (EditText) view.findViewById(R.id.AmbInputData);
        searchBtn = (Button) view.findViewById(R.id.AmbSearchBtn);
        AmbListView = (ListView) view.findViewById(R.id.AmbulantList);
        searchBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                final String reqData = inputReq.getText().toString();
            }
        });
        AmbListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
        return view;
    }

    private class SearchData extends AsyncTask<String,String,String> {
        private Context mContext;
        private ProgressDialog pLoading;
        private SearchData(Context context){
            this.mContext = context;
        }
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pLoading = new ProgressDialog(mContext);
            pLoading.setMessage("\tGetting information...");
            pLoading.setCancelable(false);
            pLoading.show();
        }
        @Override
        protected String doInBackground(String... strings) {
            String xhrRes;
            try{
                String encodedQuery = URLEncoder.encode(strings[0],"utf-8");
                String url = "http://192.168.143.24/getAmbulantInfo.inc.php?info=" + encodedQuery;
                StringBuilder builder = new StringBuilder();
                HttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                HttpResponse response = client.execute(httpGet);
                StatusLine statLine = response.getStatusLine();
                int statCode = statLine.getStatusCode();
                if(statCode == 200){
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                    String line;
                    while((line = reader.readLine()) != null){
                        builder.append(line);
                    }
                    xhrRes = builder.toString();
                }else{
                    xhrRes = "conErr";
                }
            }catch(IOException ioEx){
                ioEx.printStackTrace();
                xhrRes = "exception";
            }
            return xhrRes;
        }
    }

}
