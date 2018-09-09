package com.projects.valerian.samplemapapplication.api

import com.projects.valerian.samplemapapplication.model.Suggestion
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SuggestionsAPI {

    @POST("api/suggestions")
    fun postSuggestion(@Body content: Suggestion): Single<Any>

    @GET("api/suggestions")
    fun getSuggestions(): Single<List<Suggestion>>
}