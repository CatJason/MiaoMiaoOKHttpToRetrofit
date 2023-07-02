import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.miao.miaomiaookhttptoretrofit.AuthInterceptor
import com.miao.miaomiaookhttptoretrofit.R
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
        val requestBody = jsonString.toRequestBody(mediaType)

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