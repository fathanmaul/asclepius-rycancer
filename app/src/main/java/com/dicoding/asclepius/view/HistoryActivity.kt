package com.dicoding.asclepius.view

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.R
import com.dicoding.asclepius.adapter.ClassificationHistoryAdapter
import com.dicoding.asclepius.databinding.ActivityHistoryBinding
import com.dicoding.asclepius.viewmodel.HistoryViewModel
import com.dicoding.asclepius.viewmodel.HistoryViewModelFactory

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var historyViewModel: HistoryViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        historyViewModel = obtainViewModel(this@HistoryActivity)

        historyViewModel.getAllHistory().observe(this) {
            if (it.isNotEmpty()) {
                showResult(true)
                binding.rvHistory.layoutManager = LinearLayoutManager(this)
                val adapter = ClassificationHistoryAdapter(historyViewModel)
                adapter.setListClassificationHistory(it)
                binding.rvHistory.adapter = adapter
            } else {
                showToast(getString(R.string.no_result))
                showResult(false)
            }
        }

        setupToolbar(getString(R.string.history))
    }

    private fun setupToolbar(title: String) {
        findViewById<ImageView>(R.id.back).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        findViewById<TextView>(R.id.titleToolbar).text = title
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun obtainViewModel(activity: AppCompatActivity): HistoryViewModel {
        val factory = HistoryViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[HistoryViewModel::class.java]
    }


    private fun showResult(show: Boolean) {
        if (show) {
            binding.llNoHistory.visibility = View.GONE
            binding.rvHistory.visibility = View.VISIBLE
        } else {
            binding.llNoHistory.visibility = View.VISIBLE
            binding.rvHistory.visibility = View.GONE
        }
    }
}