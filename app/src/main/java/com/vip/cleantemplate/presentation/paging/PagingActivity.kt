package com.vip.cleantemplate.presentation.paging

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.eduvy.demo.utils.extentions.showToast
import com.vip.cleantemplate.R
import com.vip.cleantemplate.base.BaseActivity
import com.vip.cleantemplate.common.Status
import com.vip.cleantemplate.databinding.ActivityPagingBinding
import com.vip.cleantemplate.databinding.ActivitySplashBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

import org.koin.android.viewmodel.ext.android.viewModel

class PagingActivity : BaseActivity() {

    private val viewModel: PagingViewModel by viewModel()
    private lateinit var pageAdapter: PagingAdapter

    private var searchJob: Job? = null
    private lateinit var binding: ActivityPagingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPagingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupObservers()
        setupPaging()

    }

    private fun setupObservers() {

            viewModel.userDataState.observe(this, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        hideLoadingDialog()
                        debugLogE("Result-","----- ${it.data} ----")
                        showToast("Data fetched successfully.!")
                    }
                    Status.LOADING -> {
                        showLoadingDialog(this)
                    }
                    Status.ERROR -> {
                        hideLoadingDialog()
                        showToast(R.string.apierror)
                    }
                }
            })

    }

    private fun setupPaging() {

        /** using Livedata* */
        /* searchJob?.cancel()
         searchJob = lifecycleScope.launch {
             viewModel.pagingData.observe(this@PagingActivity, {
                 pageAdapter.submitData(this@PagingActivity.lifecycle, it)
             })
         }*/

        /** Same thing using Flow* */
            searchJob?.cancel()
            searchJob = lifecycleScope.launch {
                viewModel.players
                    .collectLatest {
                        pageAdapter.submitData(it)
                    }
            }
    }

    private fun setupUI() {

        pageAdapter = PagingAdapter()

        binding?.rvPaging.apply {
            layoutManager = LinearLayoutManager(this@PagingActivity)
            binding?.rvPaging.adapter = pageAdapter.withLoadStateFooter(
                footer = PlayersLoadingStateAdapter { retry() })
        }

        pageAdapter.addLoadStateListener { loadState ->

            if (loadState.refresh is LoadState.Loading) {
                if (pageAdapter.snapshot().isEmpty()) {
                    showLoadingDialog(this)
                    binding?.errorTxt.isVisible = true
                }
            } else {

                hideLoadingDialog()
                binding?.errorTxt.isVisible = false

                val error = when {
                    loadState.mediator?.prepend is LoadState.Error -> loadState.mediator?.prepend as LoadState.Error
                    loadState.mediator?.append is LoadState.Error -> loadState.mediator?.append as LoadState.Error
                    loadState.mediator?.refresh is LoadState.Error -> loadState.mediator?.refresh as LoadState.Error
                    loadState.source.append is LoadState.Error -> loadState.source.append as LoadState.Error
                    loadState.source.prepend is LoadState.Error -> loadState.source.prepend as LoadState.Error
                    loadState.source.refresh is LoadState.Error -> loadState.source.refresh as LoadState.Error
                    else -> null
                }
                error?.let {
                    if (pageAdapter.snapshot().isEmpty()) {
                        binding?.errorTxt.isVisible = true
                        binding?.errorTxt.text = it.error.localizedMessage
                    }

                }

            }
        }
    }

    private fun retry() {
        pageAdapter.retry()
    }

}