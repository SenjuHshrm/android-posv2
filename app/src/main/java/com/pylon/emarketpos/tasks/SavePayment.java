package com.pylon.emarketpos.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.pylon.emarketpos.R;
import com.pylon.emarketpos.controllers.AmbulantSearchForm;
import com.pylon.emarketpos.controllers.StallSearchForm;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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
        TrnsType = param[0];
        for(int i = 0; i < param.length; i++){
            RecInfo[i] = param[i];
        }
        try{
            //url constructor
            url = new URL("http://192.168.143.24/SavePayment.inc.php");
            //json constructor
            jsonObj = new JSONObject();
            switch(TrnsType){
                case "stall":
                    jsonObj.put("Type",param[0]);
                    jsonObj.put("StNum",param[1]);
                    jsonObj.put("OwnerName",param[2]);
                    jsonObj.put("Business",param[3]);
                    jsonObj.put("Payment",param[4]);
                    jsonObj.put("Collector",param[5]);
                    break;
                case "ambulant":
                    jsonObj.put("Type",param[0]);
                    jsonObj.put("OwnerName",param[1]);
                    jsonObj.put("Business",param[2]);
                    jsonObj.put("Payment",param[3]);
                    jsonObj.put("Collector",param[4]);
                    break;
            }
            RequestData = jsonObj.toString();
            //connection constructor
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
            //get response data(transaction number)
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
        }catch(MalformedURLException mURLEx){
            xhrRes = "conErr";
        }catch(JSONException jsonEx){
            xhrRes = "conErr";
        }catch(IOException ioEx){
            xhrRes = "conErr";
        }
        return xhrRes;
    }
    @Override
    public void onPostExecute(String res){
        pLoading.dismiss();
        if(res.equalsIgnoreCase("false") ||
                res.equalsIgnoreCase("conErr")){
            Toast.makeText(mContext,"There was an error connecting to server.",Toast.LENGTH_LONG).show();
        } else {
            new PrintReceipt(mContext).PrintReceiptPrep(TrnsType,RecInfo,res);
            switch(TrnsType){
                case "stall":
                    mFrag.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new StallSearchForm(), "StallSearch").commit();
                    break;
                case "ambulant":
                    mFrag.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AmbulantSearchForm(), "AmbulantSearch").commit();
                    break;
            }
        }
    }
}
