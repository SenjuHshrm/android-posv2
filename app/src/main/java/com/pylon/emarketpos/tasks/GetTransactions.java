package com.pylon.emarketpos.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GetTransactions extends AsyncTask<String, Void, String> {
    private URL url;
    private HttpURLConnection conn;
    private ProgressDialog pLoading;
    private JSONObject requestObj;
    private OutputStream os;
    private InputStream is;
    private String ReqData, DEV_USER;
    Context mContext;
    public GetTransactions(Context ctx){
        this.mContext = ctx;
    }
    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        pLoading = new ProgressDialog(mContext);
        pLoading.setMessage("\tFetching transactions.");
        pLoading.setCancelable(false);
        pLoading.show();
    }
    @Override
    protected String doInBackground(String... params) {
        String xhrRes = "";
        DEV_USER = params[0];
        String ipAdd = "http://" + getIP() + "/get-transaction/";
        try{
            url = new URL(ipAdd);
            requestObj = new JSONObject()
                    .put("user", params[0])
                    .put("date", params[1]);
            ReqData = requestObj.toString();
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setFixedLengthStreamingMode(ReqData.getBytes().length);
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            conn.connect();
            os = new BufferedOutputStream(conn.getOutputStream());
            os.write(ReqData.getBytes());
            os.flush();
            os.close();
            int response = conn.getResponseCode();
            if(response == HttpURLConnection.HTTP_OK){
                is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder str = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null){
                    str.append(line);
                }
                xhrRes = str.toString();

            } else {
                xhrRes = "connErr";
            }
        } catch (Exception e){
            xhrRes = "connErr";
        } finally {
            conn.disconnect();
        }
        return xhrRes;
    }
    @Override
    protected void onPostExecute(String res){
        pLoading.dismiss();
        if(res.equalsIgnoreCase("connErr")) {
            Toast.makeText(mContext, "There was an error connecting to server.", Toast.LENGTH_SHORT).show();
        } else {
            try{
                JSONArray jsonData = new JSONObject(res).getJSONArray("LIST_TRNS");
                ArrayList<String> data = new ArrayList<>();
                double total = 0.00;
                for(int i = 0; i < jsonData.length(); i++){
                    JSONObject post = jsonData.getJSONObject(i);
                    data.add(post.getString("Receipt") + "\t\tP " + formatCurrency(post.getString("Payments")));
                    total += Double.parseDouble(post.getString("Payments"));
                }
                new PrintTransactions(mContext).PrintReceipt(DEV_USER, data, formatCurrency(String.valueOf(total)));
            } catch (Exception e){

            }
        }
    }

    private String getIP(){
        DatabaseHelper dbHelp = new DatabaseHelper(mContext);
        return dbHelp.selectIP();
    }

    private String formatCurrency(String amt){
//        if(!amt.endsWith(".00")){
//            return amt + ".00";
//        } else if (amt.endsWith("[.0-9]$")){
//            return amt + 0;
//        }
//        return amt;
        String res = "";
        double d = Double.parseDouble(amt);
        String txt = Double.toString(Math.abs(d));
        int intPl = txt.indexOf('.');
        int decPl = txt.length() - intPl - 1;

        if (amt.contains(".")){
            if (decPl == 2){
                res =  amt;
            } else if (decPl == 1) {
                res =  amt + "0";
            } else if (decPl == 0) {
                res =  amt + ".00";
            }
        } else {
            res = amt + ".00";
        }
        return res;
    }
}
