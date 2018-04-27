package com.mohamed.spencer.piepoll;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    public ArrayList<String> answers = new ArrayList<>();

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
                newAnswer(newAnswerEditText.getText().toString());
                newAnswerEditText.setText("");
                break;
            case R.id.button_create_poll_submit:
                submitPoll();
                break;
            case R.id.cb_create_poll_accountreq:
                //accountReqCheck();
                break;
            case R.id.button_create_poll_answer_delete:
                deleteAnswer(view);
                break;
        }
    }

    private void deleteAnswer(View view) {
        View parent = ((View)view.getParent());
        ListView layout = (ListView) parent.getParent();
        int index = layout.indexOfChild(parent);
        answers.remove(index);

        AnswersAdapter adapter = new AnswersAdapter(this, answers);
        ArrayAdapter<String> answersAdapter = adapter;
        listView.setAdapter(answersAdapter);
    }

    private void newAnswer(String answer) {
        if(!answer.matches(""))
        {
            answers.add(answer);
            AnswersAdapter adapter = new AnswersAdapter(this, answers);
            ArrayAdapter<String> answersAdapter = adapter;
            listView.setAdapter(answersAdapter);
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
                ArrayList<String> params = new ArrayList<>();
                String question = questionEditText.getText().toString();
                String multans = multiAnsCheckBox.isChecked() ? "on" : "off";
                String accountreq = accountReqCheckBox.isChecked() ? "on" : "off";
                String uniqueip = uniqueIpCheckBox.isChecked() ? "on" : "off";

                params.add(question);
                params.add(multans);
                params.add(accountreq);
                params.add(uniqueip);
                params.addAll(answers);

                CreatePoll createPoll = new CreatePoll();
                String[] paramsArray = params.toArray(new String[params.size()]);
                createPoll.execute(paramsArray);
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

    public class AnswersAdapter extends ArrayAdapter<String> {
        Context context;
        public AnswersAdapter(Context context, ArrayList<String> answersList) {
            super(context, 0, answersList);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String answer = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_create_poll_answer, parent, false);
            }
            TextView tvCreatePollAnswerText = (TextView) convertView.findViewById(R.id.tv_create_poll_answer_text);
            Button buttonCreatePollAnswerDelete = (Button) convertView.findViewById(R.id.button_create_poll_answer_delete);

            tvCreatePollAnswerText.setText(answer);
            buttonCreatePollAnswerDelete.setOnClickListener((CreatePollActivity)context);



            return convertView;
        }
    }

        private class CreatePoll extends android.os.AsyncTask<String, Double,  WebRequest.Response> {

        protected  WebRequest.Response doInBackground(String... s) {
            WebRequest.Response r = null;
            Multimap<String, String> parameters = ArrayListMultimap.create();
            parameters.put("question", s[0]);
            parameters.put("multans", s[1]);
            parameters.put("accountreq", s[2]);
            parameters.put("uniqueip", s[3]);
            for(int i = 4; i<s.length; i++) {
                parameters.put("options[]", s[i]);
            }

            WebRequest wr = new WebRequest();
            try {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String session = prefs.getString("PHPSESSID", "");
                r = wr.postRequest("http://piepoll.us-east-1.elasticbeanstalk.com/create/createpoll.php", parameters, session);
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
            String pollid = "";

            try {
                JSONObject jsonObject = new JSONObject(result.body);
                status = jsonObject.getInt("code");
                message = jsonObject.getString("error");
                pollid = jsonObject.getString("pollid");

                if(status == 1) {
                    Intent intent = new Intent(getApplicationContext(), TakePollActivity.class);
                    intent.putExtra("pollid", pollid);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.i("d",  result.body);
        }

    }

}
