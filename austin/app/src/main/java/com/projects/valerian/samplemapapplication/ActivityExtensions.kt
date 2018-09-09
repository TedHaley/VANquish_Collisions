package com.projects.valerian.samplemapapplication

import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.projects.valerian.samplemapapplication.api.SuggestionsAPI
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://hibuaduiiqwerudnba.serveo.net/"

fun AppCompatActivity.showSnackbar(view: View, msg: String)
        = Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show()

fun AppCompatActivity.getApiInstance() = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(SuggestionsAPI::class.java)