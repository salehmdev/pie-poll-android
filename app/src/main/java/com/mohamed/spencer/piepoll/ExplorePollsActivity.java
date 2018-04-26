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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExplorePollsActivity extends Activity implements View.OnClickListener {

    Button previousButton;
    Button nextButton;
    ListView listViewPollList;
    int pollsOffset = 0;
    List<Poll> pollsList = new ArrayList<Poll>();

    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setContentView(R.layout.activity_explore_polls);

        previousButton = (Button) findViewById(R.id.button_explore_polls_previous);
        nextButton = (Button) findViewById(R.id.button_explore_polls_next);
        listViewPollList = (ListView) findViewById(R.id.listview_poll_list);

        previousButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        listViewPollList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onPollClick(parent, view, position, id);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        GetPolls getPolls = new GetPolls();
        getPolls.execute(Integer.toString(pollsOffset));
    }

    @Override
    public void onClick(View view) {
        GetPolls getPolls;
        switch(view.getId())
        {
            case R.id.button_explore_polls_previous:
                pollsOffset -= 10;
                if (pollsOffset < 0) {
                    pollsOffset = 0;
                    Toast.makeText(getApplicationContext(), "Beginning of polls", Toast.LENGTH_SHORT).show();
                }
                getPolls = new GetPolls();
                getPolls.execute(Integer.toString(pollsOffset));
                break;
            case R.id.button_explore_polls_next:
                pollsOffset += 10;
                getPolls = new GetPolls();
                getPolls.execute(Integer.toString(pollsOffset));
                break;
        }
    }

    public void onPollClick(AdapterView<?> parent, View view, int position, long id) {
        ListAdapter pollAdapter = listViewPollList.getAdapter();
        Poll p = (Poll)pollAdapter.getItem(position);

        Intent intent = new Intent(this, TakePollActivity.class);
        intent.putExtra("pollid", p.id);
        startActivity(intent);
    }

    private class GetPolls extends AsyncTask<String, Double,  WebRequest.Response> {

        protected  WebRequest.Response doInBackground(String... s) {
            WebRequest.Response r = null;
            Map<String, String> parameters = new HashMap<>();
            parameters.put("start", s[0]);

            WebRequest wr = new WebRequest();
            try {
                r = wr.postRequest("http://piepoll.us-east-1.elasticbeanstalk.com/explore/getpolls.php", parameters);
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

                JSONArray polls = jsonObject.getJSONArray("polls");

                if(polls.length() > 0) {
                    ArrayList<Poll> pList = new ArrayList<Poll>();

                    for (int i = 0; i < polls.length(); i++) {
                        JSONObject poll = polls.getJSONObject(i);
                        String id = poll.getString("id");
                        String question = poll.getString("question");
                        String votes = poll.getString("votes");
                        String days = poll.getString("days");
                        Poll p = new Poll(id, question, votes, days);
                        Log.i("Poll", "ID: " + id + ", question: " + question + ", votes: " + votes + ", days: " + days);
                        pList.add(p);
                    }
                    pollsList = pList;
                    PollsAdapter adapter = new PollsAdapter(getApplicationContext(), pList);
                    ArrayAdapter<Poll> pollAdapter = adapter;
                    listViewPollList.setAdapter(pollAdapter);
                } else {
                    Toast.makeText(getApplicationContext(), "No more polls!", Toast.LENGTH_SHORT).show();
                }

                if (polls.length() < 10) {
                    nextButton.setEnabled(false);
                    if(polls.length() == 0) {
                        pollsOffset -= 10;
                    }
                } else {
                    nextButton.setEnabled(true);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.i("d",  result.body);
        }

    }

    public class PollsAdapter extends ArrayAdapter<Poll> {
        public PollsAdapter(Context context, ArrayList<Poll> pollsList) {
            super(context, 0, pollsList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Poll poll = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.explore_polls_item, parent, false);
            }
            TextView tvQuestion = (TextView) convertView.findViewById(R.id.tvExplorePollQuestion);
            TextView tvDays = (TextView) convertView.findViewById(R.id.tvExplorePollDays);
            TextView tvVotes = (TextView) convertView.findViewById(R.id.tvExplorePollVotes);
            tvQuestion.setText(poll.question);
            tvVotes.setText(poll.votes + "v");
            if(poll.days.equals("0")) {
                tvDays.setVisibility(View.INVISIBLE);
            } else {
                tvDays.setText(poll.days + "d");
            }

            return convertView;
        }
    }

    public class Poll {
        public String id;
        public String question;
        public String votes;
        public String days;

        public Poll(String id, String question, String votes, String days) {
            this.id = id;
            this.question = question;
            this.votes = votes;
            this.days = days;
        }
    }
}
