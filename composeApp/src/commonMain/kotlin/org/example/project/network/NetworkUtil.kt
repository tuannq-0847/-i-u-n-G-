package org.example.project.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.example.project.getPlatform

object NetworkUtil {
    // Replace with your actual Supabase URL and Anon Key
    private const val BASE_URL = "https://qbunvydwuhojmesfwsoy.supabase.co/rest/v1/"
    private const val BASE_URL2 = "https://api.dicaphekhong.com/api/search/"
    private const val SUPABASE_ANON_KEY =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFidW52eWR3dWhvam1lc2Z3c295Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mzk2MTcwOTAsImV4cCI6MjA1NTE5MzA5MH0.8T0jHCmTCIwin7UaX2TjKnkpUVYcq71DW2LlJGTToos" // IMPORTANT: Store this securely

    val httpClient = HttpClient { // Or HttpClient(OkHttp)
        // For Logging Network Calls (Optional)
        install(Logging) {
            level = LogLevel.ALL
        }
        // For JSON Deserialization
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true // Important for APIs that might add new fields
            })
        }

        addRequest1(this)
    }

    val httpClient2 = HttpClient { // Or HttpClient(OkHttp)
        // For Logging Network Calls (Optional)
        install(Logging) {
            level = LogLevel.ALL
        }
        // For JSON Deserialization
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true // Important for APIs that might add new fields
            })
        }

        addRequest2(this)
    }

    private fun <T : HttpClientEngineConfig> addRequest2(client: HttpClientConfig<T>) {
// Default request parameters (e.g., base URL, common headers)
        client.defaultRequest {
            url(BASE_URL2)
            // You might also need a Content-Type header for POST/PUT requests
            // header(HttpHeaders.ContentType, ContentType.Application.Json)
            // ---- Mimicking Common Browser Headers ----
            header(
                HttpHeaders.UserAgent,
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36" // Example User-Agent for a recent Chrome on Windows
            )
            header(
                HttpHeaders.Accept,
                "application/json, text/plain, */*"
            )
            header(HttpHeaders.AcceptLanguage, "en-US,en;q=0.9") // Example: US English preferred
//            if (!getPlatform().name.startsWith("Android"))
//                header(
//                    HttpHeaders.AcceptEncoding,
//                    "gzip, deflate, br, zstd"
//                ) // Modern browsers support zstd
            header(
                HttpHeaders.Origin,
                "https://app.dicaphekhong.com"
            )
            header(
                HttpHeaders.Referrer,
                "https://app.dicaphekhong.com"
            )
            header(
                HttpHeaders.AcceptLanguage,
                "en-US,en;q=0.5"
            )
        }
    }

    private fun <T : HttpClientEngineConfig> addRequest1(client: HttpClientConfig<T>) {
// Default request parameters (e.g., base URL, common headers)
        client.defaultRequest {
            url(BASE_URL)
            // Add Supabase required headers
            header("apikey", SUPABASE_ANON_KEY)
            header("Authorization", "Bearer $SUPABASE_ANON_KEY")
            // You might also need a Content-Type header for POST/PUT requests
            // header(HttpHeaders.ContentType, ContentType.Application.Json)
            // ---- Mimicking Common Browser Headers ----
            header(
                HttpHeaders.UserAgent,
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36" // Example User-Agent for a recent Chrome on Windows
            )
            header(
                HttpHeaders.Accept,
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7"
            )
            header(HttpHeaders.AcceptLanguage, "en-US,en;q=0.9") // Example: US English preferred
//            if (!getPlatform().name.startsWith("Android"))
//                header(
//                    HttpHeaders.AcceptEncoding,
//                    "gzip, deflate, br, zstd"
//                ) // Modern browsers support zstd
        }
    }
}
