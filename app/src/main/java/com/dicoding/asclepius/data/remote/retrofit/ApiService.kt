package com.dicoding.asclepius.data.remote.retrofit

import com.dicoding.asclepius.BuildConfig
import com.dicoding.asclepius.data.remote.response.NewsResponse

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import com.dicoding.asclepius.utils.Constant.COUNTRY
import com.dicoding.asclepius.utils.Constant.CATEGORY



interface ApiService {
    @GET("top-headlines")
    fun getNews(
        @Query("country") country: String = COUNTRY,
        @Query("category") category: String = CATEGORY,
        @Query("language") language: String = "en",
        @Query("apiKey") apiKey: String = BuildConfig.API_KEY
    ): Call<NewsResponse>
}