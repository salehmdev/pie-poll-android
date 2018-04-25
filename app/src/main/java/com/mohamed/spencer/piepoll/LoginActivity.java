package com.mohamed.spencer.piepoll;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity implements View.OnClickListener {

    public Button loginButton;
    public EditText usernameEditText;
    public EditText passwordEditText;
    public SharedPreferences prefs;
    public SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setContentView(R.layout.activity_login);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = prefs.edit();

        usernameEditText = (EditText) findViewById(R.id.et_login_username);
        passwordEditText = (EditText) findViewById(R.id.et_login_password);
        loginButton = (Button) findViewById(R.id.button_login);

        loginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.button_login:
            // TODO: Check login credentials and either give error or advance to home (Also store login in sharedprefs?)
                if(true)
                {
                    editor.putBoolean("isLoggedIn", true);
                    editor.putString("username", usernameEditText.getText().toString());
                    editor.putString("password", passwordEditText.getText().toString());
                    editor.commit();
                    Intent intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(this, "Incorrect username/password", Toast.LENGTH_SHORT).show();
                }
            break;
        }
    }
}
