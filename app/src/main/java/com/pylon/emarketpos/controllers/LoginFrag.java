package com.pylon.emarketpos.controllers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.pylon.emarketpos.R;
import com.pylon.emarketpos.tasks.LoginAuth;

public class LoginFrag extends Fragment{
    private EditText username, password;
    public LoginFrag() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        username = (EditText) view.findViewById(R.id.UsernameInput);
        password = (EditText) view.findViewById(R.id.PasswordInput);
        Button LoginBtn = (Button) view.findViewById(R.id.btnLogin);
        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String getUsername = username.getText().toString();
                final String getPassword = password.getText().toString();
                new LoginAuth(view.getContext(), LoginFrag.this).execute(getUsername,getPassword);
            }
        });
        return view;
    }

}
