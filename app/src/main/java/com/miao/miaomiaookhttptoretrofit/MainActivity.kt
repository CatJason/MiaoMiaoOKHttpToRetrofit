import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.miao.miaomiaookhttptoretrofit.AuthInterceptor
import com.miao.miaomiaookhttptoretrofit.R
import com.miao.miaomiaookhttptoretrofit.interceptor.CacheInterceptor
import com.miao.miaomiaookhttptoretrofit.interceptor.RetryInterceptor
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    companion object {
        const val TIME = 5L
        const val RETRY_TIME = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun retrofitHttpPost() {
        val url = "https://example.com/api/"
        if (url.isBlank()) {
            return
        }
        url.toHttpUrlOrNull() ?: return

        val jsonString = ""
        val mediaType = "application/json; charset=utf-8".toMediaType()

        /**
         * "application/json"：JSON数据的媒体类型。
         * "application/xml"：XML数据的媒体类型。
         * "application/x-www-form-urlencoded"：URL编码表单数据的媒体类型。
         * "multipart/form-data"：多部分表单数据的媒体类型。
         * "text/plain"：纯文本数据的媒体类型。
         * "image/jpeg"：JPEG图片的媒体类型。
         * "image/png"：PNG图片的媒体类型。
         * "audio/mp3"：MP3音频的媒体类型。
         * "video/mp4"：MP4视频的媒体类型。
         */

        val requestBody = jsonString.toRequestBody(mediaType)

        /**
         * RequestBody
         * 当你需要发送自定义的请求体数据，并且以上具体子类无法满足你的需求时，可以通过继承RequestBody类来实现自定义的RequestBody子类。
         * RequestBody的子类
         * FormBody或MultipartBody
         * 当你需要发送简单的文本或键值对数据作为请求体时，可以使用FormBody或MultipartBody，提供了方便的方法来构建表单数据或多部分数据。
         * JsonBody
         * 当你需要发送JSON格式数据作为请求体时，可以使用JsonBody，它是RequestBody的具体子类，专门用于处理JSON数据。
         * FileBody、ByteArrayBody或StreamBody
         * 当你需要发送文件、字节数组、输入流等二进制数据作为请求体时，可以使用FileBody、ByteArrayBody或StreamBody，它们是RequestBody的具体子类，分别用于处理不同的二进制数据类型。
         */

        val headers = Headers.Builder().apply {
            add("name", "value")
            add("Content-Type", "application/json")
            add("Accept", "application/json")
        }.build()

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authToken = "your_auth_token"
        val authInterceptor = AuthInterceptor(authToken)

        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(
                OkHttpClient.Builder()
                    .connectTimeout(TIME, TimeUnit.SECONDS)
                    .readTimeout(TIME, TimeUnit.SECONDS)
                    .writeTimeout(TIME, TimeUnit.SECONDS)
                    .addInterceptor(RetryInterceptor(RETRY_TIME))
                    .addInterceptor(CacheInterceptor())
                    .addInterceptor(authInterceptor)
                    .addInterceptor(loggingInterceptor)
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)
        val call = service.postData(headers, requestBody)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // 处理请求失败的情况
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                // 处理请求成功的情况
            }
        })
    }
}

interface ApiService {
    @POST("users")
    fun postData(@HeaderMap headers: Headers, @Body requestBody: RequestBody): Call<ResponseBody>
}