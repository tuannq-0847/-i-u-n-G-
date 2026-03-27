package org.example.project.extension

import org.example.project.data.LocationInfo
import org.example.project.data.PlaceSearchResponse

fun String.encodeURIComponent(): String =
    encodeToByteArray()
        .joinToString("") { byte ->
            val c = byte.toInt().toChar()
            when {
                c.isLetterOrDigit() || c == '-' || c == '_' || c == '.' || c == '~' -> c.toString()
                else -> "%${byte.toInt().and(0xFF).toString(16).uppercase()}"
            }
        }

fun String.decodeURIComponent(): String {
    return buildString {
        var i = 0
        while (i < length) {
            val c = this[i]
            if (c == '%' && i + 2 < length) {
                val hex = substring(i + 1, i + 3)
                append(hex.toInt(16).toChar())
                i += 3
            } else {
                append(c)
                i++
            }
        }
    }
}

fun String.isVideo(): Boolean {
    return this.endsWith(".mp4", ignoreCase = true) ||
            this.endsWith(".m4v", ignoreCase = true) ||
            this.endsWith(".mov", ignoreCase = true)
}

fun PlaceSearchResponse.mapToLocationList(): List<LocationInfo>{
    return result?.rows?.map { place ->
        LocationInfo(
            location = place.address ?: "",
            description = place.motivations,
            priceAvg = place.priceAverage?.toString(),
            services = place.parking,
            images = place.previewMedias?.joinToString(","),
            lat = place.latitude,
            long = place.longitude,
            openTime = place.openTimeBySeconds?.toHHMM().orEmpty(),
            closeTime = place.closeTimeBySeconds?.toHHMM().orEmpty(),
            socialPhone = place.hotline,
            socialWebsite = null, // Assuming no website field in Place
            socialFacebook = null, // Assuming no Facebook field in Place
            socialInstagram = null, // Assuming no Instagram field in Place
            socialX = null, // Assuming no X (Twitter) field in Place
            socialMenu = null // Assuming no menu field in Place
        ).apply {
            name = place.name ?: ""
            city = place.city ?: ""
            id = place.id ?: ""
            slug = place.slug ?: ""
            coverImage = place.coverImage ?: ""
            ward = place.ward ?: ""
            district = place.district ?: ""
//            tags = place.tags?.joinToString(",") ?: ""
//            tagsList = place.tags?.split(',')?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
//            imageUrlList = place.previewMedias?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()

        }
    } ?: emptyList()
}

fun Int.toHHMM(): String {
    val totalMinutes = this / 60
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60

    val h = hours.toString().padStart(2, '0')
    val m = minutes.toString().padStart(2, '0')

    return "$h:$m"
}

fun String.toVND(): String {
    val number = this.toLongOrNull() ?: return this // fallback if not a number
    return number.formatWithThousandsSeparator() + " VND"
}

private fun Number.formatWithThousandsSeparator(): String {
    val str = this.toString()
    val result = StringBuilder()
    var count = 0

    for (i in str.length - 1 downTo 0) {
        result.insert(0, str[i])
        count++
        if (count % 3 == 0 && i != 0) {
            result.insert(0, '.')
        }
    }

    return result.toString()
}