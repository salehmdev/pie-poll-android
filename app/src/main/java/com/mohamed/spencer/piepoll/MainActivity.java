/*
 * Authors: Mohamed Saleh & Spencer Vierk
 * Description: This is a mobile app that acts as the mobile android version of the website 'Pie Poll'.
 *              Pie Poll is a website created by Spencer Vierk. The purpose of this app is to allow
 *              the user to create and share polls, which other users can vote. This app is designed
 *              to request information from the live version of the Pie Poll website API. All polls and
 *              other data stored by the website should also be accessible by this app and vice versa.
 *
 *              Pie Poll website must be running for this application to work. At the time of creating this
 *              android app, a live version of the website was setup on AWS for the purpose of
 *              connectivity with this application.
 *
 *              The live website used by this app can be found here:
 *              http://piepoll.us-east-1.elasticbeanstalk.com/
 * Date: 4/25/18
 */

package com.mohamed.spencer.piepoll;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements View.OnClickListener{

    public TextView tv1;
    public TextView tv2;
    public TextView tv3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new MainActivityAnimation().execute("");

    }


    @Override
    public void onClick(View view) {
        switch(view.getId())
        {

        }
    }

    // Animation which runs on a separate thread
    private class MainActivityAnimation extends AsyncTask<String, Void, String>{

        // Declarations to be used by this Async task.
        // Declare more views here to be initialized in onPreExecute()
        private TextView tv1;
        private TextView tv2;
        private TextView tv3;
        private Timer timer;
        private int index = 0;  // Used to keep track of which view is already visible

        @Override
        protected String doInBackground(String... strings)
        {
            // Task that uses the index to determine which 2 views to cross-fade
            TimerTask task = new TimerTask(){
                @Override
                public void run() {
                    switch(index){
                        case 0:
                            fadeAwayView(tv1, tv2);
                            break;
                        case 1:
                            fadeAwayView(tv2, tv3);
                            break;
                        case 2:
                            fadeAwayView(tv3, tv1);
                            break;
                    }   // To add more views to transition, add another case here
                    Log.d("Task", "Cross-faded 2 views.");
                }

                // Swaps one view with another using cross-fade animation
                // out: the view that fades away
                // in: view that fades in
                public void fadeAwayView(View out, View in){
                    out.animate()
                            .alpha(0f)
                            .setDuration(1000);
                    in.animate()
                            .alpha(1f)
                            .setDuration(1000);

                    // Change index condition here to add/remove more views to transition
                    if(index!=2) {
                        index++;
                    }else{
                        index = 0;
                    }
                }

            };
            Log.d("Task", "Scheduled new task");
            timer = new Timer(true);
            timer.schedule(task, 0, 2500);    // Every 2.5 seconds, change displayed text

            return "Done";
        }

        @Override
        protected void onPostExecute(String result) {

        }

        @Override
        protected void onPreExecute() {
            // onPreExecute() is able to retrieve GUI components, so this is a good place to initialize them
            // Initialize views here to be able to include them into cycle
            tv1 = (TextView) findViewById(R.id.textView4);
            tv2 = (TextView) findViewById(R.id.textView5);
            tv3 = (TextView) findViewById(R.id.textView6);
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
