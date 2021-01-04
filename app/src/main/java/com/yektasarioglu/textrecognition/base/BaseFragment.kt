package com.yektasarioglu.textrecognition.base

import android.app.Dialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB: ViewBinding, VM: ViewModel> : Fragment() {

    protected abstract val viewBinding: VB
    protected abstract val viewModel: VM

    private var dialog: Dialog? = null

    protected var isNetworkConnected = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? = viewBinding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleInternetConnectivity()
    }

    fun isDarkModeEnabled() = (activity as BaseActivity<*, *>).isDarkModeEnabled()

    fun switchTheme() = (activity as BaseActivity<*, *>).switchTheme()

    protected fun hideLoading() {
        dialog?.let { dialog ->
            dialog.dismiss()
        }
    }

    private fun handleInternetConnectivity() {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) { isNetworkConnected = true }
            override fun onLost(network: Network) { isNetworkConnected = false }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        } else {
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

            connectivityManager.registerNetworkCallback(request, networkCallback)
        }
    }

}