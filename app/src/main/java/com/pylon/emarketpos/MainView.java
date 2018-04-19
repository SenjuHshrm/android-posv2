package com.pylon.emarketpos;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.pylon.emarketpos.controllers.*;

public class MainView extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);
        if(findViewById(R.id.fragment_container) != null){
            if(savedInstanceState != null){
                return;
            }
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new LoginFrag(),"LoginForm").commit();
        }
    }
    @Override
    public void onBackPressed(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
        final ToolbarFrag tbFrag = new ToolbarFrag();
        final DeviceUser devUser = new DeviceUser();
        final Bundle DevUser = new Bundle();
        final Bundle bn = new Bundle();
        switch(currentFragment.getTag()){
            case "MainApp":
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("\tDo you want to log out");
                builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        bn.putString("Stat",getString(R.string.lblBtnLogin));
                        DevUser.putString("Account","");
                        tbFrag.setArguments(bn);
                        devUser.setArguments(DevUser);
                        getSupportFragmentManager().beginTransaction().replace(R.id.StatToolbar,tbFrag).commit();
                        getSupportFragmentManager().beginTransaction().replace(R.id.user_container,devUser).commit();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LoginFrag(),"LoginForm").commit();
                    }
                });
                builder.setNegativeButton("Cancel",null);
                builder.setCancelable(false);
                builder.show();
                break;
            case "AmbulantSearch":
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainApp(),"MainApp").commit();
                break;
            case "StallSearch":
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainApp(),"MainApp").commit();
                break;
            case "StallPrint":
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new StallSearchForm(), "StallSearch").commit();
                break;
            case "AmbulantPrint":
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AmbulantSearchForm(),"AmbulantSearch").commit();
                break;
            case "LoginForm":
                this.finish();
                break;
        }

    }

}
