package com.petits_raids.words.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WordsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Word word);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Word> words);

    @Query("SELECT * FROM word ORDER BY id DESC")
    LiveData<List<Word>> getAllWords();

    @Query("DELETE FROM word")
    void deleteAll();

    @Query("SELECT * FROM word WHERE eng LIKE :s ORDER BY id DESC")
    LiveData<List<Word>> queryWordByEng(String s);

    @Query("DELETE FROM word WHERE id = :wordId")
    void deleteWord(int wordId);
}
