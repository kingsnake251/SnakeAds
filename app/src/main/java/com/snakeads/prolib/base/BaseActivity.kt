package com.snakeads.prolib.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import dagger.hilt.android.AndroidEntryPoint

abstract class BaseActivity<T : ViewBinding, VM : BaseViewModel> : AppCompatActivity() {
    protected lateinit var binding: T
    protected abstract val viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getInflatedLayout(layoutInflater))
        initView()
        observeData()
    }

    abstract fun setBinding(layoutInflater: LayoutInflater): T

    abstract fun initView()

    abstract fun observeData()

    private fun getInflatedLayout(inflater: LayoutInflater): View {
        binding = setBinding(inflater)
        return binding.root
    }
}