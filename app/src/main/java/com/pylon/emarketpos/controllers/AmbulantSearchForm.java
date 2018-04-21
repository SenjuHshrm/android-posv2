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
                if(reqData.equals("")){
                    Toast.makeText(getActivity(),"Text field empty.",Toast.LENGTH_LONG).show();
                }else{
                    new SearchData(getContext()).execute(reqData);
                }
            }
        });
        AmbListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView AmbOwner = (TextView) view.findViewById(R.id.List_AmbOwner);
                TextView AmbBusiness = (TextView) view.findViewById(R.id.List_AmbBusiness);
                AmbulantPrintForm ambPrintForm = new AmbulantPrintForm();
                Bundle x = new Bundle();
                x.putString("AmbOwner",AmbOwner.getText().toString());
                x.putString("AmbBusiness",AmbBusiness.getText().toString());
                ambPrintForm.setArguments(x);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,ambPrintForm,"AmbulantPrint").commit();
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
        @SuppressWarnings("deprecation")
        protected String doInBackground(String... strings) {
            String xhrRes;
            try{
                String encodedQuery = URLEncoder.encode(strings[0],"utf-8");
                String url = "http://192.168.143.24/getAmbulantInfo.inc.php?info=" + encodedQuery;
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
                JSONArray AmbList = new JSONObject(jsonString.toString()).getJSONArray("AMB_RES");
                for(int i = 0; i < AmbList.length(); i++){
                    JSONObject post = AmbList.getJSONObject(i);
                    Map<String,String> datum = new HashMap<String,String>(2);
                    datum.put("OwnerName", post.getString("Ambulant Owner"));
                    datum.put("BusinessNat",post.getString("Business"));
                    data.add(datum);
                }
            }catch(JSONException JSONEx){
                Toast.makeText(mContext,"There was an error parsing the data.",Toast.LENGTH_LONG).show();
            }
            SimpleAdapter adapter = new SimpleAdapter(mContext,data,R.layout.layout_list_view_ambulant,new String[]{"OwnerName","BusinessNat"},new int[]{R.id.List_AmbOwner,R.id.List_AmbBusiness});
            AmbListView.setAdapter(adapter);
        }
    }

}
