package com.pylon.emarketpos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.pylon.emarketpos.controllers.LoginFrag;

public class MainView extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);
        if(findViewById(R.id.fragment_container) != null){
            if(savedInstanceState != null){
                return;
            }
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new LoginFrag(),null).commit();
        }
    }
}
