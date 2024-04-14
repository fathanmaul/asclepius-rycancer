package com.dicoding.asclepius.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.model.ClassificationHistory
import com.dicoding.asclepius.repository.ClassificationHistoryRepository
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : ViewModel() {
    private val mClassificationHistoryRepository: ClassificationHistoryRepository =
        ClassificationHistoryRepository(application)

    fun getAllHistory() = mClassificationHistoryRepository.getAllHistory()

    fun insertHistory(classificationHistory: ClassificationHistory) {
        viewModelScope.launch {
            mClassificationHistoryRepository.insert(classificationHistory)
        }
    }

    fun deleteHistory(classificationHistory: ClassificationHistory) {
        viewModelScope.launch {
            mClassificationHistoryRepository.delete(classificationHistory)
        }
    }

}