package com.mohamed.spencer.piepoll;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;

public class HomeActivity extends Activity {

    public Button createPoll;
    public Button viewPolls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

    }

    /*
        onPrepareOptionsMenu() is called right before the onCreateOptionsMenu() which allows for dynamically
        enabling/disabling of button on a screen. Since they can't be changed once the menu is created,
        this is a efficient place to do it. Do this by modifying the Menu object.
    */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        // TODO: Check (SharedPrefs?) if user is logged in, then disable login or logout button from dropdown menu

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Sets up the menu at the top of this screen. Indicated in manifest to show a title-bar for this activity
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dropdown_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_item_login:
                // TODO: Take user to MainActivity
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_item_logout:
                // TODO: Log the user out

                // finish() causes the activity to end its life cycle, which means user cannot press back button to it
                // Sends user back to MainActivity
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
