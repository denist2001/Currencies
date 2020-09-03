package com.codechallenge.currencies.repository

import com.codechallenge.currencies.data.Response
import retrofit2.Callback
import javax.inject.Inject

class RepositoryImpl @Inject constructor() : Repository {

    @Inject
    lateinit var service: RepositoryService

    override fun getRates(callback: Callback<Response>) {
        val callResponse = service.getRates()
        callResponse.enqueue(callback)
    }

}