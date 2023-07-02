package com.miao.miaomiaookhttptoretrofit.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * 我们创建了一个名为RetryInterceptor的拦截器，
 * 它接受一个maxRetries参数来指定最大重试次数。
 * 在intercept方法中，我们使用一个循环来尝试执行请求，并在捕获异常时增加重试计数。
 * 如果请求成功或达到最大重试次数后仍然失败，我们返回最终的响应或抛出异常。
 */

class RetryInterceptor(private val maxRetries: Int) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        var response: Response? = null
        var retryCount = 0

        while (response == null && retryCount < maxRetries) {
            try {
                response = chain.proceed(request)
            } catch (e: Exception) {
                retryCount++
                request = request.newBuilder().build()
            }
        }

        return response ?: throw IllegalStateException("Request failed after $maxRetries retries")
    }
}
