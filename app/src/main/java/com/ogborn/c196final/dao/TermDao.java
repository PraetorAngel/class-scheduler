package com.ogborn.c196final.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.ogborn.c196final.model.Term;

import java.util.List;

@Dao
public interface TermDao {

    @Insert
    long insert(Term term);

    @Update
    void update(Term term);

    @Delete
    void delete(Term term);

    @Query("SELECT * FROM terms")
    List<Term> getAllTerms();

    @Query("SELECT * FROM terms WHERE id = :id LIMIT 1")
    Term getTermById(int id);
}