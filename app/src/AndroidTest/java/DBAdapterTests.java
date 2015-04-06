import android.database.Cursor;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import com.mycompany.listviewdemo.DatabaseAdapter;

/**
 * Created by VIanoshchuk on 17.03.2015.
 */

public class DBAdapterTests  extends AndroidTestCase {
    private static final String TEST_FILE_PREFIX = "test2_";
    private DatabaseAdapter mMyAdapter;

    @Override
    protected void setUp() throws Exception {
        System.out.println("setUp call");
        super.setUp();

        RenamingDelegatingContext context
                = new RenamingDelegatingContext(getContext(), TEST_FILE_PREFIX);

        mMyAdapter = new DatabaseAdapter(context);
        //mMyAdapter.open();

    }

    @Override
    protected void tearDown() throws Exception {
        System.out.println("tearDown call");
        super.tearDown();

        mMyAdapter.close();
        System.out.println("DB closed");
        mMyAdapter = null;
    }

    public void testPreConditions() {
        System.out.println("testPreConditions");
        assertNotNull(mMyAdapter);
    }

    public void testInsertAndGetData(){
        long SubjId;

        Cursor c = mMyAdapter.getAllRowsSubjects();
        int count_before = c.getCount();
        c.close();

/*        SubjId = mMyAdapter.insertSubject("test subject1");

        c = mMyAdapter.getAllRowsSubjects();
        assertEquals(count_before + 1, c.getCount()); // check that "test subject1" was inserted
        c.close();

        long QuestId;
        QuestId = mMyAdapter.insertQuestion("test question", SubjId, 1);
        assertNotNull(QuestId);

        mMyAdapter.insertAnswer(QuestId, "test answer1", 0);
        mMyAdapter.insertAnswer(QuestId, "test answer2", 1);
        mMyAdapter.insertAnswer(QuestId, "test answer3", 0);
        mMyAdapter.insertAnswer(QuestId, "test answer4", 0);
        mMyAdapter.insertAnswer(QuestId, "test answer5", 0);
        mMyAdapter.insertAnswer(QuestId, "", 0); // should not be inserted
        mMyAdapter.insertAnswer(QuestId, "-", 0);// should not be inserted

        mMyAdapter.insertQuestion("test question1", SubjId, 2);
        mMyAdapter.insertQuestion("test question2", SubjId, 3);
        int cntQuests = mMyAdapter.getCountQuestionsInSubject(SubjId);
        assertEquals(3, cntQuests);

        QuestionWithAnswer qa = mMyAdapter.getQuestionBySN((int)SubjId, 1);
        assertNotNull(qa);
        assertNotNull(qa.Question);
        assertEquals(qa.Answers.length, 5);
        //array with answers is shuffled so CorrectAnswerPosition should be between 0..4
        int CorrectAnswerPosition = qa.getCorrectAnswerPosition();
        assertTrue(0 <= CorrectAnswerPosition && CorrectAnswerPosition <= 4);
        assertEquals("test answer2", qa.Answers[CorrectAnswerPosition]);
*/

    }




}
