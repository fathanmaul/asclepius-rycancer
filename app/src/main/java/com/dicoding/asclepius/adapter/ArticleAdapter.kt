package com.dicoding.asclepius.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.remote.response.ArticlesItem
import com.dicoding.asclepius.databinding.ItemArticleBinding

class ArticleAdapter :
    ListAdapter<ArticlesItem, ArticleAdapter.ArticleViewHolder>(DIFF_CALLBACK) {
    class ArticleViewHolder(
        private val binding: ItemArticleBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(article: ArticlesItem) {
            with(binding) {
                tvTitle.text = article.title
                tvDescription.text = article.description
                if (article.urlToImage != null) {
                    Glide.with(itemView.context)
                        .load(article.urlToImage)
                        .into(ivArticle)
                } else {
                    Glide.with(itemView.context)
                        .load(R.drawable.ic_place_holder)
                        .into(ivArticle)
                }
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ArticleViewHolder {
        val binding = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = getItem(position)
        holder.bind(article)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ArticlesItem>() {
            override fun areItemsTheSame(
                oldItem: ArticlesItem, newItem: ArticlesItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ArticlesItem, newItem: ArticlesItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}