package com.dicoding.asclepius.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.dicoding.asclepius.data.local.ClassificationHistoryDao
import com.dicoding.asclepius.data.local.ClassificationHistoryDatabase
import com.dicoding.asclepius.model.ClassificationHistory
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ClassificationHistoryRepository(
    application: Application,
) {
    private val classificationHistoryDao: ClassificationHistoryDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = ClassificationHistoryDatabase.getDatabase(application)
        classificationHistoryDao = db.classificationHistoryDao()
    }

    fun getAllHistory(): LiveData<List<ClassificationHistory>> =
        classificationHistoryDao.getAllClassificationHistory()

    fun insert(classificationHistory: ClassificationHistory) {
        executorService.execute {
            classificationHistoryDao.insertClassificationHistory(classificationHistory)
        }
    }

    fun delete(classificationHistory: ClassificationHistory) {
        executorService.execute {
            classificationHistoryDao.deleteClassificationHistory(classificationHistory)
        }
    }
}