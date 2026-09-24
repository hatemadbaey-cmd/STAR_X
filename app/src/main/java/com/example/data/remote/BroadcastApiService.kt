package com.example.data.remote

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface BroadcastApiService {

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<BaseApiResponse>

    @GET("get_data")
    suspend fun getData(
        @Header("Authorization") token: String
    ): Response<GetDataResponse>

    @POST("add_device")
    suspend fun addDevice(
        @Header("Authorization") token: String,
        @Body device: DeviceNetworkDto
    ): Response<BaseApiResponse>

    @POST("update_device")
    suspend fun updateDevice(
        @Header("Authorization") token: String,
        @Body device: DeviceNetworkDto
    ): Response<BaseApiResponse>

    @POST("update_status")
    suspend fun updateStatus(
        @Header("Authorization") token: String,
        @Body request: UpdateStatusRequest
    ): Response<BaseApiResponse>

    @FormUrlEncoded
    @POST("delete_device")
    suspend fun deleteDevice(
        @Header("Authorization") token: String,
        @Field("id") id: Long
    ): Response<BaseApiResponse>

    @POST("ping_device")
    suspend fun pingDevice(
        @Header("Authorization") token: String,
        @Body request: PingRequest
    ): Response<PingResponse>

    @FormUrlEncoded
    @POST("add_user")
    suspend fun addUser(
        @Header("Authorization") token: String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("role") role: String
    ): Response<BaseApiResponse>

    @FormUrlEncoded
    @POST("delete_user")
    suspend fun deleteUser(
        @Header("Authorization") token: String,
        @Field("id") id: Long
    ): Response<BaseApiResponse>

    @FormUrlEncoded
    @POST("change_password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Field("old_password") oldPass: String,
        @Field("new_password") newPass: String
    ): Response<BaseApiResponse>

    @POST("backup_db")
    suspend fun backupDb(
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @GET("export_excel")
    suspend fun exportExcel(
        @Header("Authorization") token: String
    ): Response<ResponseBody>
}

object ApiClient {
    private const val BASE_URL = "https://your-domain.com/api/"

    fun createService(): BroadcastApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        return retrofit.create(BroadcastApiService::class.java)
    }
}
