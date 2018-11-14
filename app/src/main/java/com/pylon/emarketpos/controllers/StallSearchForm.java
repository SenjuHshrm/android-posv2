package com.pylon.emarketpos.controllers;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StallSearchForm extends Fragment implements OnClickListener, OnItemClickListener, TextWatcher{
    private EditText inputReq;
    private Button searchBtn;
    private ListView StallListView;
    private InputMethodManager imm;
    private String TXT_SEARCH;

    public static StallSearchForm newInstance(String searched){
        StallSearchForm ssf = new StallSearchForm();
        Bundle args = new Bundle();
        args.putString("SEARCH_DATA", searched);
        ssf.setArguments(args);
        return ssf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stall_search_form, container, false);
        if(getArguments() != null){
            TXT_SEARCH = getArguments().getString("SEARCH_DATA");
            if(!TXT_SEARCH.isEmpty()){
                new SearchData(getContext()).execute(TXT_SEARCH);
            }
        }
        inputReq = (EditText) view.findViewById(R.id.StallInputData);
        inputReq.setText(TXT_SEARCH);
        inputReq.addTextChangedListener(this);
        searchBtn = (Button) view.findViewById(R.id.StallSearchBtn);
        StallListView = (ListView) view.findViewById(R.id.StallList);
        searchBtn.setOnClickListener(this);
        StallListView.setOnItemClickListener(this);
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        return view;
    }

    @Override
    public void onClick(View view) {
        final String reqData = inputReq.getText().toString();
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        if(reqData.equals("")){
            Toast.makeText(getActivity(),"Text field empty.",Toast.LENGTH_SHORT).show();
        }else{
            new SearchData(getContext()).execute(reqData);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        EditText inputText = (EditText) getActivity().findViewById(R.id.StallInputData);
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
        x.putString("DATA_SEARCH", inputText.getText().toString());
        stallPrintForm.setArguments(x);
        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left).replace(R.id.fragment_container,stallPrintForm,"StallPrint").commit();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(charSequence.length() == 0){
            StallListView.setAdapter(null);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

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

            try{
                ip_host = ip_host + "/get-info/stall/" + strings[0];
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
            SimpleAdapter adapter = new SimpleAdapter(mContext,data,R.layout.layout_list_view_stall,new String[]{"StallNum","Tenant","Business","CustomerID"},new int[]{R.id.List_StallNum,R.id.List_Name,R.id.List_Business,R.id.List_CustomerID_S}){
                @Override
                public View getView(int position, View convertView, ViewGroup parent){

                    View view = super.getView(position, convertView, parent);
                    if(position % 2 == 0){
                        view.setBackgroundColor(Color.parseColor("#0085b7"));
                    }
                    return view;
                }
            };
            StallListView.setAdapter(adapter);
        }
        private String getIp(){
            DatabaseHelper dbHelp = new DatabaseHelper(getActivity());
            return dbHelp.selectIP();
        }

    }

}
