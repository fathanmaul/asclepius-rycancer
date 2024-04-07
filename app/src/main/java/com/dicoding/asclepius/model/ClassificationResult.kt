package com.dicoding.asclepius.model

import android.net.Uri
import android.os.Parcelable

@kotlinx.parcelize.Parcelize
data class ClassificationResult(
    val imageUri: Uri,
    val label: String,
    val confidence: Float,
    val inferenceTime: Long
) : Parcelable
