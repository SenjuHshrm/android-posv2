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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.pylon.emarketpos.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StallSearchForm extends Fragment {
    private EditText inputReq;
    private Button searchBtn;
    private ListView StallList;

    public StallSearchForm() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stall_search_form, container, false);
        inputReq = (EditText) view.findViewById(R.id.StallInputData);
        searchBtn = (Button) view.findViewById(R.id.StallSearchBtn);
        StallList = (ListView) view.findViewById(R.id.StallList);
        searchBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                final String reqData = inputReq.getText().toString();
                new SearchData(getContext()).execute(reqData);
            }
        });
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
                String url = "http://192.168.143.24/getStallInfo.inc.php?info=" + encodedQuery;
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
        @Override
        protected void onPostExecute(String res){
            if(res.equalsIgnoreCase("conErr") || res.equalsIgnoreCase("exception")){
                Toast.makeText(mContext,"There was an error connecting to server.",Toast.LENGTH_LONG).show();
            }else{
                ConstructList(res);
            }
            pLoading.dismiss();
        }
        private void ConstructList(CharSequence jsonString){
            List<Map<String,String>> data = new ArrayList<Map<String,String>>();
            try{
                JSONArray StallList = new JSONObject(jsonString.toString()).getJSONArray("STALL_RES");
                for(int i = 0; i < StallList.length(); i++){
                    JSONObject post = StallList.getJSONObject(i);
                    Map<String,String> datum = new HashMap<String,String>(3);
                    datum.put("StallNum",post.getString("Stall No."));
                    datum.put("Tenant",post.getString("Name"));
                    datum.put("Business",post.getString("Business"));
                    data.add(datum);
                }
            }catch(JSONException JSONEx){
                Toast.makeText(mContext,"There was an error parsing the data.",Toast.LENGTH_LONG).show();
            }
            SimpleAdapter adapter = new SimpleAdapter(mContext,data,R.layout.support_simple_spinner_dropdown_item,new String[]{"StallNum","Tenant","Business"},new int[]{R.id.text2,R.id.text2,R.id.text2});
            StallList.setAdapter(adapter);
        }

    }

}
