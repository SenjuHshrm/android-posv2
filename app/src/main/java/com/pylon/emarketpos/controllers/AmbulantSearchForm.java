package com.pylon.emarketpos.controllers;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AmbulantSearchForm extends Fragment implements OnClickListener, OnItemClickListener{
    private EditText inputReq;
    private Button searchBtn;
    private ListView AmbListView;
    private InputMethodManager imm;
    private String TXT_SEARCH;

    public static AmbulantSearchForm newInstance(String searched){
        AmbulantSearchForm asf = new AmbulantSearchForm();
        Bundle args = new Bundle();
        args.putString("SEARCH_DATA", searched);
        asf.setArguments(args);
        return asf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_ambulant_search_form, container, false);
        if(getArguments() != null){
            TXT_SEARCH = getArguments().getString("SEARCH_DATA");
            if(!TXT_SEARCH.isEmpty()){
                new SearchData(getContext()).execute(TXT_SEARCH);
            }
        }
        inputReq = (EditText) view.findViewById(R.id.AmbInputData);
        inputReq.setText(TXT_SEARCH);
        searchBtn = (Button) view.findViewById(R.id.AmbSearchBtn);
        AmbListView = (ListView) view.findViewById(R.id.AmbulantList);
        searchBtn.setOnClickListener(this);
        AmbListView.setOnItemClickListener(this);
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
        EditText inputText = (EditText) getActivity().findViewById(R.id.AmbInputData);
        TextView AmbOwner = (TextView) view.findViewById(R.id.List_AmbOwner);
        TextView AmbBusiness = (TextView) view.findViewById(R.id.List_AmbBusiness);
        TextView ID = (TextView) view.findViewById(R.id.List_CustomerID_A);
        AmbulantPrintForm ambPrintForm = new AmbulantPrintForm();
        Bundle x = new Bundle();
        x.putString("AmbOwner",AmbOwner.getText().toString());
        x.putString("AmbBusiness",AmbBusiness.getText().toString());
        x.putString("CustomerID", ID.getText().toString());
        x.putString("DATA_SEARCH", inputText.getText().toString());
        ambPrintForm.setArguments(x);
        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left).replace(R.id.fragment_container,ambPrintForm,"AmbulantPrint").commit();
    }

    private class SearchData extends AsyncTask<String,String,String> {
        private Context mContext;
        private ProgressDialog pLoading;
        private URL url;
        private HttpURLConnection conn;
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
        protected String doInBackground(String... param) {
            String xhrRes;
            String reqURL;
            String ip_host = "http://" + getIp();
            ip_host = ip_host + "/get-info/ambulant/";
            try{
                if(param[0].contains(" ")){
                    String[] params = param[0].split(" ");
                    reqURL = ip_host + params[0] + "%20" + params[1];
                }else{
                    reqURL = ip_host + param[0];
                }
                url  = new URL(reqURL);
                conn = (HttpURLConnection) url.openConnection();
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
            }finally {
                conn.disconnect();
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
                JSONArray AmbList = new JSONObject(jsonString.toString()).getJSONArray("AMB_RES");
                for(int i = 0; i < AmbList.length(); i++){
                    JSONObject post = AmbList.getJSONObject(i);
                    Map<String,String> datum = new HashMap<String,String>(2);
                    datum.put("OwnerName", post.getString("Name"));
                    datum.put("BusinessNat",post.getString("Location"));
                    datum.put("ID", post.getString("ID"));
                    data.add(datum);
                }
            }catch(JSONException JSONEx){
                Toast.makeText(mContext,"Data not found.",Toast.LENGTH_SHORT).show();
            }
            SimpleAdapter adapter = new SimpleAdapter(mContext,data,R.layout.layout_list_view_ambulant,new String[]{"OwnerName","BusinessNat", "ID"},new int[]{R.id.List_AmbOwner,R.id.List_AmbBusiness, R.id.List_CustomerID_A}){
                @Override
                public View getView(int position, View convertView, ViewGroup parent){

                    View view = super.getView(position, convertView, parent);
                    if(position % 2 == 0){
                        view.setBackgroundColor(Color.parseColor("#0085b7"));
                    }
                    return view;
                }
            };
            AmbListView.setAdapter(adapter);
        }
        private String getIp(){
            DatabaseHelper dbHelp = new DatabaseHelper(getActivity());
            return dbHelp.selectIP();
        }
    }

}
