package com.github.bobryanskiy.practice.data.remote

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface TokenApiService {
    @FormUrlEncoded
    @POST("/api/v2/oauth")
    suspend fun getToken(
        @Header("Authorization") basicAuth: String,
        @Header("RqUID") rqUID: String,
        @Field("scope") scope: String = "GIGACHAT_API_PERS"
    ): Response<TokenResponse>
}

data class TokenResponse(
    val access_token: String,
    val expires_at: Long
)
