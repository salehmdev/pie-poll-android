package com.mohamed.spencer.piepoll;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class CreatePollActivity extends Activity implements View.OnClickListener {

    public EditText questionEditText;
    public EditText newAnswerEditText;
    public CheckBox multiAnsCheckBox;
    public CheckBox accountReqCheckBox;
    public CheckBox uniqueIpCheckBox;
    public Button createPollButton;
    public Button newAnswerButton;
    public ListView  listView;
    public SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        questionEditText = (EditText) findViewById(R.id.et_create_poll_question);
        newAnswerEditText = (EditText) findViewById(R.id.et_create_poll_new_answer);
        multiAnsCheckBox = (CheckBox) findViewById(R.id.cb_create_poll_multians);
        accountReqCheckBox = (CheckBox) findViewById(R.id.cb_create_poll_accountreq);
        uniqueIpCheckBox = (CheckBox) findViewById(R.id.cb_create_poll_uniqueip);
        newAnswerButton = (Button) findViewById(R.id.button_create_poll_new_answer);
        createPollButton = (Button) findViewById(R.id.button_create_poll_submit);
        listView = (ListView) findViewById(R.id.lv_create_poll);

        newAnswerButton.setOnClickListener(this);
        createPollButton.setOnClickListener(this);
        accountReqCheckBox.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.button_create_poll_new_answer:
                newAnswer();
                break;
            case R.id.button_create_poll_submit:
                submitPoll();
                break;
            case R.id.cb_create_poll_accountreq:
                accountReqCheck();
                break;
        }
    }

    private void newAnswer() {
        // TODO: implement create poll button handler
        if(!newAnswerEditText.getText().toString().matches(""))
        {

        }
        else{
            Toast.makeText(this, "New answer cannot be empty!", Toast.LENGTH_SHORT).show();
        }
    }

    private void submitPoll() {
        // TODO: implement submit handler
        if (!questionEditText.getText().toString().matches("")) {

            if(listView.getChildCount() != 0)
            {

            }
            else{
                Toast.makeText(this, "Must add at least one answer!", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Must type a question name!", Toast.LENGTH_SHORT).show();
        }
    }

    private void accountReqCheck() {
        if (prefs.getString("username", "Guest").matches("Guest")) {
            Toast.makeText(this, "You must be logged in to require accounts!", Toast.LENGTH_SHORT).show();
            accountReqCheckBox.setChecked(false);
        }

    }
}
