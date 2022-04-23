package com.heycode.iporesult.api;

import com.heycode.iporesult.models.CheckRequest
import com.heycode.iporesult.models.CheckResponse
import com.heycode.iporesult.models.HomeData
import com.heycode.iporesult.utils.Resources
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private val api: HomeApi,
) {

    suspend fun getHome(): Resources<HomeData> {
        return try {
            val response = api.getHome()
            val data = response.body()

            if (response.isSuccessful && data != null) {
                Resources.Success(data, "Successful")
            } else {
                Resources.Error(response.message())
            }
        } catch (e: Exception) {
            Resources.Error(e.message ?: "An error occurred!")
        }
    }

    suspend fun checkResult(payload: CheckRequest): Resources<CheckResponse> {
        return try {
            val response = api.checkResult(payload)
            val data = response.body()
            if (response.isSuccessful && data != null) {
                Resources.Success(data, response.body()!!.message)
            } else {
                Resources.Error(response.message())
            }
        } catch (e: Exception) {
            Resources.Error(e.message ?: "An error occurred!")
        }
    }
}