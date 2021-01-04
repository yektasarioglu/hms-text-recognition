package com.yektasarioglu.textrecognition

import android.app.Application
import com.huawei.hms.mlsdk.common.MLApplication

import dagger.hilt.android.HiltAndroidApp

import timber.log.Timber
import timber.log.Timber.DebugTree

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) Timber.plant(DebugTree())

        MLApplication.getInstance().apiKey = BuildConfig.HMS_API_KEY
    }
}