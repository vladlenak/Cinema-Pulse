package t.me.octopusapps.cinemapulse.data.remote

import java.util.concurrent.TimeUnit
import okhttp3.Call
import okhttp3.Connection
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class AuthInterceptorTest {

    @Test
    fun `constructor rejects blank api key`() {
        assertThrows(IllegalStateException::class.java) {
            AuthInterceptor(" ")
        }
    }

    @Test
    fun `intercept adds trimmed api key query parameter`() {
        val chain = FakeChain(
            Request.Builder()
                .url("https://api.themoviedb.org/3/movie/popular?page=1")
                .build(),
        )

        AuthInterceptor(" test-api-key ").intercept(chain)

        val url = chain.proceededRequest.url
        assertEquals("1", url.queryParameter("page"))
        assertEquals("test-api-key", url.queryParameter("api_key"))
    }

    @Test
    fun `intercept replaces existing api key query parameter`() {
        val chain = FakeChain(
            Request.Builder()
                .url("https://api.themoviedb.org/3/movie/popular?api_key=old-key")
                .build(),
        )

        AuthInterceptor("new-key").intercept(chain)

        assertEquals(listOf("new-key"), chain.proceededRequest.url.queryParameterValues("api_key"))
    }

    private class FakeChain(private val request: Request) : Interceptor.Chain {

        lateinit var proceededRequest: Request
            private set

        override fun request(): Request = request

        override fun proceed(request: Request): Response {
            proceededRequest = request
            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body("{}".toResponseBody())
                .build()
        }

        override fun connection(): Connection? = null

        override fun call(): Call = error("Call is not used by AuthInterceptor")

        override fun connectTimeoutMillis(): Int = 0

        override fun withConnectTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this

        override fun readTimeoutMillis(): Int = 0

        override fun withReadTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this

        override fun writeTimeoutMillis(): Int = 0

        override fun withWriteTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this
    }
}
