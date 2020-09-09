package com.codechallenge.currencies.ui.main

import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.databinding.ObservableInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.codechallenge.currencies.R
import com.codechallenge.currencies.data.Response
import com.codechallenge.currencies.databinding.MainFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.ObsoleteCoroutinesApi
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.main_fragment), LifecycleOwner {

    @Inject
    lateinit var mainFragmentAdapter: MainFragmentAdapter

    private val viewModel by viewModels<MainViewModel>()
    private var binding: MainFragmentBinding? = null

    var progressBarVisibility = ObservableInt(INVISIBLE)

    @ObsoleteCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MainFragmentBinding.bind(view)
        setupRecyclerView()
        observeViewModelState()
        if (savedInstanceState == null) {
            viewModel.onAction(MainViewModelAction.StartLoading)
        }
    }

    private fun observeViewModelState() {
        viewModel.state.observe(viewLifecycleOwner,
            { state ->
                when (state) {
                    MainViewModelState.Loading -> progressBarVisibility.set(VISIBLE)
                    is MainViewModelState.Result -> {
                        progressBarVisibility.set(INVISIBLE)
                        showResult(state.response)
                    }
                    is MainViewModelState.Error -> {
                        progressBarVisibility.set(INVISIBLE)
                        showError(state.message)
                    }
                }
            })
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun showResult(response: Response) {
        if (response.rates.isNullOrEmpty()) {
            showError("Empty response")
            return
        }
        mainFragmentAdapter.setNewRates(response.rates)
    }

    private fun setupRecyclerView() {
        with(currencies_rv) {
            adapter = mainFragmentAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}