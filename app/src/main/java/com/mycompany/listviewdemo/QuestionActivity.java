package com.mycompany.listviewdemo;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;


public class QuestionActivity extends ActionBarActivity {

    final String TAG = "QuestActivity";
    DatabaseAdapter myDb;
    ArrayList<Integer> QuestionList;
    final int END_LIST_INDICATOR = -1;
    Random QuestNumberGenerator = new Random();
    int questions_subj_id;
    private QuestionWithAnswer quest;
    private String questions_subj;
    private int QuestionsCount;
    private int CorrectAnswersCount = 0;
    private long StartTime;
    private ProgressBar pbAnswerProgress;

    //------------------
    ListView AnswLV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        Intent intent = getIntent();

        questions_subj = intent.getStringExtra("subj");

        questions_subj_id = intent.getIntExtra("subj_id", 0);

       // TextView QuestTV = (TextView) findViewById(R.id.question);
       // QuestTV.setMovementMethod(new ScrollingMovementMethod()); // needed to make question scrollable

        StartTime = System.nanoTime();

        getSupportActionBar().setTitle(questions_subj);
        openDB();

        //Log.v(TAG, "Before init");
        InitQuestionsList(questions_subj_id);

        displayNextQuestion(questions_subj_id);

        final Button button = (Button) findViewById(R.id.nextbutton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                displayNextQuestion(questions_subj_id);
            }
        });

        AnswLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                Log.v(TAG,"position = "+position);
                // Header is the 0th element so correct answer is on 1 position below
                Button NextQuestButton = (Button) findViewById(R.id.nextbutton);

                if (!NextQuestButton.isEnabled()) {

                    NextQuestButton.setEnabled(true);
                    LinearLayout rlUserAnswer = (LinearLayout)parent.getChildAt(position).findViewById(R.id.ans_row_layout);
                    int highlight_color;

                    if (quest.Correctness[position - 1]) {
                        highlight_color = android.R.color.holo_green_light; // correct answer
                        CorrectAnswersCount++;

                    }
                    else {
                        highlight_color = android.R.color.holo_red_light; // incorrect answer
                        final int CorrectAnswerPosition = quest.getCorrectAnswerPosition();
                        //Log.v(TAG, "AnswLV.getFirstVisiblePosition() = "+AnswLV.getFirstVisiblePosition());
                        //Log.v(TAG, "AnswLV.getLastVisiblePosition() = "+AnswLV.getLastVisiblePosition());
                        //Log.v(TAG, "CorrectAnswerPosition + 1 = "+ (CorrectAnswerPosition + 1));
                        if ((AnswLV.getFirstVisiblePosition() <= (CorrectAnswerPosition + 1)) && // answer is visible on the screen
                                ((CorrectAnswerPosition + 1)) <= AnswLV.getLastVisiblePosition()) {
                            LinearLayout rlCorrectAnsw = (LinearLayout) parent.getChildAt(CorrectAnswerPosition + 1).findViewById(R.id.ans_row_layout);
                            rlCorrectAnsw.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                        }else{// answer is invisible. Position to correct answer, run highlight action in separate thread
                            AnswLV.setSelection(CorrectAnswerPosition + 1);
                            final AdapterView<?> LocalParent = parent;

                            AnswLV.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    LinearLayout rlCorrectAnsw = (LinearLayout) LocalParent.getChildAt(CorrectAnswerPosition + 1).findViewById(R.id.ans_row_layout);
                                    rlCorrectAnsw.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                                }
                            }, 200);

                        }

                    }
                    rlUserAnswer.setBackgroundColor(getResources().getColor(highlight_color));
                    pbAnswerProgress.setProgress(QuestionsCount - QuestionList.size());
                }
            }

        });
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
        // as you specify a parent
        // activity in AndroidManifest.xml.
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
        else if (id == R.id.action_exit){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    private void openDB() {
        myDb = new DatabaseAdapter(this);

        try {
            myDb.createDataBase();

        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        try {
            myDb.openDataBase();
        }catch(SQLException sqle){
            throw new Error("Unable to open database");
        }
        Log.v(TAG, "DB opened");
    }

    private void closeDB() {
        myDb.close();
    }

    private void displayNextQuestion(int subject_id){
        Button NextQuestButton = (Button) findViewById(R.id.nextbutton);
        NextQuestButton.setEnabled(false);
        if(QuestionList.size() == 1)
            NextQuestButton.setText(R.string.statistics_label);
        int NextQuestionSN = GetRandomQuestionNumber();
       // TextView QuestTV = (TextView) findViewById(R.id.question);


        if (NextQuestionSN == END_LIST_INDICATOR) {
            Intent quest_activity_intent = new Intent(QuestionActivity.this, StatisticsActivity.class);
            quest_activity_intent.putExtra("subj", questions_subj);
            quest_activity_intent.putExtra("QuestionsCount", QuestionsCount);
            quest_activity_intent.putExtra("CorrectAnswersCount", CorrectAnswersCount);
            quest_activity_intent.putExtra("TimeElapsed", System.nanoTime()- StartTime);
            closeDB();
            this.startActivity(quest_activity_intent);

        } else {
            Log.v(TAG, "subject_id = " + subject_id);
            Log.v(TAG, "NextQuestionSN = " + NextQuestionSN);

            quest = myDb.getQuestionBySN(subject_id, NextQuestionSN);
            TextView tvQuest = (TextView)findViewById(R.id.tv_quest_id);
            tvQuest.setText(quest.Question);
            ArrayAdapter<String> AnswersLA =
                new ArrayAdapter<String>(this, R.layout.answer_row_layout, R.id.text_answer,quest.Answers);
            AnswLV.setAdapter(AnswersLA);
        }
    }

    private void InitQuestionsList(int SubjId){
        Log.v(TAG, "inside method SubjId = "+SubjId);
        QuestionsCount = myDb.getCountQuestionsInSubject(SubjId);
        pbAnswerProgress = (ProgressBar) findViewById(R.id.AnswerProgress);
        pbAnswerProgress.setMax(QuestionsCount);
        Log.v(TAG, "QuestionsCount = "+QuestionsCount);
        // init 1st place in the list for question
        AnswLV = (ListView) findViewById(R.id.listViewAnswers);

        //Header element for question created
        View header = getLayoutInflater().inflate(R.layout.quest_item, null);
        AnswLV.addHeaderView(header, null, false);


        QuestionList = new ArrayList<Integer>(QuestionsCount);

        for (int i = 0; i < QuestionsCount; i++){
            QuestionList.add(i, i);
        }

    }

    public int GetRandomQuestionNumber(){
        int QuestSerno;

        Log.v(TAG, "QuestionList.size = " + QuestionList.size());
        if (QuestionList.size() == 0)
            QuestSerno = END_LIST_INDICATOR;
        else{
            int QuestIndex = QuestNumberGenerator.nextInt(QuestionList.size());
            Log.v(TAG, "index generated = " + QuestIndex);
            QuestSerno = QuestionList.get(QuestIndex);
            Log.v(TAG, "QuestSerno = " + QuestSerno);
            QuestionList.remove(QuestIndex);
            Log.v(TAG, "Serno removed from list");
            ++QuestSerno; // add 1 because in DB generation starts from 1
        }
        return QuestSerno;
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
/*        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        outState.putString("TEXT", (String)text.getText());  */
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
/*        // TODO Auto-generated method stub
        super.onRestoreInstanceState(savedInstanceState);
        text.setText(savedInstanceState.getString("TEXT"));*/
    }

}
