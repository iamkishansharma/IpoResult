package com.heycode.iporesult.di

import com.heycode.iporesult.BuildConfig
import com.heycode.iporesult.api.HomeApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // will inject retrofit instance into HomeApi
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val client = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // give api access to Repository
    @Provides
    @Singleton
    fun provideHomeApi(retrofit1: Retrofit): HomeApi =
        retrofit1.create(HomeApi::class.java)
}