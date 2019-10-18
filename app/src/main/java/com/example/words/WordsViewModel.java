package com.example.words;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.words.data.Repository;
import com.example.words.data.Word;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.List;

public class WordsViewModel extends AndroidViewModel {
    private Repository repository;

    public WordsViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(application.getApplicationContext());
    }

    public void insertWord(Word word) {
        repository.insertWord(word);
    }

    public void insterAll(List<Word> words) {
        repository.insertAll(words);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public LiveData<List<Word>> getWordList() {
        return repository.getAllWords();
    }

    public void updateWord(Word showMeaning) {
        repository.updateWord(showMeaning);
    }

    public LiveData<List<Word>> queryWord(@NotNull String s) {
        return repository.queryWord(s);
    }

    public void deleteWord(@NotNull Word word) {
        repository.deleteWord(word);
    }
}
