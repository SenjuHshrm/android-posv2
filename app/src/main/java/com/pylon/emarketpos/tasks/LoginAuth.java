package com.pylon.emarketpos.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;
import com.pylon.emarketpos.R;
import com.pylon.emarketpos.controllers.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginAuth extends AsyncTask<String,String,String> {
    private Context mContext;
    private Fragment mFrag;
    private URL url;
    private HttpURLConnection conn;
    private ProgressDialog pLoading;
    DatabaseHelper dbHelper;
    public LoginAuth(Context context,@Nullable Fragment frag){
        this.mContext = context;
        this.mFrag = frag;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        pLoading = new ProgressDialog(mContext);
        pLoading.setMessage("\tLogging in, please wait");
        pLoading.setCancelable(false);
        pLoading.show();
    }
    @Override
    protected String doInBackground(String... params) {
        String xhrRes;
        try{
            url = new URL("http://192.168.143.24/login");
            conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("username", params[0])
                    .appendQueryParameter("password" ,params[1]);
            String query= builder.build().getEncodedQuery();
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();
            conn.connect();
            int response = conn.getResponseCode();
            if(response == HttpURLConnection.HTTP_OK){
                InputStream input = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder StrRes = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null){
                    StrRes.append(line);
                    Log.d("Response Data",line);
                }
                xhrRes = StrRes.toString();
            }else{
                xhrRes = "unsuccessful";
            }
        }catch(Exception e){
            xhrRes = "exception";
        }finally{
            conn.disconnect();
        }
        return xhrRes;
    }
    @Override
    protected void onPostExecute(String res){
        dbHelper = new DatabaseHelper(mContext);
        boolean response = dbHelper.insertData(res);
        if(res.equalsIgnoreCase("NoUsername")){
            Toast.makeText(mContext,"Username not registered.",Toast.LENGTH_LONG).show();
        }else if(res.equalsIgnoreCase("PassInc")){
            Toast.makeText(mContext,"Password mismatched.",Toast.LENGTH_LONG).show();
        }else if(res.equalsIgnoreCase("exception") || res.equalsIgnoreCase("unsuccessful")){
            Toast.makeText(mContext,"Could not establish connection to the server.",Toast.LENGTH_LONG).show();
        }else{
            if(response == true){
                MainApp mApp = new MainApp();
                DeviceUser devUser = new DeviceUser();
                Bundle bundle = new Bundle();
                bundle.putString("Account",res);
                FragmentManager frMn1 = mFrag.getActivity().getSupportFragmentManager();
                FragmentTransaction trns1 = frMn1.beginTransaction();
                devUser.setArguments(bundle);
                trns1.replace(R.id.user_container,devUser);
                trns1.commit();
                mFrag.getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in,R.anim.fade_out).replace(R.id.fragment_container,mApp,"MainApp").commit();
            }else{
                Toast.makeText(mContext,"An error occured",Toast.LENGTH_LONG).show();
            }
        }
        pLoading.dismiss();
    }
}