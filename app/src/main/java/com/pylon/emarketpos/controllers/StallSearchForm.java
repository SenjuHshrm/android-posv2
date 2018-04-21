package com.pylon.emarketpos.controllers;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.test.suitebuilder.annotation.Suppress;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.pylon.emarketpos.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
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
    private ListView StallListView;

    public StallSearchForm() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stall_search_form, container, false);
        inputReq = (EditText) view.findViewById(R.id.StallInputData);
        searchBtn = (Button) view.findViewById(R.id.StallSearchBtn);
        StallListView = (ListView) view.findViewById(R.id.StallList);
        searchBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                final String reqData = inputReq.getText().toString();
                if(reqData.equals("")){
                    Toast.makeText(getActivity(),"Text field empty.",Toast.LENGTH_LONG).show();
                }else{
                    new SearchData(getContext()).execute(reqData);
                }
            }
        });
        StallListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView StallNumber = (TextView) view.findViewById(R.id.List_StallNum);
                TextView OwnerName = (TextView) view.findViewById(R.id.List_Name);
                TextView BusinessType = (TextView) view.findViewById(R.id.List_Business);
                StallPrintForm stallPrintForm = new StallPrintForm();
                Bundle x = new Bundle();
                x.putString("StallNumber",StallNumber.getText().toString());
                x.putString("OwnerName",OwnerName.getText().toString());
                x.putString("BusinessType",BusinessType.getText().toString());
                stallPrintForm.setArguments(x);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,stallPrintForm,"StallPrint").commit();
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
        @SuppressWarnings("deprecation")
        protected String doInBackground(String... strings) {
            String xhrRes;
            try{
                String encodedQuery = URLEncoder.encode(strings[0],"utf-8");
                String url = "http://192.168.143.24/getStallInfo.inc.php?info=" + encodedQuery;
                StringBuilder builder = new StringBuilder();
                HttpGet httpGet = new HttpGet(url);
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters,15000);
                HttpConnectionParams.setSoTimeout(httpParameters,10000);
                HttpClient client = new DefaultHttpClient(httpParameters);
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
            SimpleAdapter adapter = new SimpleAdapter(mContext,data,R.layout.layout_list_view_stall,new String[]{"StallNum","Tenant","Business"},new int[]{R.id.List_StallNum,R.id.List_Name,R.id.List_Business});
            StallListView.setAdapter(adapter);
        }

    }

}
