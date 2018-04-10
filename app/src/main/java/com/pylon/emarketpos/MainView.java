package com.pylon.emarketpos;

import android.support.v4.app.FragmentTransaction;
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
            //getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new LoginFrag(),null).commit();
            //getSupportFragmentManager().beginTransaction().add(R.id.StatToolbar, new ToolbarFrag()).commit();
            //Stat
            Bundle statBundle = new Bundle();
            ToolbarFrag tbFrag = new ToolbarFrag();
            FragmentTransaction LoginMan = getSupportFragmentManager().beginTransaction();
            statBundle.putString("Stat",this.getString(R.string.lblBtnLogin));
            tbFrag.setArguments(statBundle);
            LoginMan.add(R.id.StatToolbar,tbFrag).commit();
            //LoginForm
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new LoginFrag(),null).commit();

        }
    }
}
