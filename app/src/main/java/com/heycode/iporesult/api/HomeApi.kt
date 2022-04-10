package com.heycode.iporesult.api

import com.heycode.iporesult.models.CheckData
import com.heycode.iporesult.models.CheckResponse
import com.heycode.iporesult.models.HomeData
import com.heycode.iporesult.models.ReloadCaptchaResponse
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface HomeApi {
    @GET("companyShares/fileUploaded")
    suspend fun getHome(): Response<HomeData>

    /*
    Send this data and get result response
    {"companyShareId": "28","boid": "1301590000282887","userCaptcha": "49147",
    "captchaIdentifier":"2c6803f6-2a1c-4fe5-94f3-3cd03b96e1eb"}
    */
    @POST("result/check")
    suspend fun checkResult(
        @Body body: CheckData,
    ): Response<CheckResponse>

    // send old captchaId to reload NEW captcha
    @POST("captcha/reload/{oldCaptchaId}")
    suspend fun reloadCaptcha(
        @Path("oldCaptchaId") captchaIdentifier: String,
    ): Response<ReloadCaptchaResponse>
//https://iporesult.cdsc.com.np/result/captcha/reload/a5e8efad-8296-4681-b81e-835379d73f3b
}