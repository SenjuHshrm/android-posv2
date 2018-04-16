package com.pylon.emarketpos.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class SavePayment extends AsyncTask<String,String,String> {
    private ProgressDialog pLoading;
    private Context mContext;
    private String[] RecInfo;
    public SavePayment(Context context){
        this.mContext = context;
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
    protected String doInBackground(String... param) {
        String xhrRes = "MXXXXXX0";
        RecInfo = new String[5];
        RecInfo[0] = param[0];
        RecInfo[1] = param[1];
        RecInfo[2] = param[2];
        RecInfo[3] = param[3];
        return xhrRes;
    }
    @Override
    public void onPostExecute(String res){
        pLoading.dismiss();
        new PrintReceipt(mContext).PrintReceiptPrep("stall",RecInfo,res);
    }
}
