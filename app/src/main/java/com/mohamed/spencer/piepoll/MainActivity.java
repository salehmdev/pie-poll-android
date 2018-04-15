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
import android.view.View;
import android.widget.EditText;
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

    private class MainActivityAnimation extends AsyncTask<String, Void, String>{

        private TextView tv1;
        private TextView tv2;
        private TextView tv3;
        private Timer timer;
        private int index = 0;

        @Override
        protected String doInBackground(String... strings)
        {
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
                    }
                }

                public void fadeAwayView(View out, View in){
                    out.animate()
                            .alpha(0f)
                            .setDuration(1000);
                    in.animate()
                            .alpha(1f)
                            .setDuration(1000);
                    if(index!=2) {
                        index++;
                    }else{
                        index = 0;
                    }
                }

//                public void fadeAwayTV2(){
//                    tv2.animate()
//                            .alpha(0f)
//                            .setDuration(1000)
//                            .setListener(new AnimatorListenerAdapter() {
//                                @Override
//                                public void onAnimationEnd(Animator animation) {
//                                    tv1.setVisibility(View.GONE);
//                                }
//                            });
//                    tv3.setVisibility(View.VISIBLE);
//                    tv3.animate()
//                            .alpha(1f)
//                            .setDuration(1000)
//                            .setListener(null);
//                    index = 2;
//                }
//
//                public void fadeAwayTV3(){
//                    tv2.animate()
//                            .alpha(0f)
//                            .setDuration(1000)
//                            .setListener(new AnimatorListenerAdapter() {
//                                @Override
//                                public void onAnimationEnd(Animator animation) {
//                                    tv1.setVisibility(View.GONE);
//                                }
//                            });
//                    tv3.setVisibility(View.VISIBLE);
//                    tv3.animate()
//                            .alpha(1f)
//                            .setDuration(1000)
//                            .setListener(null);
//                    index = 0;
//                }
            };
            timer = new Timer(true);
            timer.schedule(task, 0, 3000);    // Checks approximately every second

            return "Done";
        }

        @Override
        protected void onPostExecute(String result) {

        }

        @Override
        protected void onPreExecute() {
            tv1 = (TextView) findViewById(R.id.textView4);
            tv2 = (TextView) findViewById(R.id.textView5);
            tv3 = (TextView) findViewById(R.id.textView6);
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
