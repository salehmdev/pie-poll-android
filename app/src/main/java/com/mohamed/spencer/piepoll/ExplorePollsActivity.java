package com.mohamed.spencer.piepoll;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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

    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setContentView(R.layout.activity_explore_polls);

        previousButton = (Button) findViewById(R.id.button_explore_polls_previous);
        nextButton = (Button) findViewById(R.id.button_explore_polls_next);
        listViewPollList = (ListView) findViewById(R.id.listview_poll_list);

        previousButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

        // TODO: Retrieve poll list from DB and create a onClickListener for each, which starts new activity ViewPollResultsActivity.class
        // Possible solution: Use ScrollView instead of ListView?

    }

    @Override
    public void onResume() {
        super.onResume();
        GetPolls getPolls = new GetPolls();
        getPolls.execute(Integer.toString(pollsOffset));
    }

    @Override
    public void onClick(View view) {
        // TODO: Change displayed data based on page number, update button (enabled/disabled)
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
                    List<String> pollsList = new ArrayList<String>();

                    for (int i = 0; i < polls.length(); i++) {
                        JSONObject poll = polls.getJSONObject(i);
                        String id = poll.getString("id");
                        String question = poll.getString("question");
                        String votes = poll.getString("votes");
                        String days = poll.getString("days");
                        Log.i("Poll", "ID: " + id + ", question: " + question + ", votes: " + votes + ", days: " + days);
                        pollsList.add(question);
                    }
                    ArrayAdapter<String> pollAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, pollsList);
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
}
