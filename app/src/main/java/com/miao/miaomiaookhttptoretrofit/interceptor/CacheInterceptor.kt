package com.miao.miaomiaookhttptoretrofit.interceptor

import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.File
import java.io.IOException

/**
 * 首先，我们使用File对象定义了缓存目录，并指定了缓存大小。
 * 然后，我们使用这些参数创建了一个Cache对象。
 * 接下来，我们创建了OkHttpClient实例，并使用.cache()方法将缓存对象添加到客户端中。
 * 这样，OkHttp就会使用该缓存对象来处理请求和响应的缓存。
 * 然后，我们定义了一个名为CacheInterceptor的自定义缓存拦截器，它实现了Interceptor接口。
 * 在intercept方法中，我们检查请求头中是否包含no-cache标记，如果包含则禁用缓存，否则我们设置缓存时间为1分钟。
 * 通过添加或修改请求头中的Cache-Control字段，我们控制请求的缓存行为。
 * 最后，在创建OkHttpClient实例时，我们使用.addInterceptor()方法将自定义的缓存拦截器CacheInterceptor添加到客户端中。
 */

// 自定义缓存拦截器
class CacheInterceptor : Interceptor {
    // 设置缓存目录和大小
    val cacheDirectory = File("cacheDir", "http-cache")
    val cacheSize = 10 * 1024 * 1024 // 10 MB

    // 创建缓存对象
    val cache = Cache(cacheDirectory, cacheSize.toLong())

    // 创建OkHttpClient实例，并添加缓存
    val client = OkHttpClient.Builder()
        .cache(cache)
        .addInterceptor(CacheInterceptor())
        .build()

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        // 检查是否禁用缓存
        request = if (request.header("Cache-Control")?.contains("no-cache") == true) {
            request.newBuilder()
                .removeHeader("Pragma")
                .header("Cache-Control", "no-cache")
                .build()
        } else {
            // 设置缓存时间为1分钟
            val maxAge = 60

            request.newBuilder()
                .removeHeader("Pragma")
                .header("Cache-Control", "public, max-age=$maxAge")
                .build()
        }

        return chain.proceed(request)
    }
}
/**
 * 使用上述代码配置的缓存能够缓存HTTP请求的响应数据。
 * 具体来说，它将缓存具有Cache-Control标头的响应。
 * 根据Cache-Control标头的不同设置，缓存可以存储和提供以下内容：
 * 可缓存的响应：如果响应具有Cache-Control标头指定的缓存指令（如public或max-age），则该响应可以被缓存。
 * 私有的响应：如果响应具有Cache-Control标头中的private指令，那么该响应只能被缓存并供单个用户使用，不能被共享。
 * 只有请求成功的响应：如果响应具有Cache-Control标头中的must-revalidate或proxy-revalidate指令，那么缓存的响应将被视为陈旧，需要在发送请求之前重新验证。
 * 非缓存的响应：如果响应具有Cache-Control标头中的no-store指令，那么该响应将不会被缓存，每次请求都会从服务器获取最新的数据。
 * 需要注意的是，服务器也可以通过设置响应标头中的Expires字段来指定响应的过期时间，以及使用ETag或Last-Modified来进行缓存验证。
 * 根据以上规则，符合缓存条件的响应将会被缓存下来，
 * 当下次发起相同请求时，如果缓存未过期，将直接使用缓存的响应数据而不需要再次发送请求到服务器。这样可以提高性能和减少网络流量。
 */