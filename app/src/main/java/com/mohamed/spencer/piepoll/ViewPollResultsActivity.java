package com.mohamed.spencer.piepoll;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewPollResultsActivity extends Activity implements View.OnClickListener {

    Poll currentPoll = null;
    ListView lvViewPollResults;
    TextView textViewPollResultQuestion;
    TextView textResultsNumberOfVotes;
    Button buttonViewPollVote;
    Button buttonViewPollRefresh;


    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setContentView(R.layout.activity_view_poll_result);

        lvViewPollResults = (ListView) findViewById(R.id.lv_view_poll_results);
        textViewPollResultQuestion = (TextView) findViewById(R.id.text_view_poll_result_question);
        textResultsNumberOfVotes = (TextView) findViewById(R.id.text_view_poll_result_votes);
        buttonViewPollVote = (Button) findViewById(R.id.button_view_poll_vote);
        buttonViewPollRefresh = (Button) findViewById(R.id.button_view_poll_refresh);

        buttonViewPollVote.setOnClickListener(this);
        buttonViewPollRefresh.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        String pollId = bundle.getString("pollid");
        Boolean hasVoted = bundle.getBoolean("hasvoted");

        if(hasVoted) {
            buttonViewPollVote.setVisibility(View.INVISIBLE);
        }

        GetPoll getPoll = new GetPoll();
        getPoll.execute(pollId);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.button_view_poll_vote:
                buttonViewPollVote_Click();
                break;
            case R.id.button_view_poll_refresh:
                buttonViewPollRefresh_Click();
                break;
        }
    }

    public void buttonViewPollVote_Click() {
        Intent intent = new Intent(this, TakePollActivity.class);
        intent.putExtra("pollid", currentPoll.id);
        startActivity(intent);
        finish();
    }

    public void buttonViewPollRefresh_Click() {
        GetPoll getPoll = new GetPoll();
        getPoll.execute(currentPoll.id);
    }

    public class ResultsAdapter extends ArrayAdapter<PollOption> {
        public ResultsAdapter(Context context, ArrayList<PollOption> resultsList) {
            super(context, 0, resultsList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PollOption option = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_poll_results_option, parent, false);
            }
            TextView textViewPollResultOption = (TextView) convertView.findViewById(R.id.text_view_poll_results_option);
            TextView textViewPollResultPercent = (TextView) convertView.findViewById(R.id.text_view_poll_results_percent);
            TextView textViewPollResultVotes = (TextView) convertView.findViewById(R.id.text_view_poll_results_votes);
            ProgressBar pbViewPollResultsBar = (ProgressBar) convertView.findViewById(R.id.pb_view_poll_results_bar);
            textViewPollResultOption.setText(option.description);
            textViewPollResultPercent.setText(String.valueOf(option.percent) + "%");
            textViewPollResultVotes.setText(String.valueOf(option.votes));
            pbViewPollResultsBar.setProgress(option.percent);

            return convertView;
        }
    }

    private class GetPoll extends AsyncTask<String, Double,  WebRequest.Response> {

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
            buttonViewPollRefresh.setEnabled(false);
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

                textViewPollResultQuestion.setText(poll.question);

                GetPollResults getPollResults = new GetPollResults();
                getPollResults.execute(poll.id);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.i("d",  result.body);
        }

    }

    private class GetPollResults extends AsyncTask<String, Double,  WebRequest.Response> {

        protected  WebRequest.Response doInBackground(String... s) {
            WebRequest.Response r = null;
            Multimap<String, String> parameters = ArrayListMultimap.create();
            parameters.put("poll", s[0]);

            WebRequest wr = new WebRequest();
            try {
                r = wr.postRequest("http://piepoll.us-east-1.elasticbeanstalk.com/poll/getpollresults.php", parameters);
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

                double totalVotes = 0;
                JSONArray options = jsonObject.getJSONArray("votes");
                for (int i = 0; i < options.length(); i++) {
                    int votes = options.getInt(i);
                    totalVotes += votes;
                    currentPoll.options.get(i).votes = votes;
                }

                textResultsNumberOfVotes.setText(String.valueOf((int)totalVotes) + " Votes");

                for (int i = 0; i < options.length(); i++) {
                    double votes = options.getInt(i);
                    currentPoll.options.get(i).percent = (int)Math.floor((votes / totalVotes)*100);
                }

                ResultsAdapter adapter = new ResultsAdapter(getApplicationContext(), currentPoll.options);
                ArrayAdapter<PollOption> pollAdapter = adapter;
                lvViewPollResults.setAdapter(pollAdapter);
                buttonViewPollRefresh.setEnabled(true);
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
        public int votes;
        public int percent;

        public PollOption(String id, String description) {
            this.id = id;
            this.description = description;
        }
    }



}
