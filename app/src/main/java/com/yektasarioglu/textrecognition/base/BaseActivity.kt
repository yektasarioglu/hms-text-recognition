package com.yektasarioglu.textrecognition.base

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding

import java.lang.reflect.ParameterizedType

abstract class BaseActivity<VB : ViewBinding, VM : ViewModel> : AppCompatActivity() {

    protected abstract val viewBinding: VB
    protected lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        viewModel = ViewModelProvider(this).get(getViewModelClass())
    }

    private fun getViewModelClass(): Class<VM> {
        return (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1] as Class<VM>
    }

}