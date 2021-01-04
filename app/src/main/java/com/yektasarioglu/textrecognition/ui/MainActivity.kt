package com.yektasarioglu.textrecognition.ui

import com.yektasarioglu.textrecognition.base.BaseActivity
import com.yektasarioglu.textrecognition.databinding.ActivityMainBinding

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    override val viewBinding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

}