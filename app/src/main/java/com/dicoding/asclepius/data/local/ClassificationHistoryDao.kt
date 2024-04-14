package com.dicoding.asclepius.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.dicoding.asclepius.model.ClassificationHistory

@Dao
interface ClassificationHistoryDao {

    @Insert
    fun insertClassificationHistory(classificationHistory: ClassificationHistory)

    @Delete
    fun deleteClassificationHistory(classificationHistory: ClassificationHistory)

    @Query("SELECT * FROM ClassificationHistory ORDER BY id DESC")
    fun getAllClassificationHistory(): LiveData<List<ClassificationHistory>>
}