package com.yektasarioglu.textrecognition.text_recognition

import android.content.Context
import android.graphics.Bitmap
import android.util.SparseArray

import com.huawei.hmf.tasks.Task
import com.huawei.hms.mlsdk.MLAnalyzerFactory
import com.huawei.hms.mlsdk.common.LensEngine
import com.huawei.hms.mlsdk.common.MLException
import com.huawei.hms.mlsdk.common.MLFrame
import com.huawei.hms.mlsdk.text.MLLocalTextSetting
import com.huawei.hms.mlsdk.text.MLRemoteTextSetting
import com.huawei.hms.mlsdk.text.MLText
import com.huawei.hms.mlsdk.text.MLTextAnalyzer
import com.yektasarioglu.textrecognition.huawei.LensEnginePreview

import timber.log.Timber

import java.io.IOException

import javax.inject.Inject

class TextRecognizer @Inject constructor() {

    private var analyzer: MLTextAnalyzer? = null
    private var setting: Any? = null

    private var lensEngine: LensEngine? = null
    private lateinit var ocrDetectorProcessor: OcrDetectorProcessor

    fun initializeImageAnalyzer(isNetworkConnected: Boolean = false, textLanguage: TextLanguage) {
        if (isNetworkConnected)
            initializeCloudImageAnalyzer(listOf(textLanguage.languageCode))
        else
            initializeDeviceImageAnalyzer(textLanguage.languageCode)
    }

    fun initializeStreamAnalyzer(context: Context) {
        initializeDeviceStreamAnalyzer(context)
    }

    fun analyzeBitmap(bitmap: Bitmap, onSuccess: (MLText) -> Unit) {
        Timber.d("analyzeBitmap()")

        val task: Task<MLText> = analyzer?.asyncAnalyseFrame(MLFrame.fromBitmap(bitmap))!!

        task.addOnSuccessListener {
            // Recognition success.
            Timber.d("Success - Result is ${it.stringValue}")
            onSuccess(it)
        }.addOnFailureListener { e ->
            // If the recognition fails, obtain related exception information.
            try {
                val mlException = e as MLException
                // Obtain the result code. You can process the result code and customize respective messages displayed to users.
                val errorCode = mlException.errCode
                // Obtain the error information. You can quickly locate the fault based on the result code.
                val errorMessage = mlException.message

                Timber.d("Failure - errorCode: $errorCode errorMessage is $errorMessage")
            } catch (error: Exception) {
                Timber.e("Failure - Exception is $error")
            }
        }
    }

    fun analyzeStream(onSuccess: (SparseArray<MLText.Block?>) -> Unit) {
        ocrDetectorProcessor.resultAction = onSuccess
    }

    fun openCameraStream(lensEnginePreview: LensEnginePreview) {
        try {
            lensEnginePreview.start(lensEngine)
        } catch (exception: IOException) {
            Timber.e("Exception is $exception")
        }
    }

    fun closeCameraStream(lensEnginePreview: LensEnginePreview) = lensEnginePreview.stop()

    fun releaseResources() {
        try { analyzer?.stop() }
        catch (e: IOException) {
            Timber.e("Exception is $e")
        }

        lensEngine?.release()
    }

    private fun initializeCloudImageAnalyzer(supportedLanguageList: List<String>) {
        Timber.d("On-Cloud analyzer initialized !!")

        setting = MLRemoteTextSetting.Factory() // Set the on-cloud text detection mode.
            // MLRemoteTextSetting.OCR_COMPACT_SCENE: dense text recognition
            // MLRemoteTextSetting.OCR_LOOSE_SCENE: sparse text recognition
            .setTextDensityScene(MLRemoteTextSetting.OCR_COMPACT_SCENE) // Specify the languages that can be recognized, which should comply with ISO 639-1.
            .setLanguageList(supportedLanguageList) // TODO: Is sending single language makes it more precise?
            // MLRemoteTextSetting.NGON: Return the coordinates of the four corner points of the quadrilateral.
            // MLRemoteTextSetting.ARC: Return the corner points of a polygon border in an arc. The coordinates of up to 72 corner points can be returned.
            .setBorderType(MLRemoteTextSetting.ARC)
            .create()

        analyzer =
            MLAnalyzerFactory.getInstance().getRemoteTextAnalyzer(setting as? MLRemoteTextSetting)
    }

    private fun initializeDeviceImageAnalyzer(language: String) {
        Timber.d("On-Device analyzer initialized !!")

        setting = MLLocalTextSetting.Factory()
            .setOCRMode(MLLocalTextSetting.OCR_DETECT_MODE)
            .setLanguage(language)
            .create()

        analyzer = MLAnalyzerFactory.getInstance().getLocalTextAnalyzer(setting as? MLLocalTextSetting)
    }

    private fun initializeDeviceStreamAnalyzer(context: Context) {
        ocrDetectorProcessor = OcrDetectorProcessor()

        analyzer = MLTextAnalyzer.Factory(context).create()
        analyzer?.setTransactor(ocrDetectorProcessor)

        lensEngine = LensEngine.Creator(context, analyzer)
            .setLensType(LensEngine.BACK_LENS)
            .applyDisplayDimension(1920, 1080)
            .applyFps(60.0f)
            .enableAutomaticFocus(true)
            .create()
    }

}