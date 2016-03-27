package app.wordmemo.activities;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

import app.wordmemo.R;
import app.wordmemo.fragments.PracticeDoneFragment;
import app.wordmemo.fragments.PracticeInputFragment;
import app.wordmemo.fragments.PracticeResultFragment;
import app.wordmemo.managers.WordDAO;
import app.wordmemo.models.Word;

public class PracticeActivity extends AppCompatActivity
        implements PracticeInputFragment.OnSubmitUserTranslationListener,
        PracticeResultFragment.ShowNextDueWord {

    private List<Word> dueWords;
    private Word currentWord;
    private WordDAO wd = WordDAO.getInstance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);

        wd.open();
        dueWords = wd.fetchDueWords();
        wd.close();

        if (dueWords.size() == 0) {
            // TODO: Insert Fragment that shows "no more words today"
            return;
        }

        currentWord = dueWords.get(new Random().nextInt(dueWords.size()));

        PracticeInputFragment practiceInputFragment = PracticeInputFragment.getInstance(currentWord);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.practice_container, practiceInputFragment);
        fragmentTransaction.commit();
    }

    /**
     *  Called from PracticeInputFragment
     */
    @Override
    public void onSubmitUserTranslation(boolean isTranslationCorrect) {
        // remove and updating the currentWord
        dueWords.remove(currentWord);
        currentWord.updateLearnGroup(isTranslationCorrect);

        wd.open();
        wd.updateWord(currentWord);
        wd.close();

        // open the result fragment
        PracticeResultFragment resultFragment = PracticeResultFragment.getInstance(currentWord, isTranslationCorrect);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.practice_container, resultFragment);
        fragmentTransaction.commit();
    }

    /**
     *  Called from PracticeResultFragment
     */
    @Override
    public void showNextDueWord () {
        if (dueWords.size() == 0) {
            displayDoneFragment();
            return;
        }
        currentWord = dueWords.get(new Random().nextInt(dueWords.size()));

        PracticeInputFragment practiceInputFragment = PracticeInputFragment.getInstance(currentWord);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.practice_container, practiceInputFragment);
        fragmentTransaction.commit();
    }

    private void displayDoneFragment () {
        PracticeDoneFragment practiceDoneFragment = new PracticeDoneFragment(); // new because no args passed on

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.practice_container, practiceDoneFragment);
        fragmentTransaction.commit();
    }
}
