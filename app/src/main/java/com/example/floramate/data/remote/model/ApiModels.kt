package com.example.floramate.data.remote.model

import com.google.gson.annotations.SerializedName

// ============================================
// REQUEST MODELS
// ============================================

data class PlantIdentifyRequest(
    val images: List<String>, // Base64 encoded images or URLs
    val latitude: Double? = null,
    val longitude: Double? = null,
    @SerializedName("similar_images")
    val similarImages: Boolean = false
)

// ============================================
// RESPONSE MODELS
// ============================================

data class PlantIdentifyResponse(
    @SerializedName("access_token")
    val accessToken: String,
    val result: IdentificationResult
)

data class IdentificationResult(
    @SerializedName("is_plant")
    val isPlant: IsPlantInfo,
    val classification: Classification
)

data class IsPlantInfo(
    val binary: Boolean,
    val probability: Double
)

data class Classification(
    val suggestions: List<PlantSuggestion>
)

data class PlantSuggestion(
    val id: String,
    val name: String,
    val probability: Double,
    val details: PlantDetails?
)

// ============================================
// PLANT DETAILS
// ============================================

data class PlantDetails(
    @SerializedName("common_names")
    val commonNames: List<String>?,
    val taxonomy: Taxonomy?,
    val url: String?,
    val description: Description?,
    val watering: WateringInfo?,
    @SerializedName("best_light_condition")
    val bestLightCondition: String?,
    @SerializedName("best_watering")
    val bestWatering: String?,
    @SerializedName("best_soil_type")
    val bestSoilType: String?,
    val toxicity: String?,
    @SerializedName("edible_parts")
    val edibleParts: List<String>?,
    @SerializedName("propagation_methods")
    val propagationMethods: List<String>?,
    val image: ImageInfo?,
    val synonyms: List<String>?
)

data class Taxonomy(
    val kingdom: String?,
    val phylum: String?,
    @SerializedName("class")
    val taxonomyClass: String?,
    val order: String?,
    val family: String?,
    val genus: String?,
    val species: String?
)

data class Description(
    val value: String?,
    val citation: String?,
    @SerializedName("license_name")
    val licenseName: String?,
    @SerializedName("license_url")
    val licenseUrl: String?
)

data class WateringInfo(
    val min: Int?,
    val max: Int?
)

data class ImageInfo(
    val value: String?,
    @SerializedName("license_name")
    val licenseName: String?,
    @SerializedName("license_url")
    val licenseUrl: String?
)