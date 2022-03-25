package com.vip.cleantemplate.presentation.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.eduvy.demo.utils.extentions.showToast
import com.vip.cleantemplate.R
import com.vip.cleantemplate.base.BaseActivity
import com.vip.cleantemplate.common.Status
import com.vip.cleantemplate.data.preferences.SharedPreferenceValue
import com.vip.cleantemplate.databinding.ActivityMainBinding
import com.vip.cleantemplate.domain.model.Player
import com.vip.cleantemplate.presentation.paging.PagingActivity


import org.koin.android.viewmodel.ext.android.viewModel


class MainActivity : BaseActivity(), OnClickAdapterListener {

    private val mainViewModel: MainViewModel by viewModel()
    private lateinit var mainAdapter: MainAdapter

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupObserver()

        preferences.setValue(SharedPreferenceValue.IS_LOGGED, true)
        debugLogE("Username:", preferences.getStringValue(SharedPreferenceValue.USER_NAME, "")!!)

    }

    private fun setupUI() {

        binding.refreshList.setOnClickListener {
            mainViewModel.fetchUsers()
        }

        mainAdapter = MainAdapter(arrayListOf(),this)
        binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                adapter = mainAdapter
            }
    }

    private fun setupObserver() {

        with(mainViewModel) {

            showMessage.observe(this@MainActivity, Observer { message ->
                if(message is String)
                 showToast(message)
                else if(message is Int)
                    showToast(message)
            })


            isProgressLoading.observe(this@MainActivity, Observer { isVisible ->
                if (isVisible) showLoadingDialog(this@MainActivity)
                else hideLoadingDialog()
            })

            users.observe(this@MainActivity, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        hideLoadingDialog()
                        binding.nodata.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
                        it.data?.let {
                                users -> renderList(users)
                        }
                    }
                    Status.LOADING -> {
                        showLoadingDialog(this@MainActivity)
                        binding.nodata.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                    }
                    Status.ERROR -> {
                        hideLoadingDialog()
                        binding.nodata.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                        showToast(R.string.apierror)
                    }
                }
            })

        }

    }

    private fun renderList(users: List<Player>) {
        mainAdapter.setItems(users)
    }


    override fun clickedAdapterItem(name: String) {
        Intent(this@MainActivity, PagingActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(it)
        }
    }
}
