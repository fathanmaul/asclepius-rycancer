package com.dicoding.asclepius.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class ClassificationHistory(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,


    @ColumnInfo(name = "label") val label: String,

    @ColumnInfo(name = "confidence") val confidence: String,

    @ColumnInfo(name = "imageUri") val imageUri: String,

    @ColumnInfo(name = "date") val date: String

) : Parcelable
