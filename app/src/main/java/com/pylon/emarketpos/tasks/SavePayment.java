package com.pylon.emarketpos.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class SavePayment extends AsyncTask<String,String,String> {
    private ProgressDialog pLoading;
    private Context mContext;
    private String[] RecInfo = new String[10];
    private String TrnsType;
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
    protected String doInBackground(String[] param) {
        String xhrRes = "MXXXXXX0";
        TrnsType = param[0];
        for(int i = 0; i < param.length; i++){
            RecInfo[i] = param[i];
        }
        return xhrRes;
    }
    @Override
    public void onPostExecute(String res){
        pLoading.dismiss();
        new PrintReceipt(mContext).PrintReceiptPrep(TrnsType,RecInfo,res);
    }
}
