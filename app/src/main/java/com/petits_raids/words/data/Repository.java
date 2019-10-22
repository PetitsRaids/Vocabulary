package com.petits_raids.words.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Repository {

    private MyDatabase database;
    private Executor executor;
    private static Repository repository;

    public synchronized static Repository getInstance(Context context) {
        if (repository == null) {
            repository = new Repository(context);
        }
        return repository;
    }

    private Repository(Context context) {
        database = MyDatabase.getInstance(context);
        executor = Executors.newSingleThreadExecutor();
    }

    public void insertWord(final Word word) {
        executor.execute(() -> database.wordsDao().insert(word));
    }

    public void insertAll(List<Word> words) {
        executor.execute(() -> database.wordsDao().insertAll(words));
    }

    public void deleteAll() {
        executor.execute(() -> database.wordsDao().deleteAll());
    }

    public LiveData<List<Word>> getAllWords() {
        return database.wordsDao().getAllWords();
    }

    public void updateWord(Word word) {
        executor.execute(() -> database.wordsDao().insert(word));
    }

    public LiveData<List<Word>> queryWord(String s) {
        return database.wordsDao().queryWordByEng(s);
    }

    public void deleteWord(Word word) {
        executor.execute(() -> database.wordsDao().deleteWord(word.getId()));
    }
}
