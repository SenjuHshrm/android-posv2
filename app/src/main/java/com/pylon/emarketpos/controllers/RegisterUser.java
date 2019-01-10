package com.pylon.emarketpos.controllers;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import java.net.URI;
import java.net.URL;

public class RegisterUser extends Fragment implements View.OnClickListener{
    private EditText fname, mname, lname, address, cont, username, password;
    public RegisterUser() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_user, container, false);
        TextView pageStat = (TextView) getActivity().findViewById(R.id.pageStat);
        pageStat.setText(getResources().getString(R.string.lblRegUser));
        Button RegBtn = (Button) view.findViewById(R.id.btnReg);
        fname = (EditText) view.findViewById(R.id.fname);
        mname = (EditText) view.findViewById(R.id.mname);
        lname = (EditText) view.findViewById(R.id.lname);
        address = (EditText) view.findViewById(R.id.addr);
        cont = (EditText) view.findViewById(R.id.contact);
        username = (EditText) view.findViewById(R.id.usrnm);
        password = (EditText) view.findViewById(R.id.pswrd);
        RegBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        final String ffname = fname.getText().toString();
        final String fmname = mname.getText().toString();
        final String flname = lname.getText().toString();
        final String faddr = address.getText().toString();
        final String fcont = cont.getText().toString();
        final String fuser = username.getText().toString();
        final String fpass = password.getText().toString();
        if(ffname.isEmpty() ||
           fmname.isEmpty() ||
           flname.isEmpty() ||
           faddr.isEmpty() ||
           fcont.isEmpty() ||
           fuser.isEmpty() ||
           fpass.isEmpty()){
            Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
        } else {
            new SaveUserInfo(getContext(), this).execute(ffname,fmname,flname,faddr,fcont,fuser,fpass);
        }

    }

    private class SaveUserInfo extends AsyncTask<String,Void,String> {
        private Context mContext;
        private Fragment mFrag;
        private ProgressDialog pLoading;
        private URL url;
        private HttpURLConnection conn;
        private JSONObject userJson;
        private OutputStream os;
        private InputStream is;
        DatabaseHelper dbHelp;
        private SaveUserInfo(Context ctx, Fragment frag){
            this.mContext = ctx;
            this.mFrag = frag;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pLoading = new ProgressDialog(mContext);
            pLoading.setMessage("\tSaving info, please wait");
            pLoading.setCancelable(false);
            pLoading.show();
        }
        @Override
        protected String doInBackground(String... params) {
            String xhrRes;
            String ip_host = "http://" + getIp() + "/register/";
            try{
                url = new URL(ip_host);
                userJson = new JSONObject()
                    .put("fname", params[0])
                    .put("mname", params[1])
                    .put("lname", params[2])
                    .put("address", params[3])
                    .put("contact", params[4])
                    .put("username", params[5])
                    .put("password", params[6]);
                String PostData = userJson.toString();
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setFixedLengthStreamingMode(PostData.getBytes().length);
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                conn.setRequestProperty("X-Request-With", "XMLHttpRequest");
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
                }else {
                    xhrRes = "false";
                }
            } catch (Exception e) {
                xhrRes = "false";
            }
            return xhrRes;
        }
        @Override
        public void onPostExecute(String res){
            pLoading.dismiss();
            if(res.equalsIgnoreCase("true")){
                clearText();
                Toast.makeText(mContext, "Registered successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "Unable to connect to the server. Check your connection and try again.", Toast.LENGTH_SHORT).show();
            }
        }
        private String getIp() {
            dbHelp = new DatabaseHelper(mFrag.getActivity());
            return dbHelp.selectIP();
        }
    }

    public void clearText() {
        fname.setText("");
        mname.setText("");
        lname.setText("");
        address.setText("");
        cont.setText("");
        username.setText("");
        password.setText("");
    }

}
