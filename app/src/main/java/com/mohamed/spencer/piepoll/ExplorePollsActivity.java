package com.mohamed.spencer.piepoll;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ExplorePollsActivity extends Activity implements View.OnClickListener {

    Button previousButton;
    Button nextButton;

    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setContentView(R.layout.activity_explore_polls);

        previousButton = (Button) findViewById(R.id.button_explore_polls_previous);
        nextButton = (Button) findViewById(R.id.button_explore_polls_next);

        previousButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

        // TODO: Retrieve poll list from DB and create a onClickListener for each, which starts new activity ViewPollResultsActivity.class
        // Possible solution: Use ScrollView instead of ListView?

    }

    @Override
    public void onClick(View view) {
        // TODO: Change displayed data based on page number, update button (enabled/disabled)
        switch(view.getId())
        {
            case R.id.button_explore_polls_previous:

                break;
            case R.id.button_explore_polls_next:

                break;
        }
    }
}
