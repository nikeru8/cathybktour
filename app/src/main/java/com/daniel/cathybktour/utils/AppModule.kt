package com.daniel.cathybktour.utils

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.daniel.cathybktour.api.TaipeiTourService
import com.daniel.cathybktour.model.Language
import com.daniel.cathybktour.repository.ExploreFragmentRepository
import com.daniel.cathybktour.repository.MainActivityRepository
import com.daniel.cathybktour.repository.NewsFragmentRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideMainActivityRepository(taipeiTourService: TaipeiTourService): MainActivityRepository {
        return MainActivityRepository(taipeiTourService)
    }

    @Provides
    @Singleton
    fun provideExploreRepository(taipeiTourService: TaipeiTourService): ExploreFragmentRepository {
        return ExploreFragmentRepository(taipeiTourService)
    }

    @Provides
    @Singleton
    fun provideNewsRepository(taipeiTourService: TaipeiTourService): NewsFragmentRepository {
        return NewsFragmentRepository(taipeiTourService)
    }

    class LogJsonInterceptor : Interceptor {
        @Throws(IOException::class)

        override fun intercept(chain: Interceptor.Chain): Response {
            val request: Request = chain.request()
            val response: Response = chain.proceed(request)
            val rawJson: String? = response.body()?.string()

            Log.d(
                "TAG",
                String.format(
                    "call: ${response.request().url()} \n  , RetrofitManager raw JSON response is: %s",
                    rawJson
                )
            )

            // Re-create the response before returning it because body can be read only once
            return response.newBuilder()
                .body(ResponseBody.create(response.body()?.contentType(), rawJson.toString())).build()
        }
    }

    @Provides
    @Singleton
    fun provideLogJsonInterceptor(): LogJsonInterceptor {

        return LogJsonInterceptor()

    }

    @Provides
    @Singleton
    fun provideOkHttpClient(interceptor: LogJsonInterceptor): OkHttpClient {

        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()

    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {

        return Retrofit.Builder()
            .baseUrl("https://www.travel.taipei/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

    }

    @Provides
    @Singleton
    fun provideLanguages(): List<Language> {
        return listOf(
            Language("繁體中文", "zh-tw", true),//默認被選中
            Language("简体中文", "zh-cn"),
            Language("English", "en"),
            Language("日本語", "ja"),
            Language("한국어", "ko"),
            Language("Español", "es"),
            Language("ภาษาไทย", "th"),
            Language("Tiếng Việt", "vi")
        )
    }

    @Provides
    @Singleton
    fun provideTaipeiTourService(retrofit: Retrofit): TaipeiTourService {
        return retrofit.create(TaipeiTourService::class.java)
    }

    @Provides
    @Singleton
    fun provideTourItemDao(appDatabase: AppDatabase): TourItemDao {
        return appDatabase.tourItemDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "db_tour_data"
        ).build()
    }

}
