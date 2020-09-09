package com.codechallenge.currencies.ui.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codechallenge.currencies.data.Response
import com.codechallenge.currencies.repository.Repository
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback

class MainViewModel @ViewModelInject constructor(
    private val repository: Repository,
    private val requestDelayInMs: Long
) : ViewModel() {

    val state = MutableLiveData<MainViewModelState>()

    @ObsoleteCoroutinesApi
    fun onAction(action: MainViewModelAction) {
        when (action) {
            is MainViewModelAction.StartLoading -> startLoading()
        }
    }

    @ObsoleteCoroutinesApi
    private fun startLoading() {
        state.postValue(MainViewModelState.Loading)
        val timerTask = ticker(requestDelayInMs, 0)
        viewModelScope.launch {
            for (event in timerTask) {
                repository.getRates(callback)
            }
        }
    }

    private val callback = object : Callback<Response> {
        override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
            when (response.code()) {
                in 100 until 300 -> response.body()?.let {
                    state.postValue(MainViewModelState.Result(it))
                }
                else -> state.postValue(MainViewModelState.Error("Can not fetch data from server"))
            }
        }

        override fun onFailure(call: Call<Response>, t: Throwable) {
            t.message?.let { state.postValue(MainViewModelState.Error(it)) }
        }

    }
}

sealed class MainViewModelAction {
    object StartLoading : MainViewModelAction()
}

sealed class MainViewModelState {
    object Loading : MainViewModelState()
    class Result(val response: Response) : MainViewModelState()
    class Error(val message: String) : MainViewModelState()
}
