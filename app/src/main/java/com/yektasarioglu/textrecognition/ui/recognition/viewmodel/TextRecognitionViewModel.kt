package com.yektasarioglu.textrecognition.ui.recognition.viewmodel

import android.content.Context
import android.graphics.Bitmap

import androidx.core.util.forEach
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.yektasarioglu.textrecognition.huawei.LensEnginePreview
import com.yektasarioglu.textrecognition.text_recognition.TextLanguage
import com.yektasarioglu.textrecognition.text_recognition.TextRecognizer

import timber.log.Timber

class TextRecognitionViewModel @ViewModelInject constructor(private val textRecognizer: TextRecognizer) : ViewModel() {

    var outputLiveData = MutableLiveData<String>()

    private var isCameraStarted: Boolean = false

    fun initializeImageAnalyzer(isNetworkConnected: Boolean) {
        textRecognizer.initializeImageAnalyzer(isNetworkConnected, TextLanguage.TURKISH)
    }

    fun initializeStreamAnalyzer(context: Context) {
        textRecognizer.initializeStreamAnalyzer(context)
    }

    fun analyzeBitmap(bitmap: Bitmap) {
        textRecognizer.analyzeBitmap(bitmap) {
            Timber.d("stringValue is ${it.stringValue}")

            if (it.stringValue.isEmpty()) {
                outputLiveData.value = "No text recognized !!"
                Timber.e("No text recognized !!")
            }

            it.blocks.forEach {
                Timber.d("analyzeBitmap: ${it.stringValue}")
                outputLiveData.value = it.stringValue
            }
        }
    }

    fun analyzeStream() {
        if (isCameraStarted)
            textRecognizer.analyzeStream {
                it.forEach { _, value ->
                    Timber.d("stringValue is ${value?.stringValue}")
                    outputLiveData.value = value?.stringValue

                    value?.contents?.forEachIndexed { index, element ->
                        Timber.d("[$index] is ${element.stringValue}")
                    }
                }
                Timber.d("itemList is $it")
            }
    }

    fun openCameraStream(lensEnginePreview: LensEnginePreview) {
        textRecognizer.openCameraStream(lensEnginePreview)
        isCameraStarted = true
    }

    fun closeCameraStream(lensEnginePreview: LensEnginePreview) {
        textRecognizer.closeCameraStream(lensEnginePreview)
        isCameraStarted = false
    }

    fun onDestroy() {
        textRecognizer.releaseResources()
    }

}