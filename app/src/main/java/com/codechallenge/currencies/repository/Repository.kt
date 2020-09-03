package com.codechallenge.currencies.repository

import com.codechallenge.currencies.data.Response
import retrofit2.Callback

interface Repository {
    fun getRates(callback: Callback<Response>)
}
