package com.github.bobryanskiy.practice.data.remote

import android.content.Context
import android.util.Base64
import okhttp3.OkHttpClient
import java.security.KeyStore
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

object CertUtils {

    fun getOkHttpClientWithCustomTrust(context: Context): OkHttpClient {
        val certFiles = listOf("gigachat_token.cer", "gigachat_api.cer")

        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
            load(null, null)
            certFiles.forEachIndexed { index, fileName ->
                val cert = loadPemCertificate(context, fileName)
                setCertificateEntry("cert_$index", cert)
            }
        }

        val trustManagerFactory = TrustManagerFactory.getInstance(
            TrustManagerFactory.getDefaultAlgorithm()
        ).apply {
            init(keyStore)
        }

        val sslContext = SSLContext.getInstance("TLS").apply {
            init(null, trustManagerFactory.trustManagers, null)
        }

        return OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustManagerFactory.trustManagers[0] as X509TrustManager)
            .build()
    }

    private fun loadPemCertificate(context: Context, fileName: String): java.security.cert.Certificate {
        val cf = CertificateFactory.getInstance("X.509")
        val pem = context.assets.open(fileName).bufferedReader().readText()
        val base64 = pem
            .replace("-----BEGIN CERTIFICATE-----", "")
            .replace("-----END CERTIFICATE-----", "")
            .replace("\\s".toRegex(), "")
        val decoded = Base64.decode(base64, Base64.DEFAULT)
        return cf.generateCertificate(decoded.inputStream())
    }
}