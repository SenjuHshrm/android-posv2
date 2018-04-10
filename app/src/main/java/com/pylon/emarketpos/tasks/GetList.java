package com.pylon.emarketpos.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class GetList extends AsyncTask<String,Void,String> {
    private Context mContext;
    private Fragment mFrag;
    private ProgressDialog pLoading;
    public GetList(Context context, @Nullable Fragment fragment){
        this.mContext = context;
        this.mFrag = fragment;
    }
    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        pLoading = new ProgressDialog(mContext);
        pLoading.setMessage("\tGetting list...");
        pLoading.setCancelable(false);
        pLoading.show();
    }
    @Override
    protected String doInBackground(String... strings) {
        String url = "http://192.168.143.24/getInfo.inc.php?type=" + strings[0];
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        try{
            HttpResponse response = client.execute(httpGet);
            StatusLine statLine = response.getStatusLine();
            int statCode = statLine.getStatusCode();
            if(statCode == 200){
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while((line = reader.readLine()) != null){
                    builder.append(line);
                }
            }
        }catch(IOException ioEx){
            ioEx.printStackTrace();
        }
        return builder.toString();
    }
    @Override
    protected void onPostExecute(String res){
        pLoading.dismiss();
        Toast.makeText(mContext,"AsyncTask working",Toast.LENGTH_LONG).show();

    }
}
