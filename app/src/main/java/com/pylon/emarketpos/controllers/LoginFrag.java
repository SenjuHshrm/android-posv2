package com.pylon.emarketpos.controllers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.pylon.emarketpos.R;
import com.pylon.emarketpos.tasks.LoginAuth;

public class LoginFrag extends Fragment implements OnClickListener{
    private EditText username, password;
    public LoginFrag() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ImageButton settBtn = (ImageButton) getActivity().findViewById(R.id.openSettings);
        settBtn.setVisibility(View.VISIBLE);
        TextView pageStat = (TextView) getActivity().findViewById(R.id.pageStat);
        pageStat.setText(getResources().getString(R.string.lblBtnLogin));
        username = (EditText) view.findViewById(R.id.UsernameInput);
        password = (EditText) view.findViewById(R.id.PasswordInput);
        Button LoginBtn = (Button) view.findViewById(R.id.btnLogin);
        Button RegBtn = (Button) view.findViewById(R.id.btnCreateAc);
        LoginBtn.setOnClickListener(this);
        RegBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnCreateAc:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RegisterUser(), "RegUser").commit();
                break;
            case R.id.btnLogin:
                final String getUsername = username.getText().toString();
                final String getPassword = password.getText().toString();
                if(getUsername.equalsIgnoreCase("") || getPassword.equalsIgnoreCase("")){
                    Toast.makeText(getActivity(),"Please input required fields",Toast.LENGTH_SHORT).show();
                }else {
                    new LoginAuth(view.getContext(), LoginFrag.this).execute(getUsername, getPassword);
                }
                break;
        }
    }
}
