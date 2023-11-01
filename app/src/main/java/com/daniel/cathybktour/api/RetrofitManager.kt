package com.daniel.cathybktour.api

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class RetrofitManager {

    companion object {

        val manager = RetrofitManager()

        var from = ""

        //會員
        fun callTaipeiTourService(from: String): TaipeiTourService {
            this.from = from
            return manager.tourService
        }

    }

    class LogJsonInterceptor : Interceptor {
        @Throws(IOException::class)

        override fun intercept(chain: Interceptor.Chain): Response {
            val request: Request = chain.request()
            val response: Response = chain.proceed(request)
            val rawJson: String? = response.body()?.string()

            Log.d(
                "TAG",
                String.format("call: ${response.request().url()} \n ,from - $from , RetrofitManager raw JSON response is: %s", rawJson)
            )

            // Re-create the response before returning it because body can be read only once
            return response.newBuilder()
                .body(ResponseBody.create(response.body()?.contentType(), rawJson)).build()
        }
    }


    var mOkHttpClient = OkHttpClient.Builder()
//        .cache(Cache(context.getCacheDir(), cacheSize)) //設置緩存目錄大小
        .addInterceptor(LogJsonInterceptor())
        .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS) //Connect Time 15s
        .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS) //Read Time 20s
        .writeTimeout(20, java.util.concurrent.TimeUnit.SECONDS) //Write Time 20s
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.travel.taipei/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(mOkHttpClient)
        .build()


    //旅遊資訊
    private val tourService: TaipeiTourService = retrofit.create(TaipeiTourService::class.java)

}