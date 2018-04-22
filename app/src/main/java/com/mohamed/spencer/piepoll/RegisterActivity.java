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

public class RegisterActivity extends Activity implements View.OnClickListener {

    public Button registerButton;
    public EditText usernameEditText;
    public EditText passwordEditText;
    public EditText confirmPasswordEditText;
    public SharedPreferences prefs;
    public SharedPreferences.Editor editor;


    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setContentView(R.layout.activity_register);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = prefs.edit();

        usernameEditText = (EditText) findViewById(R.id.et_register_username);
        passwordEditText = (EditText) findViewById(R.id.et_register_password);
        confirmPasswordEditText = (EditText) findViewById(R.id.et_register_confirm_password);
        registerButton = (Button) findViewById(R.id.button_register);

        registerButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.button_register:
                // TODO: Check if valid registration credentials and then store into website DB if valid
                if(true)
                {
                    // TODO: Take user to HomeActivity as logged in
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
