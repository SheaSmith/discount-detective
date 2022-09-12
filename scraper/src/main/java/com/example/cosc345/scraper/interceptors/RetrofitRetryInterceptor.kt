package com.example.cosc345.scraper.interceptors

import okhttp3.Interceptor
import okhttp3.Response

/**
 * An interceptor which automatically retries a request in case of transient server errors.
 */
class RetrofitRetryInterceptor : Interceptor {
    /**
     * Intercept the request, and repeat it if it fails.
     *
     * @param chain The request chain so far.
     * @return The response from the HTTP endpoint.
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response: Response? = null
        var responseSuccessful = false
        var tryCount = 0

        while (!responseSuccessful && tryCount < 5) {
            try {
                response?.close()
                response = chain.proceed(request)
                responseSuccessful = response!!.isSuccessful
            } finally {
                tryCount++
            }
        }

        return response!!
    }
}