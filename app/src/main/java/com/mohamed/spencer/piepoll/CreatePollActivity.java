package com.mohamed.spencer.piepoll;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

public class CreatePollActivity extends Activity implements View.OnClickListener {

    public EditText questionEditText;
    public EditText newAnswerEditText;
    public Switch multiAnsSwitch;
    public Switch accountReqSwitch;
    public Switch uniqueIpSwitch;
    public Button createPollButton;
    public Button newAnswerButton;
    public ListView  listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll);

        questionEditText = (EditText) findViewById(R.id.et_create_poll_question);
        newAnswerEditText = (EditText) findViewById(R.id.et_create_poll_new_answer);
        multiAnsSwitch = (Switch) findViewById(R.id.switch_create_poll_multians);
        accountReqSwitch = (Switch) findViewById(R.id.switch_create_poll_accountreq);
        uniqueIpSwitch = (Switch) findViewById(R.id.switch_create_poll_uniqueip);
        newAnswerButton = (Button) findViewById(R.id.button_create_poll_new_answer);
        createPollButton = (Button) findViewById(R.id.button_create_poll_submit);
        listView = (ListView) findViewById(R.id.lv_create_poll);

        newAnswerButton.setOnClickListener(this);
        createPollButton.setOnClickListener(this);

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
}
