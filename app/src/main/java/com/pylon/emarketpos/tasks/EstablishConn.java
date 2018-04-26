package com.pylon.emarketpos.tasks;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.pylon.emarketpos.R;
import com.pylon.emarketpos.controllers.DeviceUser;
import com.pylon.emarketpos.controllers.LoginFrag;
import com.pylon.emarketpos.controllers.MainApp;
import com.pylon.emarketpos.controllers.NoConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class EstablishConn extends AsyncTask<Void,String,String> {
    private DatabaseHelper dbHelper;
    private Bundle instance;
    private Context mContext;
    private Fragment mFrag;
    private URL url;
    private HttpURLConnection conn;

    public EstablishConn(Context context, Bundle bundle, Fragment fragment){
        this.instance = bundle;
        this.mContext = context;
        this.mFrag = fragment;
    }
    @Override
    protected String doInBackground(Void... voids) {
        String xhrRes;
        try{
            //url
            url = new URL("http://192.168.143.24/connection-test");
            //req
            conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(20000);
            conn.connect();
            //res
            int resCode = conn.getResponseCode();
            if(resCode == 200){
                InputStream input = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder StrRes = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null){
                    StrRes.append(line);
                }
                xhrRes = StrRes.toString();
            }else{
                xhrRes = "unsuccessful";
            }

        }catch(MalformedURLException urlEx){
            xhrRes = "exception";
        }catch(IOException ioEx){
            xhrRes = "exception";
        }
        return xhrRes;
    }
    @Override
    public void onPostExecute(String res){
        if(res.equalsIgnoreCase("unsuccessful") || res.equalsIgnoreCase("exception") || res.equalsIgnoreCase("dbError") || res.equalsIgnoreCase("OOPs something went wrong")){
            mFrag.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NoConnection(),"LoginForm").commit();
        }else if(res.equalsIgnoreCase("ok")){
            dbHelper = new DatabaseHelper(mContext);
            Cursor curs = dbHelper.getAllData();
            if(curs.getCount() == 0){
                if(mFrag.getActivity().findViewById(R.id.fragment_container) != null){
                    if(instance != null){
                        return;
                    }
                    mFrag.getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in,0).replace(R.id.fragment_container, new LoginFrag(),"LoginForm").commit();
                }
            }else{
                StringBuilder buffer = new StringBuilder();
                while(curs.moveToNext()){
                    buffer.append(curs.getString(0));
                }
                DeviceUser devUser = new DeviceUser();
                Bundle x = new Bundle();
                x.putString("Account",buffer.toString());
                devUser.setArguments(x);
                mFrag.getActivity().getSupportFragmentManager().beginTransaction().add(R.id.user_container, devUser).commit();
                mFrag.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainApp(), "MainApp").commit();
            }
        }
    }
}