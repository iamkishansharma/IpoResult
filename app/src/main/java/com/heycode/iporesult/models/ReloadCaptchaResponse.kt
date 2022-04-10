package com.heycode.iporesult.models

data class ReloadCaptchaResponse(
    val body: CaptchaData,
    val message: String,
    val success: Boolean
)
