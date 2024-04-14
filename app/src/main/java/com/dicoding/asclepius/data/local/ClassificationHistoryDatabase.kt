package com.dicoding.asclepius.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.asclepius.model.ClassificationHistory

@Database(entities = [ClassificationHistory::class], version = 1)
abstract class ClassificationHistoryDatabase : RoomDatabase() {
    abstract fun classificationHistoryDao(): ClassificationHistoryDao
    companion object{
        @Volatile
        private var INSTANCE: ClassificationHistoryDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): ClassificationHistoryDatabase {
            if (INSTANCE == null) {
                synchronized(ClassificationHistoryDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        ClassificationHistoryDatabase::class.java, "note_database")
                        .build()
                }
            }
            return INSTANCE as ClassificationHistoryDatabase
        }
    }


}
