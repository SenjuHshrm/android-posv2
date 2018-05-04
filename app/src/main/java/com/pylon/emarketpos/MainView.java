package com.pylon.emarketpos;

import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.pylon.emarketpos.controllers.*;
import com.pylon.emarketpos.tasks.DatabaseHelper;

public class MainView extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);
        DatabaseHelper DBHelp = new DatabaseHelper(this);
        Cursor getIP = DBHelp.selectIP();
        if(getIP.getCount() == 0){
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new ConnSettings(), "SetupConnStart").commit();
        }else{
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new CheckConnection()).commit();
        }
    }
    public void OpenSettings(View view){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ConnSettings(), "SetupConnMod").commit();
    }
    @Override
    public void onBackPressed(){
        final DatabaseHelper dbHelper;
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
        final ToolbarFrag tbFrag = new ToolbarFrag();
        final DeviceUser devUser = new DeviceUser();
        final Bundle DevUser = new Bundle();
        final Bundle bn = new Bundle();
        switch(currentFragment.getTag()){
            case "MainApp":
                dbHelper = new DatabaseHelper(this);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("\tDo you want to log out");
                builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dbHelper.deleteData();
                        bn.putString("Stat",getString(R.string.lblBtnLogin));
                        DevUser.putString("Account","");
                        tbFrag.setArguments(bn);
                        devUser.setArguments(DevUser);
                        getSupportFragmentManager().beginTransaction().replace(R.id.StatToolbar,tbFrag).commit();
                        getSupportFragmentManager().beginTransaction().replace(R.id.user_container,devUser).commit();
                        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in,R.anim.fade_out).replace(R.id.fragment_container, new LoginFrag(),"LoginForm").commit();
                    }
                });
                builder.setNegativeButton("Cancel",null);
                builder.setCancelable(false);
                builder.show();
                break;
            case "AmbulantSearch":
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right).replace(R.id.fragment_container, new MainApp(),"MainApp").commit();
                break;
            case "StallSearch":
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right).replace(R.id.fragment_container, new MainApp(),"MainApp").commit();
                break;
            case "StallPrint":
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right).replace(R.id.fragment_container, new StallSearchForm(), "StallSearch").commit();
                break;
            case "AmbulantPrint":
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right).replace(R.id.fragment_container, new AmbulantSearchForm(),"AmbulantSearch").commit();
                break;
            case "LoginForm":
                this.finish();
                break;
            case "NoConn":
                this.finish();
                break;
            case "SetupConnStart":
                this.finish();
                break;
            case "SetupConnMod":
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CheckConnection()).commit();
                break;

        }

    }

}
