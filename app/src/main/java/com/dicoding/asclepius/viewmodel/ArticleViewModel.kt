package com.dicoding.asclepius.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.data.remote.response.ArticlesItem
import com.dicoding.asclepius.data.remote.response.NewsResponse
import com.dicoding.asclepius.data.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArticleViewModel : ViewModel() {
    private var _article = MutableLiveData<List<ArticlesItem>>()
    val listArticle: LiveData<List<ArticlesItem>> = _article

    private var _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    init {
        getAllNews()
    }

    private fun getAllNews() {
        _loading.value = true
        val client = ApiConfig.getApiService().getNews()
        client.enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                _loading.value = false
                if (response.isSuccessful) {
                    _article.value = response.body()?.articles
                } else {
                    _article.value = emptyList()
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                _loading.value = false
                _article.value = emptyList()
            }

        })
    }
}