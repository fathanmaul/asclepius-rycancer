package com.dicoding.asclepius.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.model.ClassificationResult
import com.yalantis.ucrop.UCrop
import org.tensorflow.lite.task.vision.classifier.Classifications

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentImageUri: Uri? = null
    private var imageClassifierHelper: ImageClassifierHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.galleryButton.setOnClickListener {
            startGallery()
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            launchUCrop(uri)
        } else {
            showToast("Gagal mengambil gambar dari Gallery.")
        }
    }

    private fun createTempFile(): java.io.File {
        val tempFileName = "temp_${System.currentTimeMillis()}"
        return java.io.File.createTempFile(tempFileName, ".jpg", cacheDir)
    }

    private fun launchUCrop(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(createTempFile())
        val uCrop =
            UCrop.of(sourceUri, destinationUri).withAspectRatio(1f, 1f)
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
                    analyzeImage(resultUri)
                }
            } else if (result.resultCode == UCrop.RESULT_ERROR) {
                val error = UCrop.getError(result.data!!)
                showToast("Error: ${error?.localizedMessage}")
            }
        }


    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun analyzeImage(imageUri: Uri) {
        Toast.makeText(this, "Analyzing image...", Toast.LENGTH_SHORT).show()
        try {
            imageClassifierHelper = ImageClassifierHelper(
                context = this,
                classifierListener = object : ImageClassifierHelper.ClassifierListener {
                    override fun onError(error: String) {
                        Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
                    }

                    override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                        results?.let { listClassification ->
                            val topResult = listClassification[0]
                            val result = topResult.categories[0].label
                            val confidence = topResult.categories[0].score
                            val inferenceTimeInSecond = inferenceTime / 1000.0
                            showToast("Result: $result, Confidence: $confidence, Inference Time: $inferenceTimeInSecond s")
                            Log.i(
                                "Image Classification",
                                "Result: $result, Confidence: $confidence, Inference Time: $inferenceTimeInSecond s"
                            )
                            moveToResult(imageUri, result, confidence, inferenceTime)
                        }
                    }
                }
            )
        } catch (e: Exception) {
            showToast("Error: ${e.message}")
        }

        imageClassifierHelper?.classifyStaticImage(imageUri)
    }

    private fun moveToResult(
        currentImageUri: Uri,
        label: String,
        confidence: Float,
        inferenceTime: Long
    ) {
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra(
                ResultActivity.EXTRA_RESULT,
                ClassificationResult(
                    currentImageUri,
                    label,
                    confidence,
                    inferenceTime
                )
            )
        }
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}