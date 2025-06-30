package org.example.project.data

import kotlinx.serialization.Serializable

@Serializable
data class SearchRequest(
    val page: Int,
    val limit: Int,
    val lat: Double? = null,
    val lng: Double? = null,
    val order: String = mapOf("distance" to "asc").toString(),
    val q: String? = null
)
