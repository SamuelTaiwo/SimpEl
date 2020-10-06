package com.example.simpel

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Network {
    val okHttpClient = OkHttpClient.Builder().build()
    val gsonConverterFactory = GsonConverterFactory.create()
    val coroutineCallAdapterFactory = CoroutineCallAdapterFactory()
    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(gsonConverterFactory)
        .addCallAdapterFactory(coroutineCallAdapterFactory)
        .build()
    val trackService = retrofit.create(LastFmNetworkService::class.java)
}

