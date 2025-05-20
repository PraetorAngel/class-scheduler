package com.ogborn.c196final.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.ogborn.c196final.model.Assessment;

import java.util.List;

@Dao
public interface AssessmentDao {

    @Insert
    long insert(Assessment assessment);

    @Update
    void update(Assessment assessment);

    @Delete
    void delete(Assessment assessment);

    @Query("SELECT * FROM assessments WHERE id = :id LIMIT 1")
    Assessment getAssessmentById(int id);

    @Query("SELECT * FROM assessments WHERE courseId = :courseId")
    List<Assessment> getAssessmentsForCourse(int courseId);

    @Query("DELETE FROM assessments WHERE courseId = :courseId")
    void deleteAllForCourse(int courseId);
}