package com.github.bobryanskiy.practice.di

import android.content.Context
import com.github.bobryanskiy.practice.data.local.AppDatabase
import com.github.bobryanskiy.practice.data.remote.CertUtils
import com.github.bobryanskiy.practice.data.remote.GigaChatApiService
import com.github.bobryanskiy.practice.data.remote.TokenApiService
import com.github.bobryanskiy.practice.data.repository.DefaultTaskRepository
import com.github.bobryanskiy.practice.domain.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTaskRepository(
        defaultTaskRepository: DefaultTaskRepository
    ): TaskRepository
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkAndDataModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        return CertUtils.getOkHttpClientWithCustomTrust(context)
    }

    @Provides
    @Singleton
    fun provideTokenApiService(okHttpClient: OkHttpClient): TokenApiService {
        return Retrofit.Builder()
            .baseUrl("https://ngw.devices.sberbank.ru:9443")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TokenApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideGigaChatApiService(okHttpClient: OkHttpClient): GigaChatApiService {
        return Retrofit.Builder()
            .baseUrl("https://gigachat.devices.sberbank.ru")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GigaChatApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideUserProgressDao(database: AppDatabase) = database.userProgressDao()

    @Provides
    @Singleton
    fun provideUsedTableDao(database: AppDatabase) = database.usedTableDao()
}