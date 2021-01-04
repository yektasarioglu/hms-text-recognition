package com.yektasarioglu.textrecognition.text_recognition

import android.util.SparseArray

import com.huawei.hms.mlsdk.common.MLAnalyzer.MLTransactor
import com.huawei.hms.mlsdk.text.MLText
import com.huawei.hms.mlsdk.common.MLAnalyzer

class OcrDetectorProcessor : MLTransactor<MLText.Block?> {

    var resultAction: ((SparseArray<MLText.Block?>) -> Unit)? = null

    override fun transactResult(results: MLAnalyzer.Result<MLText.Block?>) {
        val items = results.analyseList
        // Determine detection result processing as required. Note that only the detection results are processed.
        // Other detection-related APIs provided by ML Kit cannot be called.
        //Log.i(TAG, "items is $items")

        resultAction?.invoke(items)
        resultAction = null
    }

    override fun destroy() {
        // Callback method used to release resources when the detection ends.
    }

}