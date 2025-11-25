package com.example.floramate.data.remote.api

import com.example.floramate.data.remote.model.PlantIdentifyRequest
import com.example.floramate.data.remote.model.PlantIdentifyResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface PlantApiService {

    @POST("identification")
    @Headers("Api-Key: H4g9e1SjAT7HTmOuyjxk5AF8fy5d43ZriDMuAhMEIkNA20vgf7")
    suspend fun identifyPlant(
        @Body request: PlantIdentifyRequest,
        @Query("details") details: String = "common_names,url,description,taxonomy,watering,best_light_condition,best_watering,best_soil_type,toxicity,edible_parts,propagation_methods",
        @Query("language") language: String = "en"
    ): PlantIdentifyResponse

    @Multipart
    @POST("identification")
    @Headers("Api-Key: H4g9e1SjAT7HTmOuyjxk5AF8fy5d43ZriDMuAhMEIkNA20vgf7")
    suspend fun identifyPlantMultipart(
        @Part image: MultipartBody.Part,
        @Query("details") details: String = "common_names,url,description,taxonomy,watering,best_light_condition,best_watering,best_soil_type,toxicity,edible_parts,propagation_methods",
        @Query("language") language: String = "en"
    ): PlantIdentifyResponse

    @GET("identification/{access_token}")
    @Headers("Api-Key: H4g9e1SjAT7HTmOuyjxk5AF8fy5d43ZriDMuAhMEIkNA20vgf7")
    suspend fun getIdentificationResult(
        @Path("access_token") accessToken: String,
        @Query("details") details: String = "common_names,url,description,taxonomy,watering,best_light_condition,best_watering",
        @Query("language") language: String = "en"
    ): PlantIdentifyResponse
}