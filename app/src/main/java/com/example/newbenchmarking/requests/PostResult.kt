package com.example.newbenchmarking.requests

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class Phone(
    val brand_name: String,
    val manufacturer: String,
    val phone_model: String,
    val total_ram: Int
)

data class Inference(
    val init_speed: Int,
    val inf_speed: Int?,
    val first_inf_speed: Int?,
    val standard_deviation: Int?,
    val ml_model: String,
    val category: String,
    val quantization: String,
    val dataset: String,
    val num_images: Int,
    val uses_nnapi: Boolean,
    val uses_gpu: Boolean,
    val num_threads: Int,
    val ram_usage: Int,
    val gpu_usage: Int?,
    val cpu_usage: Int,
    val gpu: String?,
    val cpu: String?,
    val android_id: String
)

data class PostData(
    val phone: Phone,
    val inference: Inference
)

interface ApiService {
    @POST("inference")
    suspend fun createPost(@Body postData: PostData): Response<Inference>
}

val retrofit = Retrofit.Builder()
    .baseUrl("http://localhost:3030/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val apiService = retrofit.create(ApiService::class.java)

fun postResult(postData: PostData) {
    GlobalScope.launch(Dispatchers.IO) {
        try {
            val response: Response<Inference> = apiService.createPost(postData)
            if (response.isSuccessful) {
                Log.d("Post success", "Sent result over network")
            } else {
                Log.d("Post failed", "Post failed " + response.code())
            }
        } catch (e: Exception) {
                Log.e("Post error", e.toString())
        }
    }
}


