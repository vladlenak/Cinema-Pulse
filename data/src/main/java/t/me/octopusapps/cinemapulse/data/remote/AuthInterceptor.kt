package t.me.octopusapps.cinemapulse.data.remote

import okhttp3.Interceptor
import okhttp3.Response
import t.me.octopusapps.cinemapulse.data.config.ApiConstants

internal class AuthInterceptor(apiKey: String) : Interceptor {

    private val apiKey = apiKey.trim().also { trimmedApiKey ->
        check(trimmedApiKey.isNotEmpty()) {
            "TMDB API key is missing. Add apikey=YOUR_TMDB_API_KEY to local.properties."
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val url = originalRequest.url.newBuilder()
            .setQueryParameter(ApiConstants.API_KEY_QUERY_PARAMETER, apiKey)
            .build()
        return chain.proceed(originalRequest.newBuilder().url(url).build())
    }
}
