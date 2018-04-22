package com.mohamed.spencer.piepoll;

import android.app.Activity;
import android.os.Bundle;

public class TakePollActivity extends Activity {



    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setContentView(R.layout.activity_take_poll);

        /*
            Nice resource for adding checkboxes dynamically to a layout:
            https://stackoverflow.com/questions/7618553/how-to-add-checkboxes-dynamically-in-android
         */
    }

}
