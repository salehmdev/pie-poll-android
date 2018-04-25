package com.mohamed.spencer.piepoll;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class HomeActivity extends Activity implements View.OnClickListener {

    public Button createPoll;
    public Button viewPolls;
    public SharedPreferences prefs;
    public SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = prefs.edit();

        createPoll = (Button) findViewById(R.id.button_create_poll);
        viewPolls = (Button) findViewById(R.id.button_explore_polls);

        createPoll.setOnClickListener(this);
        viewPolls.setOnClickListener(this);
    }

    /*
        onPrepareOptionsMenu() is called right before the onCreateOptionsMenu() which allows for dynamically
        enabling/disabling of button on a screen. Since they can't be changed once the menu is created,
        this is a efficient place to do it. Do this by modifying the Menu object.
    */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        String username = prefs.getString("username", "");
        menu.findItem(R.id.menu_item_username).setTitle(username);

        // If logged in
        if(isLoggedIn)
        {
            menu.findItem(R.id.menu_item_login).setEnabled(false);
            menu.findItem(R.id.menu_item_login).setVisible(false);
            menu.findItem(R.id.menu_item_logout).setEnabled(true);
            menu.findItem(R.id.menu_item_logout).setVisible(true);
        }else{
            menu.findItem(R.id.menu_item_logout).setEnabled(false);
            menu.findItem(R.id.menu_item_logout).setVisible(false);
            menu.findItem(R.id.menu_item_login).setEnabled(true);
            menu.findItem(R.id.menu_item_login).setVisible(true);
        }

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
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.menu_item_logout:
                // TODO: Log the user out (remove login key from shared prefs?)
                editor.putBoolean("isLoggedIn", false);
                editor.commit();
                // finish() causes the activity to end its life cycle, which means user cannot press back button to it
                // Sends user back to MainActivity
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch(view.getId())
        {
            case R.id.button_create_poll:
                intent = new Intent(this, CreatePollActivity.class);
                startActivity(intent);
                break;
            case R.id.button_explore_polls:
                intent = new Intent(this, ExplorePollsActivity.class);
                startActivity(intent);
                break;
        }
    }
}
