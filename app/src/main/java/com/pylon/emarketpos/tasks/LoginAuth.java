package com.pylon.emarketpos.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;
import com.pylon.emarketpos.R;
import com.pylon.emarketpos.controllers.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class LoginAuth extends AsyncTask<String,String,String> {
    private Context mContext;
    private Fragment mFrag;
    private URL url;
    private HttpURLConnection conn;
    private ProgressDialog pLoading;
    DatabaseHelper dbHelper;
    public LoginAuth(Context context, Fragment frag){
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
        String[] xhrRes1 = new String[2];
        String ip_host = "http://" + getIp();
        ip_host = ip_host + "/login";
        try{
            url = new URL(ip_host);
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
                xhrRes1[0] = "";
                xhrRes1[1] = "unsuccessful";
                xhrRes = Arrays.toString(xhrRes1);
            }
        }catch(Exception e){
            xhrRes1[0] = "";
            xhrRes1[1] = "exception";
            xhrRes = Arrays.toString(xhrRes1);
        }finally{
            conn.disconnect();
        }
        return xhrRes;
    }
    @Override
    protected void onPostExecute(String res){
        String[] test = getResponse(res);
        dbHelper = new DatabaseHelper(mContext);
        if(test[1].equalsIgnoreCase("NoUsername")){
            Toast.makeText(mContext,"Username not registered.",Toast.LENGTH_SHORT).show();
        }else if(test[1].equalsIgnoreCase("PassInc")){
            Toast.makeText(mContext,"Password mismatched.",Toast.LENGTH_SHORT).show();
        }else if(test[1].equalsIgnoreCase("exception") || test[1].equalsIgnoreCase("unsuccessful")){
            Toast.makeText(mContext,"Could not establish connection to the server.",Toast.LENGTH_SHORT).show();
        }else{
            boolean response = dbHelper.insertData(test[1], test[0]);
            if(response){
                DeviceUser devUser = new DeviceUser();
                Bundle user = new Bundle();
                user.putString("DevUser", test[1]);
                devUser.setArguments(user);
                mFrag.getActivity().getSupportFragmentManager().beginTransaction().add(R.id.devuser_con, devUser).commit();
                mFrag.getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in,R.anim.fade_out).replace(R.id.fragment_container,new MainApp(),"MainApp").commit();
            }else{
                Toast.makeText(mContext,"An error occured",Toast.LENGTH_SHORT).show();
            }
        }
        pLoading.dismiss();
    }
    private String getIp(){
        DatabaseHelper dbHelp = new DatabaseHelper(mFrag.getActivity());
        Cursor CurIP = dbHelp.selectIP();
        StringBuilder StrBf = new StringBuilder();
        while(CurIP.moveToNext()) {
            StrBf.append(CurIP.getString(0));
        }
        return StrBf.toString();
    }
    private String[] getResponse(CharSequence req){
        String[] res = new String[2];
        try{
            JSONArray data = new JSONObject(req.toString()).getJSONArray("USER");
            JSONObject xobj = data.getJSONObject(0);
            res[0] = xobj.getString("ID");
            res[1] = xobj.getString("fullname");
        } catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }
}