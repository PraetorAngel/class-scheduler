package com.ogborn.c196final.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.ogborn.c196final.model.Course;

import java.util.List;

@Dao
public interface CourseDao {

    @Insert
    long insert(Course course);

    @Update
    void update(Course course);

    @Delete
    void delete(Course course);

    @Query("SELECT * FROM courses WHERE id = :id LIMIT 1")
    Course getCourseById(int id);

    @Query("SELECT * FROM courses WHERE termId = :termId")
    List<Course> getCoursesForTerm(int termId);
}