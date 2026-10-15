// SPDX-License-Identifier: Apache-2.0
// Adapted from CoMaps ge0/parser.cpp; Kotlin port with share-text and geo: support.
// See THIRD_PARTY_NOTICES.md and NOTICE for upstream attribution.
package app.openincomaps

import java.net.URI
import java.net.URLDecoder

/** CoMaps ge0 wire format: libs/ge0/parser.cpp (30-bit interleaved coordinates). */
object CoMapsShareParser {
    private const val ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_"
    private val prefixes = listOf(
        "https://comaps.at/", "http://comaps.at/", "comaps://", "cm://",
        "ge0://", "https://ge0.me/", "http://ge0.me/",
    )
    private val linkPattern = Regex(
        "(?:https?://|comaps://|cm://|ge0://|geo:)[^\\s<>\\\"']+",
        RegexOption.IGNORE_CASE,
    )
    private const val NUMBER = "[+-]?(?:\\d+(?:\\.\\d*)?|\\.\\d+)"
    private val pair = Regex("^($NUMBER)\\s*,\\s*($NUMBER)(?:\\([^)]*\\))?$")

    fun extract(sharedTexts: Iterable<String>): Coordinates? = sharedTexts.firstNotNullOfOrNull { text ->
        linkPattern.findAll(text).firstNotNullOfOrNull { match ->
            parse(match.value.trimEnd('.', ',', ';'))
        }
    }

    fun parse(link: String): Coordinates? {
        if (link.startsWith("geo:", ignoreCase = true)) return parseGeo(link)
        val prefix = prefixes.firstOrNull { link.startsWith(it, ignoreCase = true) } ?: return null
        val payload = link.substring(prefix.length)
        if (payload.length < 10 || (payload.length > 10 && payload[10] != '/')) return null
        if (payload.take(10).any { it !in ALPHABET }) return null

        var latitudeBits = 0L
        var longitudeBits = 0L
        payload.substring(1, 10).forEachIndexed { index, char ->
            val value = ALPHABET.indexOf(char)
            val latitude = ((value shr 5 and 1) shl 2) or
                ((value shr 3 and 1) shl 1) or (value shr 1 and 1)
            val longitude = ((value shr 4 and 1) shl 2) or
                ((value shr 2 and 1) shl 1) or (value and 1)
            val shift = 27 - index * 3
            latitudeBits = latitudeBits or (latitude.toLong() shl shift)
            longitudeBits = longitudeBits or (longitude.toLong() shl shift)
        }
        return coordinates(
            (latitudeBits + 4).toDouble() / 1_073_741_823 * 180 - 90,
            (longitudeBits + 4).toDouble() / 1_073_741_824 * 360 - 180,
        )
    }

    private fun parseGeo(link: String): Coordinates? {
        val uri = runCatching { URI(link) }.getOrNull() ?: return null
        val body = uri.rawSchemeSpecificPart
        val parts = body.split('?', limit = 2)
        // Prefer numeric q= coordinates (the primary pair may intentionally be 0,0).
        if (parts.size == 2) {
            val query = parts[1].split('&').firstOrNull { it.substringBefore('=') == "q" }
            if (query != null) {
                val decoded = runCatching {
                    URLDecoder.decode(query.substringAfter('=', ""), "UTF-8")
                }.getOrNull() ?: return null
                parsePair(decoded)?.let { return it }
            }
        }
        // Accept an optional altitude but never reinterpret a text search as coordinates.
        val primary = parts[0].substringBefore(';').split(',').take(2).joinToString(",")
        val result = parsePair(primary) ?: return null
        if (result.latitude == 0.0 && result.longitude == 0.0 &&
            parts.getOrNull(1)?.split('&')?.any { it.startsWith("q=") } == true
        ) return null
        return result
    }

    private fun parsePair(value: String): Coordinates? {
        val match = pair.matchEntire(value) ?: return null
        return coordinates(
            match.groupValues[1].toDoubleOrNull() ?: return null,
            match.groupValues[2].toDoubleOrNull() ?: return null,
        )
    }

    private fun coordinates(latitude: Double, longitude: Double): Coordinates? =
        runCatching { Coordinates(latitude, longitude) }.getOrNull()
}
