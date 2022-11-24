package com.juniori.puzzle.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.juniori.puzzle.data.firebase.FireStoreDataSource
import com.juniori.puzzle.data.firebase.FirebaseService
import com.juniori.puzzle.util.STORAGE_BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val TIME_OUT_MILLIS = 5000L

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(TIME_OUT_MILLIS, TimeUnit.MILLISECONDS)
        .readTimeout(TIME_OUT_MILLIS, TimeUnit.MILLISECONDS)
        .writeTimeout(TIME_OUT_MILLIS, TimeUnit.MILLISECONDS)
        .addNetworkInterceptor {
            val request = it.request()
                .newBuilder()
                .build()
            it.proceed(request)
        }
        .build()

    @Singleton
    @Provides
    fun provideGson(): Gson = GsonBuilder()
        .setLenient()
        .create()

    @Singleton
    @Provides
    @Storage
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .baseUrl(STORAGE_BASE_URL)
        .build()

    @Singleton
    @Provides
    fun provideFirebaseService(@Storage retrofit: Retrofit): FirebaseService =
        retrofit.create(FirebaseService::class.java)

    @Singleton
    @Provides
    fun provideFirebaseRepository(service: FirebaseService): FireStoreDataSource =
        FireStoreDataSource(service)

    class Interceptor : okhttp3.Interceptor {
        override fun intercept(chain: okhttp3.Interceptor.Chain): Response {
            TODO("Not yet implemented")
        }

    }
}
