package t.me.octopusapps.cinemapulse.data.remote

import java.util.concurrent.TimeUnit
import okhttp3.Call
import okhttp3.Connection
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HttpLoggingInterceptorFactoryTest {

    @Test
    fun `debug logging redacts api key query parameter`() {
        val logs = mutableListOf<String>()
        val interceptor = HttpLoggingInterceptorFactory.create(
            isDebug = true,
            logger = HttpLoggingInterceptor.Logger { message -> logs += message },
        )

        interceptor.intercept(
            FakeChain(
                Request.Builder()
                    .url("https://api.themoviedb.org/3/movie/popular?api_key=secret-key&page=1")
                    .build(),
            ),
        )

        val output = logs.joinToString(separator = "\n")
        assertTrue(output.contains("api_key"))
        assertFalse(output.contains("secret-key"))
    }

    @Test
    fun `release logging does not write network logs`() {
        val logs = mutableListOf<String>()
        val interceptor = HttpLoggingInterceptorFactory.create(
            isDebug = false,
            logger = HttpLoggingInterceptor.Logger { message -> logs += message },
        )

        interceptor.intercept(
            FakeChain(
                Request.Builder()
                    .url("https://api.themoviedb.org/3/movie/popular?api_key=secret-key&page=1")
                    .build(),
            ),
        )

        assertTrue(logs.isEmpty())
    }

    private class FakeChain(private val request: Request) : Interceptor.Chain {

        override fun request(): Request = request

        override fun proceed(request: Request): Response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body("{}".toResponseBody())
            .build()

        override fun connection(): Connection? = null

        override fun call(): Call = error("Call is not used by HttpLoggingInterceptor")

        override fun connectTimeoutMillis(): Int = 0

        override fun withConnectTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this

        override fun readTimeoutMillis(): Int = 0

        override fun withReadTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this

        override fun writeTimeoutMillis(): Int = 0

        override fun withWriteTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this
    }
}
