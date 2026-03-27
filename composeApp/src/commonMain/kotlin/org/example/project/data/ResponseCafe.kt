package org.example.project.data

import kotlinx.serialization.Serializable

@Serializable
data class PlaceSearchResponse(
    val result: PlaceResult? = null
)

@Serializable
data class PlaceResult(
    val meta: Meta,
    val rows: List<Place>
)

@Serializable
data class Meta(
    val page: Int,
    val perPage: Int,
    val total: Int
)

@Serializable
open class Place {
    open var id: String? = null
    open var slug: String? = null
    open var name: String? = null
    open var coverImage: String? = null
    open var address: String? = null
    open var ward: String? = null
    open var district: String? = null
    open var city: String? = null
    open var hotline: String? = null
    open var tags: String? = null
    open var motivations: String? = null
    open var parking: String? = null
    open var openTimeBySeconds: Int? = null
    open var closeTimeBySeconds: Int? = null
    open var priceAverage: Int? = null
    open var latitude: Double? = null
    open var longitude: Double? = null
    open var vouchers: List<String> = emptyList()
    open var is24h: Boolean? = null
    open var verified: Boolean? = null
    open var isSponsor: Boolean? = null
    open var previewMedias: List<String>? = null
}