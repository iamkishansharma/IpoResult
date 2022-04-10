package com.heycode.iporesult.models

data class CheckResponse(
    val body: Any,
    val message: String,
    val success: Boolean
)