package com.dicoding.asclepius.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class HistoryViewModelFactory private constructor(
    private val mApplication: Application
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(mApplication) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var INSTANCE: HistoryViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): HistoryViewModelFactory {
            if (INSTANCE == null) {
                synchronized(HistoryViewModelFactory::class.java) {
                    INSTANCE = HistoryViewModelFactory(
                        context.applicationContext as Application
                    )
                }
            }
            return INSTANCE as HistoryViewModelFactory
        }
    }
}