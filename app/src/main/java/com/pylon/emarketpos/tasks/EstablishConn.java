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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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
        String ip_host = "http://" + getIp();
        ip_host = ip_host + "/connection-test";
        try{
            url = new URL(ip_host);
            conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(20000);
            conn.connect();
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

        }catch(Exception e){
            xhrRes = "exception";
        }
        return xhrRes;
    }
    @Override
    public void onPostExecute(String res){
        if(res.equalsIgnoreCase("unsuccessful") || res.equalsIgnoreCase("exception") || res.equalsIgnoreCase("dbError") || res.equalsIgnoreCase("OOPs something went wrong")){
            mFrag.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NoConnection(),"NoConn").commit();
        }else if(res.equalsIgnoreCase("ok")){
            dbHelper = new DatabaseHelper(mContext);
            Cursor curs = dbHelper.getAllData();
            if(curs.getCount() == 0){
                if(mFrag.getActivity().findViewById(R.id.fragment_container) != null){
                    if(instance != null){
                        return;
                    }
                    Fragment loginFrag = new LoginFrag();
                    Bundle info = new Bundle();
                    info.putString("pageStat", mFrag.getResources().getString(R.string.lblBtnLogin));
                    loginFrag.setArguments(info);
                    mFrag.getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in,0).replace(R.id.fragment_container, loginFrag,"LoginForm").commit();
                }
            }else{
                StringBuilder buffer = new StringBuilder();
                while(curs.moveToNext()){
                    buffer.append(curs.getString(0));
                }
                DeviceUser devUser = new DeviceUser();
                Bundle user = new Bundle();
                user.putString("DevUser", buffer.toString());
                devUser.setArguments(user);
                mFrag.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.devuser_con, devUser, "MainApp").commit();
                mFrag.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainApp(), "MainApp").commit();
            }
        }
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
}