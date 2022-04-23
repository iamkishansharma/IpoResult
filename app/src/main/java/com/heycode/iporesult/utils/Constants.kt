package com.heycode.iporesult.utils

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.heycode.iporesult.BuildConfig
import com.heycode.iporesult.R

const val LOGO_IMG = "https://iporesult.cdsc.com.np/assets/brand-login.png"
const val MY_SHARED_PREFERENCES = "myPref"
const val IS_DARK_MODE_ON = "IS_DARK_MODE_ON"
const val USER_BOID_SET = "USER_BOID_SET"

// editor will help to set data
fun editorFromSharedPref(context: Context): SharedPreferences.Editor {
    return context.getSharedPreferences(
        MY_SHARED_PREFERENCES,
        AppCompatActivity.MODE_PRIVATE
    ).edit()
}

// it will help to get data out of Shared Pref
fun dataFromSharedPref(context: Context): SharedPreferences {
    return context.getSharedPreferences(
        MY_SHARED_PREFERENCES,
        AppCompatActivity.MODE_PRIVATE
    )
}

private fun setLogo(context: Context, imageView: ImageView) {
    Glide
        .with(context)
        .load(LOGO_IMG)
        .fitCenter()
        .placeholder(R.drawable.logo2)
        .into(imageView)
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