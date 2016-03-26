package app.wordmemo.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import app.wordmemo.helpers.WordDbHelper;
import app.wordmemo.models.Word;
import app.wordmemo.utils.DateUtil;

public class WordDAO {

    private static WordDAO sInstance;

    private SQLiteDatabase wordDatabase;
    private WordDbHelper wordDbHelper;

    private WordDAO(Context context) {
        wordDbHelper = new WordDbHelper(context);
    }

    public static synchronized WordDAO getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new WordDAO(context);
        }
        return sInstance;
    }

    public void open() {
        wordDatabase = wordDbHelper.getWritableDatabase();
    }

    public void close() {
        wordDbHelper.close();
    }

    public boolean insertWord (Word word) {
        ContentValues values = new ContentValues();
        values.put(WordDbHelper.COLUMN_ORIGINAL, word.getOriginal());
        values.put(WordDbHelper.COLUMN_TRANSLATION, word.getTranslation());
        values.put(WordDbHelper.COLUMN_DUEDATE, DateUtil.formatDate(word.getDueDate()));

        long result = wordDatabase.insert(WordDbHelper.TABLE_WORDS, null, values);

        return result != -1;

    }

    public List<Word> fetchDueWords () {
        List<Word> dueWords = new ArrayList<>();

        Cursor cursor = wordDatabase.rawQuery("select * from " + WordDbHelper.TABLE_WORDS +
                " where " + WordDbHelper.COLUMN_DUEDATE + " <= " + DateUtil.getTodayAsInt(), null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            dueWords.add(cursorToWord(cursor));
            cursor.moveToNext();
        }

        cursor.close();
        return dueWords;

    }

    private Word cursorToWord(Cursor cursor) {
        int id = cursor.getInt(0);
        String original = cursor.getString(1);
        String translation = cursor.getString(2);
        Calendar dueDate = DateUtil.formatDate(cursor.getInt(3));

        return new Word (id, original, translation, dueDate);
    }

}
