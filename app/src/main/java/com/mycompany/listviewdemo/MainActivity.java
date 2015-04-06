package com.mycompany.listviewdemo;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.sql.SQLException;

public class MainActivity extends ActionBarActivity {

    DatabaseAdapter myDb;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
            System.exit(0);
        }
       // Log.v("MainActivity", "OnCreate event");
        openDB();
        //Log.v("MainActivity", "Database opened");

        //Log.v("MainActivity", "Table cleared");

        // for the moment data is inserted here
        //but it should be loaded by special loader

        //Log.v("MainActivity", "Rows inserted");
        final Cursor cursor = myDb.getAllRowsSubjects();

        CustomCursorAdapter myAdapter = new CustomCursorAdapter(this, cursor);
        ListView lvSubjsList = (ListView)findViewById(R.id.list);
        lvSubjsList.setAdapter(myAdapter);

        lvSubjsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                TextView textViewSubj = (TextView) view.findViewById(R.id.name);
                String SubjSelectedFromList = textViewSubj.getText().toString();
                TextView textViewSubjId = (TextView) view.findViewById(R.id.serNo);
                String SubjIdFromList = textViewSubjId.getText().toString();

                //Toast.makeText(getApplicationContext(), selectedFromList/*"some text"*/, Toast.LENGTH_SHORT).show();
                Intent quest_activity_intent = new Intent(MainActivity.this, QuestionActivity.class);
                quest_activity_intent.putExtra("subj", SubjSelectedFromList);
                quest_activity_intent.putExtra("subj_id", Integer.valueOf(SubjIdFromList));
                closeDB();
                MainActivity.this.startActivity(quest_activity_intent);
                //TextView header = (TextView) findViewById(R.id.edit_message);
            }

        });
    }

    private void openDB() {
        myDb = new DatabaseAdapter(this);

        try {
            myDb.createDataBase();
            Log.v(TAG, "DB created OK");
        } catch (IOException ioe) {
            Log.v(TAG, "Unable to create database");
            throw new Error("Unable to create database");
        }

        try {
            myDb.openDataBase();
            Log.v(TAG, "DB opened OK");
        }catch(SQLException sqle){
            Log.v(TAG, "Unable to open database");
            throw new Error("Unable to open database");
        }
    }

    private void closeDB() {
        myDb.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            View messageView = getLayoutInflater().inflate(R.layout.about_window, null, false);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.app_name);
            builder.setView(messageView);
            builder.create();
            builder.show();
            return true;
        }
        else if (id ==R.id.action_exit){
            finish();
            System.exit(0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
