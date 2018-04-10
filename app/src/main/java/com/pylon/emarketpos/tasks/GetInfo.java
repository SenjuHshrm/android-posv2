package com.pylon.emarketpos.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Created by Admin on 4/10/2018.
 */

public class GetInfo extends AsyncTask<String,String,String> {
    private Context mContext;
    private Fragment mFrag;
    private String type;
    public GetInfo(Context context, String info,@Nullable Fragment fragment){
        this.mContext = context;
        this.mFrag = fragment;
        this.type = info;
    }
    @Override
    protected void onPreExecute(){
        super.onPreExecute();
    }
    @Override
    protected String doInBackground(String... strings) {
        return null;
    }
    @Override
    protected void onPostExecute(String res){

    }
}
