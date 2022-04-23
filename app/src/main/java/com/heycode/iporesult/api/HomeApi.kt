package com.heycode.iporesult.api

import com.heycode.iporesult.BuildConfig
import com.heycode.iporesult.models.CheckRequest
import com.heycode.iporesult.models.CheckResponse
import com.heycode.iporesult.models.HomeData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface HomeApi {
    @GET("companyShares/fileUploaded")
    suspend fun getHome(): Response<HomeData>

    @POST("${BuildConfig.BASE_URL2}check")
    suspend fun checkResult(@Body checkData: CheckRequest): Response<CheckResponse>
}