package t.me.octopusapps.cinemapulse.data.remote

import okhttp3.Interceptor
import okhttp3.Response

internal class AuthInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val url = originalRequest.url.newBuilder()
            .addQueryParameter("api_key", apiKey)
            .build()
        return chain.proceed(originalRequest.newBuilder().url(url).build())
    }
}