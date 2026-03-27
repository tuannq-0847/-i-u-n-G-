package org.example.project.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.example.project.data.LocationInfo
import org.example.project.data.PlaceSearchResponse
import org.example.project.data.SearchRequest

class PlaceApiService(private val httpClient: HttpClient, private val httpClient2: HttpClient) {

    // Example: Fetching places using the Supabase URL structure
    suspend fun getPlaces(location: String, endpoint: String = "places"): List<LocationInfo> {
        // The specific endpoint path and query parameters
        val selectQuery =
            "id,name,description,location,city,rating,tags,services,images,lat,long,open_time,close_time,social_phone,social_website,social_facebook,social_instagram,social_menu"
        val orderQuery = "id.desc"

        return try {
            httpClient.get(endpoint) {
                parameter("select", selectQuery)
                parameter("order", orderQuery)
                parameter("city", location)
                // Add any other necessary parameters or headers here
            }.body() // Ktor will deserialize the JSON array into List<LocationInfo>
        } catch (e: Exception) {
            // Handle exceptions (e.g., network errors, serialization errors)
            // Log the error, rethrow a custom domain exception, or return an empty list/error state
            println("Error fetching places: ${e.message}")
            throw e // Or handle more gracefully
        }
    }

    // Example: Fetching places using the Supabase URL structure
    suspend fun getCFPlaces(
        location: String,
        request: SearchRequest,
        endpoint: String = "brands"
    ): PlaceSearchResponse {

        return try {
            httpClient2.post(endpoint) {
                contentType(ContentType.Application.Json)
                setBody(request)
                // Add any other necessary parameters or headers here
            }.body() // Ktor will deserialize the JSON array into List<LocationInfo>
        } catch (e: Exception) {
            // Handle exceptions (e.g., network errors, serialization errors)
            // Log the error, rethrow a custom domain exception, or return an empty list/error state
            println("Error fetching places: ${e.message}")
            throw e // Or handle more gracefully
        }
    }
}