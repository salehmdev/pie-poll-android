package com.mohamed.spencer.piepoll;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TakePollActivity extends Activity {

    TextView textTakePollName;
    LinearLayout llTakePoll;
    Poll currentPoll = null;

    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setContentView(R.layout.activity_take_poll);

        textTakePollName = (TextView) findViewById(R.id.text_take_poll_name);
        llTakePoll = (LinearLayout) findViewById(R.id.ll_take_poll);

        Bundle bundle = getIntent().getExtras();
        String pollId = bundle.getString("pollid");
        Toast.makeText(getApplicationContext(), "Taking poll of id: " + pollId, Toast.LENGTH_SHORT).show();

        GetPoll getPoll = new GetPoll();
        getPoll.execute(pollId);

        /*
            Nice resource for adding checkboxes dynamically to a layout:
            https://stackoverflow.com/questions/7618553/how-to-add-checkboxes-dynamically-in-android
         */
    }

    private class GetPoll extends AsyncTask<String, Double,  WebRequest.Response> {

        protected  WebRequest.Response doInBackground(String... s) {
            WebRequest.Response r = null;
            Map<String, String> parameters = new HashMap<>();
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

        protected void onPostExecute(WebRequest.Response result) {

            try {
                JSONObject jsonObject = new JSONObject(result.body);

                String id = jsonObject.getString("id");
                String creator = jsonObject.getString("creator");
                String question = jsonObject.getString("question");
                Boolean multAnswer = Boolean.parseBoolean(jsonObject.getString("multanswer"));
                Boolean ipCheck = Boolean.parseBoolean(jsonObject.getString("ipcheck"));
                Boolean accountReq = Boolean.parseBoolean(jsonObject.getString("accountreq"));

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

                TextView newTV = new TextView(getApplicationContext());
                newTV.setText("Creator: " + poll.creator);
                newTV.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                llTakePoll.addView(newTV);

                for( int i = 0; i<poll.options.size(); i++) {
                    TextView newTV2 = new TextView(getApplicationContext());
                    newTV2.setText(poll.options.get(i).description);
                    llTakePoll.addView(newTV2);
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
