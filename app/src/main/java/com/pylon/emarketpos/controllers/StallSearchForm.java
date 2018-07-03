package com.pylon.emarketpos.controllers;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.pylon.emarketpos.R;
import com.pylon.emarketpos.tasks.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StallSearchForm extends Fragment implements OnClickListener, OnItemClickListener{
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
        searchBtn.setOnClickListener(this);
        StallListView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        final String reqData = inputReq.getText().toString();
        if(reqData.equals("")){
            Toast.makeText(getActivity(),"Text field empty.",Toast.LENGTH_SHORT).show();
        }else{
            new SearchData(getContext()).execute(reqData);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TextView StallNumber = (TextView) view.findViewById(R.id.List_StallNum);
        TextView OwnerName = (TextView) view.findViewById(R.id.List_Name);
        TextView BusinessType = (TextView) view.findViewById(R.id.List_Business);
        TextView CustID = (TextView) view.findViewById(R.id.List_CustomerID_S);
        StallPrintForm stallPrintForm = new StallPrintForm();
        Bundle x = new Bundle();
        x.putString("StallNumber",StallNumber.getText().toString());
        x.putString("OwnerName",OwnerName.getText().toString());
        x.putString("BusinessType",BusinessType.getText().toString());
        x.putString("CustomerID",CustID.getText().toString());
        stallPrintForm.setArguments(x);
        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left).replace(R.id.fragment_container,stallPrintForm,"StallPrint").commit();
    }

    private class SearchData extends AsyncTask<String,String,String> {
        private Context mContext;
        private ProgressDialog pLoading;
        private URL url;
        private HttpURLConnection conn;
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
            String ip_host = "http://" + getIp();
            ip_host = ip_host + "/get-info/stall/" + strings[0];
            try{
                url = new URL(ip_host);
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.connect();
                int response = conn.getResponseCode();
                if(response == HttpURLConnection.HTTP_OK){
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder StrRes = new StringBuilder();
                    String line;
                    while((line = reader.readLine()) != null){
                        StrRes.append(line);
                    }
                    xhrRes = StrRes.toString();
                }else{
                    xhrRes = "conErr";
                }
            }catch(Exception ioEx){
                ioEx.printStackTrace();
                xhrRes = "exception";
            }
            return xhrRes;
        }
        @Override
        protected void onPostExecute(String res){
            if(res.equalsIgnoreCase("conErr") || res.equalsIgnoreCase("exception")){
                Toast.makeText(mContext,"There was an error connecting to server.",Toast.LENGTH_SHORT).show();
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
                    datum.put("CustomerID", Integer.toString(post.getInt("ID")));
                    data.add(datum);
                }
            }catch(JSONException JSONEx){
                Toast.makeText(mContext,"Data not found.",Toast.LENGTH_SHORT).show();
            }
            SimpleAdapter adapter = new SimpleAdapter(mContext,data,R.layout.layout_list_view_stall,new String[]{"StallNum","Tenant","Business","CustomerID"},new int[]{R.id.List_StallNum,R.id.List_Name,R.id.List_Business,R.id.List_CustomerID_S});
            StallListView.setAdapter(adapter);
        }
        private String getIp(){
            DatabaseHelper dbHelp = new DatabaseHelper(getActivity());
            return dbHelp.selectIP();
        }

    }

}
