package com.example.newbenchmarking.requests

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.newbenchmarking.BuildConfig
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

data class Phone(
    val brand_name: String,
    val manufacturer: String,
    val phone_model: String,
    val total_ram: Int
)

data class Inference(
    val init_speed: Int?,
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
    val ram_usage: Int?,
    val gpu_usage: Int?,
    val cpu_usage: Int?,
    val gpu: String?,
    val cpu: String?,
    val android_id: String,
    val errorMessage: String?
)

data class PostData(
    val phone: Phone,
    val inference: Inference
)

interface ApiService {
    @POST("inference")
    suspend fun createPost(@Body encryptedData: Map<String, String>): Response<Any>
}

const val apiAdress = BuildConfig.API_ADRESS

val retrofit = Retrofit.Builder()
    .baseUrl("$apiAdress/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val apiService = retrofit.create(ApiService::class.java)

const val secretKeyString = BuildConfig.API_KEY

@RequiresApi(Build.VERSION_CODES.O)
fun encryptAndPostResult(postData: PostData) {
    // Convert PostResult object to JSON using Gson
    val gson = Gson()
    val postDataJson = gson.toJson(postData)

    // Encrypt the JSON string
    val encryptedData = encryptData(postDataJson, secretKeyString)

    // Prepare the encrypted data to send in a JSON format
    val encryptedDataMap = mapOf("encryptedData" to encryptedData)

    // Send the encrypted data to the server
    GlobalScope.launch(Dispatchers.IO) {
        try {
            val response: Response<Any> = apiService.createPost(encryptedDataMap)
            if (response.isSuccessful) {
                Log.d("post", "Sent encrypted result over network")
            } else {
                Log.d("post", "Post failed " + response.code())
            }
        } catch (e: Exception) {
            Log.e("post", e.toString())
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun encryptData(plainText: String, stringKey: String): String {
    // Decode the Base64-encoded key string to get the key bytes
    val keyBytes = Base64.getDecoder().decode(stringKey)
    val secretKey = SecretKeySpec(keyBytes, "AES")  // Use the full keyBytes (should be 32 bytes for AES-256)

    val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
    val iv = ByteArray(16)
    SecureRandom().nextBytes(iv)
    val ivParameterSpec = IvParameterSpec(iv)

    cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)

    val encryptedData = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

    val ivBase64 = Base64.getEncoder().encodeToString(iv)
    val encryptedDataBase64 = Base64.getEncoder().encodeToString(encryptedData)

    return "$ivBase64:$encryptedDataBase64"
}


