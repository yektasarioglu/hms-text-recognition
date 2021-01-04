package com.yektasarioglu.textrecognition.ui.recognition.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast

import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels

import com.esafirm.imagepicker.features.ImagePicker

import com.yektasarioglu.textrecognition.ui.recognition.viewmodel.TextRecognitionViewModel
import com.yektasarioglu.textrecognition.base.BaseFragment
import com.yektasarioglu.textrecognition.databinding.FragmentTextRecognitionBinding
import com.yektasarioglu.textrecognition.extensions.toDrawable
import com.yektasarioglu.textrecognition.extensions.toast

import dagger.hilt.android.AndroidEntryPoint

import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions

@AndroidEntryPoint
@RuntimePermissions
class TextRecognitionFragment : BaseFragment<FragmentTextRecognitionBinding, TextRecognitionViewModel>() {

    override val viewBinding: FragmentTextRecognitionBinding by lazy { FragmentTextRecognitionBinding.inflate(layoutInflater) }
    override val viewModel: TextRecognitionViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        initialize()
    }

    override fun onResume() {
        super.onResume()
        viewModel.openCameraStream(viewBinding.preview)
    }

    override fun onPause() {
        super.onPause()
        viewModel.closeCameraStream(viewBinding.preview)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            val image = ImagePicker.getFirstImageOrNull(data)
            viewBinding.apply {
                val drawable = image.uri.toDrawable(this@TextRecognitionFragment.activity as Context)
                viewModel.analyzeBitmap(drawable.toBitmap())
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // NOTE: delegate the permission handling to generated function
        onRequestPermissionsResult(requestCode, grantResults)
    }


    override fun onNetworkAvailable() {
        super.onNetworkAvailable()
        viewModel.initializeTextAnalyzer(isNetworkConnected = true)
    }

    override fun onNetworkUnavailable() {
        super.onNetworkUnavailable()
        viewModel.initializeTextAnalyzer(isNetworkConnected = false)
    }

    private fun setupUI() {
        viewBinding.apply {
            scanFromGalleryTextView.setOnClickListener { askImageFromUser() }
            scanTextView.setOnClickListener {
                viewModel.analyzeStream()
            }
        }
    }

    private fun initialize() {
        viewModel.initializeTextAnalyzer(isNetworkConnected = false)
        startCameraWithPermissionCheck()
    }

    private fun askImageFromUser() = ImagePicker.create(this).single().start()

    @NeedsPermission(Manifest.permission.CAMERA)
    fun startCamera() {
        viewModel.initializeStreamAnalyzer(activity as Context)
        viewModel.openCameraStream(viewBinding.preview)
        registerOutputObserver()
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    fun onCameraDenied() {
        Toast.makeText(activity, "Permission Denied", Toast.LENGTH_SHORT).show()
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    fun onCameraNeverAskAgain() {
        Toast.makeText(activity, "You should manually give permissions !!", Toast.LENGTH_SHORT).show()
    }

    private fun registerOutputObserver() = viewModel.outputLiveData.observe(viewLifecycleOwner, { toast("Output is $it") })

}