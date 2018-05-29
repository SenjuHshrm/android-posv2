package com.pylon.emarketpos;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.pylon.emarketpos.controllers.*;
import com.pylon.emarketpos.tasks.DatabaseHelper;

public class MainView extends AppCompatActivity{

    private TextView pageStat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);
        pageStat = (TextView) findViewById(R.id.pageStat);
        DatabaseHelper DBHelp = new DatabaseHelper(this);
        String getIP = DBHelp.selectIP();
        if(getIP.equals("")){
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new ConnSettings(), "SetupConnStart").commit();
            pageStat.setText("SETTINGS");
        }else{
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new CheckConnection()).commit();
        }
    }
    public void OpenSettings(View view){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ConnSettings(), "SetupConnMod").commit();
        pageStat.setText("SETTINGS");
    }
    @Override
    public void onBackPressed(){
        final DatabaseHelper dbHelper;
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
        switch(currentFragment.getTag()){
            case "MainApp":
                dbHelper = new DatabaseHelper(this);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("\tDo you want to log out");
                builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dbHelper.deleteData();
                        DeviceUser DevUse = new DeviceUser();
                        Bundle user = new Bundle();
                        user.putString("DevUser", "");
                        DevUse.setArguments(user);
                        getSupportFragmentManager().beginTransaction().replace(R.id.devuser_con, DevUse).commit();
                        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in,R.anim.fade_out).replace(R.id.fragment_container, new LoginFrag(),"LoginForm").commit();
                        pageStat.setText(R.string.lblBtnLogin);
                    }
                });
                builder.setNegativeButton("Cancel",null);
                builder.setCancelable(false);
                builder.show();
                break;
            case "AmbulantSearch":
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right).replace(R.id.fragment_container, new MainApp(),"MainApp").commit();
                pageStat.setText(R.string.toolbarTitleMenu);
                break;
            case "StallSearch":
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right).replace(R.id.fragment_container, new MainApp(),"MainApp").commit();
                pageStat.setText(R.string.toolbarTitleMenu);
                break;
            case "StallPrint":
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right).replace(R.id.fragment_container, new StallSearchForm(), "StallSearch").commit();
                pageStat.setText(R.string.lblStallList);
                break;
            case "AmbulantPrint":
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right).replace(R.id.fragment_container, new AmbulantSearchForm(),"AmbulantSearch").commit();
                pageStat.setText(R.string.lblAmbList);
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
                pageStat.setText("");
                break;

        }

    }

}
