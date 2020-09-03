package com.codechallenge.currencies.repository

import com.codechallenge.currencies.data.Response
import retrofit2.Call
import retrofit2.http.GET

interface RepositoryService {
    @GET("/rates")
    fun getRates(): Call<Response>
}
