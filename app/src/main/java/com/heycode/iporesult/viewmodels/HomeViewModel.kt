package com.heycode.iporesult.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heycode.iporesult.api.HomeRepository
import com.heycode.iporesult.models.CheckData
import com.heycode.iporesult.models.CheckResponse
import com.heycode.iporesult.models.HomeData
import com.heycode.iporesult.models.ReloadCaptchaResponse
import com.heycode.iporesult.utils.Resources
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository,
) : ViewModel() {
    private val homeMutableLiveData = MutableLiveData<HomeData>()
    private val captchaMutableLiveData = MutableLiveData<ReloadCaptchaResponse>()
    private val resultMutableLiveData = MutableLiveData<CheckResponse>()
    private val messageMutableLiveData = MutableLiveData<String>()

    // view can access
    val homeContent: LiveData<HomeData> = homeMutableLiveData
    val captchaLiveData: LiveData<ReloadCaptchaResponse> = captchaMutableLiveData
    val resultLiveData: LiveData<CheckResponse> = resultMutableLiveData
    val message: LiveData<String> = messageMutableLiveData

    // on instance created GET all data
    suspend fun getHome() {
        viewModelScope.launch {
            when (val response = repository.getHome()) {
                is Resources.Success -> {
                    homeMutableLiveData.value = response.data!!
                    messageMutableLiveData.value = response.message!!
                }
                is Resources.Error -> {
                    messageMutableLiveData.value = response.message!!
                }
            }
        }
    }

    // on function call POST JsonObject as body
    suspend fun checkResult(payload: CheckData) {
        when (val response = repository.checkResult(payload)) {
            is Resources.Success -> {
                resultMutableLiveData.value = response.data!!
                messageMutableLiveData.value = response.data.message
            }
            is Resources.Error -> {
                messageMutableLiveData.value = response.message!!
            }
        }
    }

    // this is already done from backend so no need to implement
    suspend fun reloadCaptcha(captchaId: String) {
        when (val response = repository.reloadCaptcha(captchaId)) {
            is Resources.Success -> {
                captchaMutableLiveData.value = response.data!!
                messageMutableLiveData.value = response.message!!
            }
            is Resources.Error -> {
                messageMutableLiveData.value = response.message!!
            }
        }
    }


}