package com.dicoding.asclepius.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.R
import com.dicoding.asclepius.adapter.ArticleAdapter
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.DateHelper
import com.dicoding.asclepius.helper.floatToPercentage
import com.dicoding.asclepius.model.ClassificationHistory
import com.dicoding.asclepius.utils.Constant.CONFIDENCE_EXTRA
import com.dicoding.asclepius.utils.Constant.IMAGE_URI_EXTRA
import com.dicoding.asclepius.utils.Constant.IS_SAVED_EXTRA
import com.dicoding.asclepius.utils.Constant.LABEL_EXTRA
import com.dicoding.asclepius.viewmodel.ArticleViewModel
import com.dicoding.asclepius.viewmodel.HistoryViewModel
import com.dicoding.asclepius.viewmodel.HistoryViewModelFactory


class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private val articleViewModel by viewModels<ArticleViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO: Menampilkan hasil gambar, prediksi, dan confidence score.
        val labelResult = intent.getStringExtra(LABEL_EXTRA)
        val confidenceResult = intent.getFloatExtra(CONFIDENCE_EXTRA, 0.0f)
        val imageUriResult = intent.getStringExtra(IMAGE_URI_EXTRA)?.toUri()
        val isSaved = intent.getBooleanExtra(IS_SAVED_EXTRA, false)
        binding.apply {
            resultTextLabel.text = labelResult
            resultTextConfidence.text = floatToPercentage(confidenceResult)
            resultImage.setImageURI(imageUriResult)
        }
        val historyViewModelFactory = HistoryViewModelFactory.getInstance(this)
        val historyViewModel: HistoryViewModel by viewModels {
            historyViewModelFactory
        }

        binding.saveButton.setOnClickListener {
            val label = labelResult.toString()
            val confidence = confidenceResult.toString()
            val imageUri = imageUriResult.toString()
            if (saveResult(label, confidence, imageUri, historyViewModel)) {
                successSaveResult()
            } else {
                showToast("Failed to save result.")
            }
        }

        if (isSaved) {
            binding.saveButton.visibility = View.GONE
        } else {
            binding.saveButton.visibility = View.VISIBLE
        }

        setupArticleList()
        setupToolbar(getString(R.string.result))
    }

    private fun setupToolbar(title: String) {
        findViewById<ImageView>(R.id.back).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        findViewById<TextView>(R.id.titleToolbar).text = title
    }

    private fun saveResult(
        label: String,
        confidence: String,
        imageUri: String,
        historyViewModel: HistoryViewModel
    ): Boolean {
        return try {
            val classificationHistory = ClassificationHistory(
                label = label,
                confidence = confidence,
                imageUri = imageUri,
                date = DateHelper.getCurrentDate()
            )
            historyViewModel.insertHistory(classificationHistory)
            true
        } catch (e: Exception) {
            showToast("Error when saving result")
            false
        }
    }

    private fun successSaveResult() {
        showToast("Result saved successfully.")
        binding.saveButton.isEnabled = false
        binding.saveButton.text = getString(R.string.saved)
    }

    private fun setupArticleList() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvArticle.layoutManager = layoutManager

        articleViewModel.listArticle.observe(this) {
            val adapter = ArticleAdapter()
            adapter.submitList(it)
            binding.rvArticle.adapter = adapter
            binding.rvArticle.visibility = View.VISIBLE
        }

        articleViewModel.loading.observe(this) {
            showLoading(it)
        }

    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}