package com.dicoding.asclepius.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.utils.Constant.LABEL_EXTRA
import com.dicoding.asclepius.utils.Constant.IMAGE_URI_EXTRA
import com.dicoding.asclepius.utils.Constant.CONFIDENCE_EXTRA
import com.dicoding.asclepius.utils.Constant.DELAY_RESULT
import com.dicoding.asclepius.utils.Constant.INFERENCE_TIME_EXTRA
import com.dicoding.asclepius.utils.Constant.PROGRESS_INDICATOR
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.tensorflow.lite.task.vision.classifier.Classifications

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentImageUri: Uri? = null
    private var imageClassifierHelper: ImageClassifierHelper? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        with(binding) {
            galleryButton.setOnClickListener {
                startGallery()
            }
            analyzeButton.setOnClickListener {
                if (currentImageUri != null) {
                    analyzeImage(currentImageUri!!)
                } else {
                    showToast("Please select an image first.")
                }
            }
            historyButton.setOnClickListener {
                startActivity(Intent(this@MainActivity, HistoryActivity::class.java))
            }
        }
        showProgressIndicator(false)
    }


    private fun startGallery() {
        binding.galleryButton.isEnabled = false
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            launchUCrop(uri)
        } else {
            showToast("Failed to get image from gallery.")
            binding.galleryButton.isEnabled = true
        }
    }

    private fun createTempFile(): java.io.File {
        val tempFileName = "temp_${System.currentTimeMillis()}"
        return java.io.File.createTempFile(tempFileName, ".jpg", cacheDir)
    }

    private fun launchUCrop(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(createTempFile())
        val uCrop = UCrop.of(sourceUri, destinationUri).withAspectRatio(1f, 1f)
        uCrop.getIntent(this).let {
            uCropLauncher.launch(it)
        }
    }

    private val uCropLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val resultUri = UCrop.getOutput(result.data!!)
                if (resultUri != null) {
                    currentImageUri = resultUri
                    showImage()
                    binding.galleryButton.isEnabled = true
                }
            } else if (result.resultCode == UCrop.RESULT_ERROR) {
                showToast("Something went error!")
            }
        }

    private fun showImage() {
        currentImageUri?.let {
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun analyzeImage(imageUri: Uri) {
        Toast.makeText(this, "Analyzing image...", Toast.LENGTH_SHORT).show()
        try {
            imageClassifierHelper = ImageClassifierHelper(context = this,
                classifierListener = object : ImageClassifierHelper.ClassifierListener {
                    override fun onError(error: String) {
                        Toast.makeText(this@MainActivity, "Something went error!", Toast.LENGTH_SHORT).show()
                    }

                    override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                        results?.let { listClassification ->
                            val topResult = listClassification[0]
                            val result = topResult.categories[0].label
                            val confidence = topResult.categories[0].score
                            showToast("Done Analyzing Image!")
                            moveToResult(imageUri.toString(), result, confidence, inferenceTime)
                        }
                    }
                })
        } catch (e: Exception) {
            showToast("Error: ${e.message}")
        }

        imageClassifierHelper?.classifyStaticImage(imageUri)
    }


    private fun moveToResult(
        currentImageUri: String, label: String, confidence: Float, inferenceTime: Long
    ) {
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra(LABEL_EXTRA, label)
            putExtra(CONFIDENCE_EXTRA, confidence)
            putExtra(INFERENCE_TIME_EXTRA, inferenceTime)
            putExtra(IMAGE_URI_EXTRA, currentImageUri)
        }
        CoroutineScope(Dispatchers.Main).launch {
            showProgressIndicator(true)
            delay(DELAY_RESULT)
            showProgressIndicator(false)
            binding.previewImageView.setImageResource(R.drawable.ic_place_holder)
            this@MainActivity.currentImageUri = null
            startActivity(intent)
        }


    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showProgressIndicator(show: Boolean) {
        binding.progressIndicator.progress = PROGRESS_INDICATOR
        if (show) {
            binding.progressIndicator.visibility = View.VISIBLE
        } else {
            binding.progressIndicator.visibility = View.GONE
        }
    }
}