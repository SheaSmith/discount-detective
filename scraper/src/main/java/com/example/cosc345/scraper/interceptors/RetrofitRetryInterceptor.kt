package com.example.cosc345.scraper.interceptors

import okhttp3.Interceptor
import okhttp3.Response

class RetrofitRetryInterceptor : Interceptor {
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