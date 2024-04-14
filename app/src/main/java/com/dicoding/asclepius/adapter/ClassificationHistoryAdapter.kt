package com.dicoding.asclepius.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ItemHistoryBinding
import com.dicoding.asclepius.model.ClassificationHistory
import com.dicoding.asclepius.utils.Constant
import com.dicoding.asclepius.view.ResultActivity
import com.dicoding.asclepius.viewmodel.HistoryViewModel
import java.io.File


class ClassificationHistoryAdapter(private val historyViewModel: HistoryViewModel) :
    RecyclerView.Adapter<ClassificationHistoryAdapter.ViewHolder>() {

    private val listClassificationHistory = ArrayList<ClassificationHistory>()

    class ViewHolder(
        private val binding: ItemHistoryBinding,
        private val historyViewModel: HistoryViewModel
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(classificationHistory: ClassificationHistory) {
            with(binding) {
                tvDate.text = classificationHistory.date
                tvLabel.text = classificationHistory.label
                cvHistory.setOnClickListener {
                    val intent = Intent(it.context, ResultActivity::class.java).apply {
                        putExtra(Constant.LABEL_EXTRA, classificationHistory.label)
                        putExtra("CONFIDENCE_EXTRA", classificationHistory.confidence.toFloat())
                        putExtra("IMAGE_URI_EXTRA", classificationHistory.imageUri)
                        putExtra("IS_SAVED_EXTRA", true)
                    }
                    it.context.startActivity(intent)
                }
                ivDelete.setOnClickListener {
                    val builder = androidx.appcompat.app.AlertDialog.Builder(it.context)
                    builder.setTitle(it.context.getString(R.string.delete))
                    builder.setMessage(it.context.getString(R.string.delete_message))
                    builder.setPositiveButton(it.context.getString(R.string.yes)) { _, _ ->
                        if (File(classificationHistory.imageUri).exists()) {
                            File(classificationHistory.imageUri).delete()
                        }
                        historyViewModel.deleteHistory(classificationHistory)
                    }
                    builder.setNegativeButton(it.context.getString(R.string.cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    builder.show()
                }
            }
        }
    }

    fun setListClassificationHistory(listClassificationHistory: List<ClassificationHistory>) {
        this.listClassificationHistory.clear()
        this.listClassificationHistory.addAll(listClassificationHistory)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, historyViewModel)
    }

    override fun getItemCount(): Int = listClassificationHistory.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listClassificationHistory[position]
        holder.bind(item)
    }


}