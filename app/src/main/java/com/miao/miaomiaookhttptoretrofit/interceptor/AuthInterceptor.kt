package com.miao.miaomiaookhttptoretrofit

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * 我们创建了一个名为AuthInterceptor的拦截器
 * 它接受一个authToken参数作为身份验证的令牌。
 * 在intercept方法中，
 * 我们获取原始请求，
 * 并使用newBuilder()方法创建一个新的请求构建器。
 * 然后，我们使用header方法添加一个名为"Authorization"的头部，
 * 并将Bearer Token附加到头部的值中。
 * 最后，我们使用build()方法构建新的请求，
 * 并通过chain.proceed()方法将其传递给下一个拦截器或执行网络请求。
 */

class AuthInterceptor(private val authToken: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()

        // 添加身份验证头部
        val authenticatedRequest: Request = originalRequest.newBuilder()
            .header("Authorization", "Bearer $authToken")
            .build()

        return chain.proceed(authenticatedRequest)
    }
}
