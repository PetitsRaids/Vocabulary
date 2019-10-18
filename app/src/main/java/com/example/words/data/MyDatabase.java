package com.example.words.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Word.class}, version = 1, exportSchema = false)
public abstract class MyDatabase extends RoomDatabase {
    private static MyDatabase myDatabase;

    public abstract WordsDao wordsDao();

    public synchronized static MyDatabase getInstance(Context context) {
        if (myDatabase == null) {
            myDatabase = Room.databaseBuilder(context, MyDatabase.class, "word_database").build();
        }
        return myDatabase;
    }
}
