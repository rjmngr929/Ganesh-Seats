package com.my.ganeshseats.di

import com.my.ganeshseats.api.AuthInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.my.ganeshseats.BuildConfig
import com.my.ganeshseats.api.AuthAPI
import com.my.ganeshseats.api.UserAPI
import com.my.ganeshseats.constants.Constants
import com.my.ganeshseats.di.exception.NetworkExceptionHandler
import com.my.ganeshseats.di.exception.NetworkInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {


    @Provides
    fun provideNetworkExceptionHandler(): NetworkExceptionHandler {
        return NetworkExceptionHandler()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    @Provides
    fun provideNetworkInterceptor(exceptionHandler: NetworkExceptionHandler): NetworkInterceptor {
        return NetworkInterceptor(exceptionHandler)
    }


    @Singleton
    @Provides
    fun providesRetrofit(gson: Gson): Retrofit.Builder {
        return Retrofit.Builder().baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(interceptor: AuthInterceptor, networkInterceptor: NetworkInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(networkInterceptor)
                .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true).build()
    }

    @Singleton
    @Provides
    fun providesAuthAPI(retrofitBuilder: Retrofit.Builder): AuthAPI {
        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
        return retrofitBuilder.client(okHttpClient).build().create(AuthAPI::class.java)
    }

    @Singleton
    @Provides
    fun providesUserAPI(retrofitBuilder: Retrofit.Builder, okHttpClient: OkHttpClient): UserAPI {
        return retrofitBuilder.client(okHttpClient).build().create(UserAPI::class.java)
    }



}