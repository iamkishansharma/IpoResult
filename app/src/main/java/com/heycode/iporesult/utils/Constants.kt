package com.heycode.iporesult.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.heycode.iporesult.BuildConfig
import com.heycode.iporesult.R

const val BASE_URL = "https://iporesult.cdsc.com.np/result/"
const val LOGO_IMG = "https://iporesult.cdsc.com.np/assets/brand-login.png"
const val INTERNET_PERMISSION_CODE = 100
const val STORAGE_READ_PERMISSION_CODE = 101
const val STORAGE_WRITE_PERMISSION_CODE = 102
const val MY_SHARED_PREFERENCES = "myPref"
const val IS_DARK_MODE_ON = "IS_DARK_MODE_ON"

private fun setLogo(context: Context, imageView: ImageView) {
    Glide
        .with(context)
        .load(LOGO_IMG)
        .fitCenter()
        .placeholder(R.drawable.logo2)
        .into(imageView);
}

fun getVersionNameAndCode(): String {
    val versionName: String = BuildConfig.VERSION_NAME
    val versionCode: Int = BuildConfig.VERSION_CODE
    return "$versionName (${versionCode})"
}

fun hasInternetConnection(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (connectivityManager != null) {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
//                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
//                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
//                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
    }
    return false
}