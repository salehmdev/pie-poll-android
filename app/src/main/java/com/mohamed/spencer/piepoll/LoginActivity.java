package com.mohamed.spencer.piepoll;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
                LoginTask stuff = new LoginTask();
                stuff.execute(usernameEditText.getText().toString(), passwordEditText.getText().toString());
            break;
        }
    }

    private class LoginTask extends AsyncTask<String, Double,  WebRequest.Response> {

        protected  WebRequest.Response doInBackground(String... s) {
            WebRequest.Response r = null;
            Map<String, String> parameters = new HashMap<>();
            parameters.put("username", s[0]);
            parameters.put("password", s[1]);

            WebRequest wr = new WebRequest();
            try {
                r = wr.postRequest("http://piepoll.us-east-1.elasticbeanstalk.com/login/login.php", parameters);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //publishProgress(progress);

            return r;
        }

        protected void onProgressUpdate(Double... progress) {
            //Log.i("a", Double.toString(progress[0]));

        }

        protected void onPostExecute(WebRequest.Response result) {
            int status = -1;
            String message = "";

            try {
                JSONObject jsonObject = new JSONObject(result.body);
                status = jsonObject.getInt("code");
                message = jsonObject.getString("error");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            // TODO: Check login credentials and either give error or advance to home (Also store login in sharedprefs?)
            if(status == 1)
            {
                editor.putBoolean("isLoggedIn", true);
                editor.putString("PHPSESSID", result.session);
                editor.putString("username", usernameEditText.getText().toString());
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
            else{
                Toast.makeText(getApplicationContext(), "Incorrect username/password", Toast.LENGTH_SHORT).show();
            }


            Log.i("d",  result.body);
        }
    }
}
