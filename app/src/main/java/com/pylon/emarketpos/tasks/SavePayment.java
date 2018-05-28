package com.pylon.emarketpos.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;
import com.pylon.emarketpos.R;
import com.pylon.emarketpos.controllers.AmbulantSearchForm;
import com.pylon.emarketpos.controllers.StallSearchForm;
import org.json.JSONObject;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SavePayment extends AsyncTask<String,String,String> {
    private ProgressDialog pLoading;
    private Context mContext;
    private Fragment mFrag;
    private String[] RecInfo = new String[10];
    private String TrnsType, RequestData;
    private URL url;
    private HttpURLConnection conn;
    private JSONObject jsonObj;
    private OutputStream os;
    private InputStream is;
    DatabaseHelper dbHelp;
    public SavePayment(Context context, Fragment fragment){
        this.mContext = context;
        this.mFrag = fragment;
    }
    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        pLoading = new ProgressDialog(mContext);
        pLoading.setMessage("\tSaving transaction...");
        pLoading.setCancelable(false);
        pLoading.show();
    }
    @Override
    protected String doInBackground(String[] param) {
        String xhrRes = "MXXXXXX0";
        String ip_host = "http://" + getIp();
        ip_host = ip_host + "/save-transaction";
        TrnsType = param[0];
        for(int i = 0; i < param.length; i++){
            RecInfo[i] = param[i];
        }
        try{
            dbHelp = new DatabaseHelper(mFrag.getActivity());
            url = new URL(ip_host);
            jsonObj = new JSONObject();
            switch(TrnsType){
                case "stall":
                    jsonObj.put("Payment",param[3]);
                    jsonObj.put("CollectorID",dbHelp.getID());
                    jsonObj.put("CollectorName", param[4]);
                    jsonObj.put("CustomerID",param[5]);
                    break;
                case "ambulant":
                    jsonObj.put("Payment",param[3]);
                    jsonObj.put("CollectorID",dbHelp.getID());
                    jsonObj.put("CollectorName",param[4]);
                    jsonObj.put("CustomerID",param[5]);
                    break;
            }
            RequestData = jsonObj.toString();
            conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setFixedLengthStreamingMode(RequestData.getBytes().length);
            conn.setRequestProperty("Content-Type","application/json;charset=utf-8");
            conn.setRequestProperty("X-Requested-With","XMLHttpRequest");
            conn.connect();
            os = new BufferedOutputStream(conn.getOutputStream());
            os.write(RequestData.getBytes());
            os.flush();
            int response = conn.getResponseCode();
            if(response == HttpURLConnection.HTTP_OK){
                is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder StRes = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null){
                    StRes.append(line);
                }
                xhrRes = StRes.toString();
            }
        }catch(Exception e){
            xhrRes = "conErr";
            e.printStackTrace();
        }
        return xhrRes;
    }
    @Override
    public void onPostExecute(String res){
        pLoading.dismiss();
        Log.d("Response", res);
        if(res.equalsIgnoreCase("false") ||
                res.equalsIgnoreCase("conErr")){
            Toast.makeText(mContext,"There was an error connecting to server.",Toast.LENGTH_SHORT).show();
        } else {
            new PrintReceipt(mContext).PrintReceiptPrep(TrnsType,RecInfo,res);
            switch(TrnsType){
                case "stall":
                    mFrag.getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in,R.anim.fade_out).replace(R.id.fragment_container, new StallSearchForm(), "StallSearch").commit();
                    break;
                case "ambulant":
                    mFrag.getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in,R.anim.fade_out).replace(R.id.fragment_container, new AmbulantSearchForm(), "AmbulantSearch").commit();
                    break;
            }
        }
    }
    private String getIp(){
        dbHelp = new DatabaseHelper(mFrag.getActivity());
        Cursor CurIP = dbHelp.selectIP();
        StringBuilder StrBf = new StringBuilder();
        while(CurIP.moveToNext()) {
            StrBf.append(CurIP.getString(0));
        }
        return StrBf.toString();
    }
}
