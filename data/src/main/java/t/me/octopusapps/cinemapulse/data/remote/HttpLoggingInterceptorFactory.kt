package t.me.octopusapps.cinemapulse.data.remote

import okhttp3.logging.HttpLoggingInterceptor
import t.me.octopusapps.cinemapulse.data.config.ApiConstants

internal object HttpLoggingInterceptorFactory {

    fun create(isDebug: Boolean): HttpLoggingInterceptor =
        create(
            isDebug = isDebug,
            logger = HttpLoggingInterceptor.Logger.DEFAULT
        )

    fun create(
        isDebug: Boolean,
        logger: HttpLoggingInterceptor.Logger
    ): HttpLoggingInterceptor =
        HttpLoggingInterceptor(logger).apply {
            redactQueryParams(ApiConstants.API_KEY_QUERY_PARAMETER)
            level = if (isDebug) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
}
