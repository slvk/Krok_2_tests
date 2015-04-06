package com.mycompany.listviewdemo;

import java.util.Random;

/**
 * Created by VIanoshchuk on 11.03.2015.
 */
public class QuestionWithAnswer {
    public String Question;
    public String[] Answers;
    public boolean[] Correctness;

    QuestionWithAnswer(String Question, String[] Answers, boolean[] Correctness){
        this.Question = Question;
        this.Answers = Answers;
        this.Correctness = Correctness;
        shuffleAnswers();
    }
    private void shuffleAnswers(){
        Random rnd = new Random();
        String a;
        Boolean b;
        for (int i = Answers.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Swap answers
            a = Answers[index];
            Answers[index] = Answers[i];
            Answers[i] = a;
            // Swap correctness
            b = Correctness[index];
            Correctness[index] = Correctness[i];
            Correctness[i] = b;
        }
    }
    public int getCorrectAnswerPosition(){
        for (int i = 0; i < Correctness.length; i++)
            if (Correctness[i])
                return i;
        //todo replace with exception that question has no correct answer
        return -1;
    }
}
