package com.pylon.emarketpos.tasks;

import android.app.ProgressDialog;
import android.content.Context;
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

public class SavePayment extends AsyncTask<String,Void,String> {
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
    private String DATA_SEARCH;
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
        DATA_SEARCH = param[1];
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
                    jsonObj.put("Payment",param[4]);
                    jsonObj.put("CollectorID",dbHelp.getID());
                    jsonObj.put("CollectorName",param[5]);
                    jsonObj.put("CustomerID",param[6]);
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
            } else {
                xhrRes = "conErr";
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
                    StallSearchForm ssf = StallSearchForm.newInstance(DATA_SEARCH);
                    mFrag.getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in,R.anim.fade_out).replace(R.id.fragment_container, ssf, "StallSearch").commit();
                    break;
                case "ambulant":
                    AmbulantSearchForm asd = AmbulantSearchForm.newInstance(DATA_SEARCH);
                    mFrag.getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in,R.anim.fade_out).replace(R.id.fragment_container, asd, "AmbulantSearch").commit();
                    break;
            }
        }
    }
    private String getIp(){
        dbHelp = new DatabaseHelper(mFrag.getActivity());
        return dbHelp.selectIP();
    }
}
