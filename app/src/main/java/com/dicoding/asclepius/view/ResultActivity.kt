package com.dicoding.asclepius.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.model.ClassificationResult

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // TODO: Menampilkan hasil gambar, prediksi, dan confidence score.
        val result = intent.getParcelableExtra<ClassificationResult>(EXTRA_RESULT)
        binding.apply {
            result?.let {
                resultImage.setImageURI(result.imageUri)
                resultTextLabel.text = result.label
                resultTextConfidence.text = result.confidence.toString()
            }
        }


    }

    companion object {
        const val EXTRA_RESULT = "extra_result"
    }


}