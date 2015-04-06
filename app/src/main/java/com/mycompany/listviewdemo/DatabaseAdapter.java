/**
 * Created by VIanoshchuk on 05.03.2015.
 */


package com.mycompany.listviewdemo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

public class DatabaseAdapter  extends SQLiteOpenHelper{
    private static final String TAG = "DBAdapter";

    // Subjects Fields
    public static final String SUBJ_ID = "_id";
    public static final String SUBJ_NAME = "name";
    // Questions Fields
    public static final String QUEST_ID = "_id";
    public static final String QUEST_TEXT = "q_text";
    public static final String QUEST_SUBJ_ID = "subj_id";
    public static final String QUEST_SERNO = "ser_num";
    // Questions Answers
    public static final String ANSW_ID = "_id";
    public static final String ANSW_TEXT = "a_text";
    public static final String ANSW_QUEST_ID = "quest_id";
    public static final String ANSW_IS_CORRECT = "is_correct";

    // TODO: Setup your field numbers here (0 = KEY_ROWID, 1=...)

    public static final String[] SUBJ_COLUMNS = new String[] {SUBJ_ID, SUBJ_NAME};

    public static final String DATABASE_NAME = "MyDb";
    public static final String DATABASE_PATH = "/data/data/com.mycompany.listviewdemo/databases/";// todo: replace with automatic filling from code

    public static final String TAB_SUBJS = "Subjects";
    public static final String TAB_QUESTIONS = "Questions";
    public static final String TAB_ANSWERS = "Answers";

   //public static final int DATABASE_VERSION = 31;

    private static final String get_quest_query = String.format("select %s,%s from %s where %s = ? and %s = ?", QUEST_TEXT, QUEST_ID,TAB_QUESTIONS, QUEST_SUBJ_ID, QUEST_SERNO);
    private static final String get_answers_query = String.format("select %s, %s from %s where %s = ?", ANSW_TEXT,ANSW_IS_CORRECT, TAB_ANSWERS, ANSW_QUEST_ID);
    private static final String get_count_quests_query = String.format("select count(*) from %s where %s = ?", TAB_QUESTIONS, QUEST_SUBJ_ID);
    private final Context context;

    private SQLiteDatabase db;

    /////////////////////////////////////////////////////////////////////
    //	Public methods:
    /////////////////////////////////////////////////////////////////////

    public DatabaseAdapter(Context ctx) {
        super(ctx, DATABASE_NAME, null, 1);
        this.context = ctx;
    }

    // Open the database connection.
/*    public DatabaseAdapter open() {
        db = myDBHelper.getReadableDatabase();
        //Log.v(TAG, "getWritableDatabase() " + myDBHelper.getDatabaseName());
        return this;
    }
*/
    public Cursor getAllRowsSubjects() {
        String where = null;
        Cursor c = 	db.query(true, TAB_SUBJS, SUBJ_COLUMNS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }
    //----------------------------------------------------------------------------------------------
    //Alternative create database
    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException {

        if(checkDataBase()){
            //do nothing - database already exist
        }else{

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
           this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {
                Log.v(TAG, "File not copied");
                throw new Error("Error copying database");
            }
        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DATABASE_PATH + DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){
            Log.v(TAG, "DB so far does not exist");
        }

        if(checkDB != null){

            Log.v(TAG, "DB exists. closing");
            checkDB.close();
        }

        return checkDB != null;
    }

    private void copyDataBase() throws IOException{

        //Open your local db as the input stream
        InputStream myInput = context.getAssets().open(DATABASE_NAME);

        // Path to the just created empty db
        String outFileName = DATABASE_PATH + DATABASE_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = DATABASE_PATH + DATABASE_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if(db != null)
            db.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase _db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {

    }

    //----------------------------------------------------------------------------------------------
    public QuestionWithAnswer getQuestionBySN(int subject_id, int serno){

        Cursor q_cursor;
        String question_text = "";
        String question_id = "";

        q_cursor = db.rawQuery(get_quest_query, new String[] {String.valueOf(subject_id), String.valueOf(serno)});

        try {
            if (q_cursor.getCount() > 0) {
                q_cursor.moveToFirst();
                question_text = q_cursor.getString(q_cursor.getColumnIndex(QUEST_TEXT));
                question_id = q_cursor.getString(q_cursor.getColumnIndex(QUEST_ID));
            }
        }
        finally{
            q_cursor.close();
        }

        q_cursor = db.rawQuery(get_answers_query, new String[] {question_id});
        String[] question_answers = new String[q_cursor.getCount()];
        boolean[] answer_correctness = new boolean[q_cursor.getCount()];

        if (q_cursor.moveToFirst()) {
            int i = 0;
            do {
                question_answers[i] = q_cursor.getString(q_cursor.getColumnIndex(ANSW_TEXT));
                answer_correctness[i] = (q_cursor.getInt(q_cursor.getColumnIndex(ANSW_IS_CORRECT)) == 1);
                i++;
            } while (q_cursor.moveToNext());
        }

        q_cursor.close();

        QuestionWithAnswer quest_with_answer = new QuestionWithAnswer(question_text, question_answers, answer_correctness);
        return quest_with_answer;
    }

    public int getCountQuestionsInSubject(long subject_id){
        Cursor cursor_cnt;
        int QuestCount;
        cursor_cnt = db.rawQuery(get_count_quests_query, new String[] {String.valueOf(subject_id)});
        cursor_cnt.moveToFirst();
        QuestCount = cursor_cnt.getInt(0);
        cursor_cnt.close();
        return QuestCount;
    }

}
