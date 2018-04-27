package com.mohamed.spencer.piepoll;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TakePollActivity extends Activity implements View.OnClickListener {

    TextView textTakePollName;
    TextView textTakePollCreator;
    TextView textTakePollMulti;
    TextView textTakePollAccount;
    TextView textTakePollIP;
    Button buttonTakePollVote;
    Button buttonTakePollResults;
    LinearLayout llTakePoll;
    Poll currentPoll = null;

    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setContentView(R.layout.activity_take_poll);

        textTakePollName = (TextView) findViewById(R.id.text_take_poll_name);
        textTakePollCreator = (TextView) findViewById(R.id.text_take_poll_creator);
        textTakePollMulti = (TextView) findViewById(R.id.text_take_poll_multi);
        textTakePollAccount = (TextView) findViewById(R.id.text_take_poll_account);
        textTakePollIP = (TextView) findViewById(R.id.text_take_poll_ip);
        buttonTakePollVote = (Button) findViewById(R.id.button_take_poll_vote);
        buttonTakePollResults = (Button) findViewById(R.id.button_take_poll_results);
        llTakePoll = (LinearLayout) findViewById(R.id.ll_take_poll);

        textTakePollMulti.setVisibility(View.GONE);
        textTakePollAccount.setVisibility(View.GONE);
        textTakePollIP.setVisibility(View.GONE);

        buttonTakePollVote.setOnClickListener(this);
        buttonTakePollResults.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        String pollId = bundle.getString("pollid");

        HasVoted hasVoted = new HasVoted(this);
        hasVoted.execute(pollId);

        /*
            Nice resource for adding checkboxes dynamically to a layout:
            https://stackoverflow.com/questions/7618553/how-to-add-checkboxes-dynamically-in-android
         */
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.button_take_poll_vote:
                buttonTakePollVote_Clicked();
                break;
            case R.id.button_take_poll_results:
                buttonTakePollResults_Clicked();
                break;
        }

        if(view instanceof CheckBox)
        {
            if(!currentPoll.multanswer) {
                for( int i = 0; i<llTakePoll.getChildCount(); i++) {
                    if(llTakePoll.getChildAt(i) instanceof CheckBox && !view.equals(llTakePoll.getChildAt(i))) {
                        ((CheckBox) llTakePoll.getChildAt(i)).setChecked(false);
                    }
                }
            }
            CheckBox clickedCheckBox = (CheckBox) view;
        }
    }

    public void buttonTakePollVote_Clicked() {
        VotePoll votePoll = new VotePoll();
        ArrayList<String> params = new ArrayList<>();
        params.add(currentPoll.id);
        for( int i = 0; i<llTakePoll.getChildCount(); i++) {
            if(llTakePoll.getChildAt(i) instanceof CheckBox && ((CheckBox) llTakePoll.getChildAt(i)).isChecked()) {
                params.add(llTakePoll.getChildAt(i).getTag().toString());
            }
        }

        if(params.size() < 2) {
            Toast.makeText(getApplicationContext(), "You must select an option!", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] paramsArr = params.toArray(new String[params.size()]);
        votePoll.execute(paramsArr);
    }

    public void buttonTakePollResults_Clicked() {
        Intent intent = new Intent(this, ViewPollResultsActivity.class);
        intent.putExtra("pollid", currentPoll.id);
        startActivity(intent);
        finish();
    }

    private class HasVoted extends AsyncTask<String, Double,  WebRequest.Response> {

        private Context activityContext;
        public HasVoted(Context c) {
            this.activityContext = c;
        }

        public int pollid = 0;
        protected  WebRequest.Response doInBackground(String... s) {
            WebRequest.Response r = null;
            Multimap<String, String> parameters = ArrayListMultimap.create();
            parameters.put("poll", s[0]);
            pollid = Integer.parseInt(s[0]);
            WebRequest wr = new WebRequest();
            try {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String session = prefs.getString("PHPSESSID", "");
                r = wr.postRequest("http://piepoll.us-east-1.elasticbeanstalk.com/poll/userhasvoted.php", parameters, session);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //publishProgress(progress);

            return r;
        }

        protected void onProgressUpdate(Double... progress) {
            //Log.i("a", Double.toString(progress[0]));

        }

        protected void onPreExecute() {

        }

        protected void onPostExecute(WebRequest.Response result) {

            try {
                JSONObject jsonObject = new JSONObject(result.body);

                int code = jsonObject.getInt("code");
                if(code != 1) {
                    Intent intent = new Intent(getApplicationContext(), ViewPollResultsActivity.class);
                    intent.putExtra("pollid", Integer.toString(pollid));
                    intent.putExtra("hasvoted", true);
                    startActivity(intent);
                    finish();
                } else {
                    GetPoll getPoll = new GetPoll(activityContext);
                    getPoll.execute(Integer.toString(pollid));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.i("d",  result.body);
        }

    }

    private class VotePoll extends AsyncTask<String, Double,  WebRequest.Response> {

        protected  WebRequest.Response doInBackground(String... s) {
            WebRequest.Response r = null;
            Multimap<String, String> parameters = ArrayListMultimap.create();
            parameters.put("pollid", s[0]);
            for(int i = 1; i<s.length; i++) {
                parameters.put("options[" + s[i] + "]", "on");
            }

            WebRequest wr = new WebRequest();
            try {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String session = prefs.getString("PHPSESSID", "");
                r = wr.postRequest("http://piepoll.us-east-1.elasticbeanstalk.com/poll/vote.php", parameters, session);
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

                if(status == 1) {
                    Intent intent = new Intent(getApplicationContext(), ViewPollResultsActivity.class);
                    intent.putExtra("pollid", currentPoll.id);
                    intent.putExtra("hasvoted", true);
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

    private class GetPoll extends AsyncTask<String, Double,  WebRequest.Response> {

        private Context activityContext;
        public GetPoll(Context c) {
            this.activityContext = c;
        }

        protected  WebRequest.Response doInBackground(String... s) {
            WebRequest.Response r = null;
            Multimap<String, String> parameters = ArrayListMultimap.create();
            parameters.put("poll", s[0]);

            WebRequest wr = new WebRequest();
            try {
                r = wr.postRequest("http://piepoll.us-east-1.elasticbeanstalk.com/poll/getpoll.php", parameters);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //publishProgress(progress);

            return r;
        }

        protected void onProgressUpdate(Double... progress) {
            //Log.i("a", Double.toString(progress[0]));

        }

        protected void onPreExecute() {

        }

        protected void onPostExecute(WebRequest.Response result) {

            try {
                JSONObject jsonObject = new JSONObject(result.body);

                String id = jsonObject.getString("id");
                String creator = jsonObject.getString("creator");
                String question = jsonObject.getString("question");
                Boolean multAnswer = "1".equals(jsonObject.getString("multanswer"));
                Boolean ipCheck = "1".equals(jsonObject.getString("ipcheck"));
                Boolean accountReq = "1".equals(jsonObject.getString("accountreq"));

                JSONArray options = jsonObject.getJSONArray("options");
                ArrayList<PollOption> pollOptions = new ArrayList<>();
                for (int i = 0; i < options.length(); i++) {
                    JSONObject option = options.getJSONObject(i);
                    String optionId = option.getString("id");
                    String optionDescription = option.getString("description");
                    pollOptions.add(new PollOption(optionId, optionDescription));
                }

                Poll poll = new Poll(id, creator, question, multAnswer, ipCheck, accountReq, pollOptions);
                currentPoll = poll;

                textTakePollName.setText(poll.question);

                textTakePollCreator.setText(poll.creator);

                if(poll.multanswer) {
                    textTakePollMulti.setVisibility(View.VISIBLE);
                }

                if(poll.accountreq) {
                    textTakePollAccount.setVisibility(View.VISIBLE);
                }

                if(poll.ipcheck) {
                    textTakePollIP.setVisibility(View.VISIBLE);
                }

                for( int i = 0; i<poll.options.size(); i++) {
                    CheckBox chkOption = new CheckBox(getApplicationContext());
                    chkOption.setText(poll.options.get(i).description);
                    chkOption.setTag(poll.options.get(i).id);
                    chkOption.setTextSize(24f);
                    chkOption.setOnClickListener((TakePollActivity) activityContext);
                    llTakePoll.addView(chkOption);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.i("d",  result.body);
        }

    }

    public class Poll {
        public String id;
        public String creator;
        public String question;
        public Boolean multanswer;
        public Boolean ipcheck;
        public Boolean accountreq;
        public ArrayList<PollOption> options;

        public Poll(String id, String creator, String question, Boolean multanswer, Boolean ipcheck, Boolean accountreq, ArrayList<PollOption> options) {
            this.id = id;
            this.creator = creator;
            this.question = question;
            this.multanswer = multanswer;
            this.ipcheck = ipcheck;
            this.accountreq = accountreq;
            this.options = options;
        }
    }

    public class PollOption {
        public String id;
        public String description;

        public PollOption(String id, String description) {
            this.id = id;
            this.description = description;
        }
    }

}
