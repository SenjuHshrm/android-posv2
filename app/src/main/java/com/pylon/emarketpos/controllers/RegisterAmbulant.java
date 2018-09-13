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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pylon.emarketpos.R;
import com.pylon.emarketpos.tasks.DatabaseHelper;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterAmbulant extends Fragment implements OnClickListener{
    private EditText fName, mName, lName, businessType, locName, locNum;
    private Button regAmb;
    public RegisterAmbulant() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_ambulant, container, false);
        fName = (EditText) view.findViewById(R.id.firstNameIn);
        mName = (EditText) view.findViewById(R.id.middleNameIn);
        lName = (EditText) view.findViewById(R.id.lastNameIn);
        businessType = (EditText) view.findViewById(R.id.businessIn);
        locName = (EditText) view.findViewById(R.id.locationIn);
        locNum = (EditText) view.findViewById(R.id.locationNumIn);
        regAmb = (Button) view.findViewById(R.id.btnRegAmb);
        regAmb.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        final String firstName = fName.getText().toString();
        final String middleName = mName.getText().toString();
        final String lastName = lName.getText().toString();
        final String business = businessType.getText().toString();
        final String locationName = locName.getText().toString();
        final String locationNumber = locNum.getText().toString();
        if(firstName.isEmpty() ||
           lastName.isEmpty() ||
           locationName.isEmpty() ||
           locationNumber.isEmpty()){
            Toast.makeText(getContext(), "Input all required fields.", Toast.LENGTH_SHORT).show();
        } else {
            new RegAmbulant(getContext(), this).execute(firstName, middleName, lastName, business, locationName, locationNumber);
        }
    }

    private class RegAmbulant extends AsyncTask<String, Void, String>{
        Context mContext;
        Fragment mFrag;
        private ProgressDialog pLoading;
        private URL url;
        private HttpURLConnection conn;
        private JSONObject ambObj;
        private OutputStream os;
        private InputStream is;
        DatabaseHelper dbHelp;
        public RegAmbulant(Context context, Fragment fragment){
            this.mContext = context;
            this.mFrag = fragment;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pLoading = new ProgressDialog(mContext);
            pLoading.setMessage("\tRegistering, please wait");
            pLoading.setCancelable(false);
            pLoading.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String xhrRes;
            String ip_host = "http://" + getIp() + "/register/ambulant/";
            try{
                url = new URL(ip_host);
                ambObj = new JSONObject()
                        .put("FirstName", params[0])
                        .put("MiddleName", params[1])
                        .put("LastName", params[2])
                        .put("BusinessType", params[3])
                        .put("Location", params[4])
                        .put("LocationNumber", params[5]);
                String PostData = ambObj.toString();
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setFixedLengthStreamingMode(PostData.getBytes().length);
                conn.setRequestProperty("Content-Type","application/json; charset=utf-8");
                conn.setRequestProperty("X-Requested-With","XMLHttpRequest");
                conn.connect();
                os = new BufferedOutputStream(conn.getOutputStream());
                os.write(PostData.getBytes());
                os.flush();
                os.close();
                int response = conn.getResponseCode();
                if(response == HttpURLConnection.HTTP_OK){
                    is = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder strBuild = new StringBuilder();
                    String line;
                    while((line = reader.readLine()) != null){
                        strBuild.append(line);
                    }
                    xhrRes = strBuild.toString();
                } else {
                    xhrRes = "connErr404";
                }
            } catch (Exception e){
                xhrRes = "connErr";
            } finally{
                conn.disconnect();
            }
            return xhrRes;
        }

        @Override
        protected void onPostExecute(String res){
            pLoading.dismiss();
            if(res.equalsIgnoreCase("true")){
                clearText();
                Toast.makeText(mContext,"Successfully registered",Toast.LENGTH_SHORT).show();
            } else if(res.equalsIgnoreCase("false")) {
                Toast.makeText(mContext,"Registration was unsuccessful",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext,"An error occured while connecting to server.",Toast.LENGTH_SHORT).show();
            }
        }

        private String getIp() {
            dbHelp = new DatabaseHelper(mFrag.getActivity());
            return dbHelp.selectIP();
        }
    }
    public void clearText() {
        fName.setText("");
        mName.setText("");
        lName.setText("");
        businessType.setText("");
        locName.setText("");
        locNum.setText("");
    }
}
