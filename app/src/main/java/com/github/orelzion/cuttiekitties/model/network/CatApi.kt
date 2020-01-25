package com.github.orelzion.cuttiekitties.model.network

import com.github.orelzion.cuttiekitties.BuildConfig
import com.github.orelzion.cuttiekitties.model.network.entity.BreedResponse
import com.github.orelzion.cuttiekitties.model.network.entity.CatImageResponse
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

interface CatApi {

    companion object {
        val PAGE_SIZE = 20
    }

    @GET("/v1/images/search")
    suspend fun fetchImagesByBreed(
        @Query("breed_id") breed_id: String,
        @Query("limit") limit: Int = 20,
        @Query("page") page: Int
    ): CatImageResponse

    @GET("/v1/breeds")
    suspend fun fetchBreedsList(
        @Query("limit") limit: Int = 20,
        @Query("page") page: Int
    ): BreedResponse
}


private val level =
    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
private val loggingInterceptor = HttpLoggingInterceptor().setLevel(level)

private val client = OkHttpClient.Builder()
    .addInterceptor { chain ->
        val request = chain.request()
            .newBuilder()
            .addHeader("x-api-key", BuildConfig.CAT_API_KEY)
            .build()
        chain.proceed(request)
    }
    .addInterceptor(loggingInterceptor)
    .build()


val catApi = Retrofit.Builder()
    .baseUrl("https://api.thecatapi.com/")
    .addConverterFactory(Json.nonstrict.asConverterFactory("application/json".toMediaType()))
    .client(client)
    .build()
    .create(CatApi::class.java)