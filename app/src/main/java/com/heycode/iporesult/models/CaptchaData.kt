package com.heycode.iporesult.models

data class CaptchaData(
    val audioCaptcha: String,
    val captcha: String,
    val captchaIdentifier: String
)