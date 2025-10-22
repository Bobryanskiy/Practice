package com.github.bobryanskiy.practice.data.remote

import android.util.Base64.NO_WRAP
import android.util.Base64.encodeToString
import android.util.Log
import com.github.bobryanskiy.practice.BuildConfig
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(private val tokenApiService: TokenApiService){
    private val mutex = Mutex()

    private var cachedToken: String? = null
    private var tokenExpiryTime: Long = 0

    suspend fun getValidToken(): String = mutex.withLock {
        if (isTokenExpired()) refreshAccessToken()
        cachedToken ?: throw IllegalStateException("Не удалось получить токен GigaChat API")
    }

    private fun isTokenExpired() = System.currentTimeMillis() >= tokenExpiryTime

    private suspend fun refreshAccessToken() {
        val clientId = BuildConfig.GIGACHAT_CLIENT_ID
        val clientSecret = BuildConfig.GIGACHAT_CLIENT_SECRET

        val credentials = "$clientId:$clientSecret"
        val authorizationKey = encodeToString(credentials.toByteArray(), NO_WRAP)

        val basicAuth = "Basic $authorizationKey"
        val rqUID = UUID.randomUUID().toString()

        val response = tokenApiService.getToken(basicAuth, rqUID = rqUID)

        if (response.isSuccessful) {
            val tokenResponse = response.body()
            cachedToken = tokenResponse?.access_token
            val serverExpiryTime = tokenResponse?.expires_at ?: 0
            tokenExpiryTime = serverExpiryTime - 60000
            Log.d("F", "SUCCESS TOKEN")
        } else {
            val errorMessage = response.errorBody()?.toString() ?: "Неизвестная ошибка"
            throw Exception("Ошибка получения токена GigaChat API: ${response.code()} - $errorMessage")
        }
    }
}