package org.example.project.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocationInfo(
    val location: String,
    val description: String? = null,
    val priceAvg: String? = null,      // Kept as String due to "4,4" format; parse manually if needed
    val services: String? = null,
    val images: String? = null,      // Comma-separated URLs as a string
    val lat: Double? = null,
    @SerialName("long")          // 'long' is a keyword in Kotlin, annotation helps mapping
    val long: Double? = null,   // Renamed from 'long' to 'longitude'
    @SerialName("open_time")
    val openTime: String? = null,
    @SerialName("close_time")
    val closeTime: String? = null,
    @SerialName("social_phone")
    val socialPhone: String? = null,
    @SerialName("social_website")
    val socialWebsite: String? = null,
    @SerialName("social_facebook")
    val socialFacebook: String? = null,
    @SerialName("social_instagram")
    val socialInstagram: String? = null,
    @SerialName("social_X")      // Annotation for mapping from JSON key "social_X"
    val socialX: String? = null,     // Property named in camelCase
    @SerialName("social_menu")
    val socialMenu: String? = null
): Place() {
    val imageUrlList: List<String>
        get() = images
            ?.split(',')
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()

    val tagsList: List<String>
        get() = tags
            ?.split(',')
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()
}

// The entire response would be a list of these LocationInfo objects:
// val locations: List<LocationInfo> = ...