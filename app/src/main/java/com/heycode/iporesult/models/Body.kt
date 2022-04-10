package com.heycode.iporesult.models

data class Body(
    val captchaData: CaptchaData,
    val companyShareList: List<CompanyShare>
)