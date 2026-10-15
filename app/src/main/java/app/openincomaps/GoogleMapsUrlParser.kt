package app.openincomaps

import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

object GoogleMapsUrlParser {
    private const val NUMBER = "[+-]?\\d{1,3}(?:\\.\\d+)?"

    private val urlPattern = Regex("https?://[^\\s<>\\\"']+", RegexOption.IGNORE_CASE)
    private val routeDestinationPattern = parameterPattern("destination|daddr")
    private val explicitQueryPattern = parameterPattern("q|query")
    private val viewportParameterPattern = parameterPattern("ll|center")
    private val coordinatePairPattern = Regex("($NUMBER)\\s*,\\s*($NUMBER)")
    private val exactPlacePattern = Regex("!3d($NUMBER)!4d($NUMBER)", RegexOption.IGNORE_CASE)
    private val alternatePlacePattern = Regex("!2d($NUMBER)!3d($NUMBER)", RegexOption.IGNORE_CASE)
    private val viewportPattern = Regex("@($NUMBER),($NUMBER)(?:[,/?]|$)", RegexOption.IGNORE_CASE)
    private val placePathPattern = Regex("/maps/place(?:/|[?#]|$)", RegexOption.IGNORE_CASE)
    private val googleDomainPattern = Regex(
        "^(?:www\\.|maps\\.)?google\\.(?:com|[a-z]{2}|(?:com|co)\\.[a-z]{2})$",
        RegexOption.IGNORE_CASE,
    )

    fun extractCoordinates(sharedText: String): Coordinates? =
        extractBestFromUrls(extractGoogleMapsUrls(sharedText))

    internal fun extractBestFromUrls(urls: Iterable<String>): Coordinates? =
        urls.mapIndexedNotNull { index, url ->
            extractCandidate(url)?.let { RankedCandidate(it, index) }
        }.maxWithOrNull(
            compareBy<RankedCandidate> { it.candidate.source.priority }
                .thenBy { it.index },
        )?.candidate?.coordinates

    fun extractGoogleMapsUrls(sharedText: String): List<String> =
        urlPattern.findAll(sharedText)
            .map { it.value.trimEnd('.', ',', ';', ':', ')', ']', '}') }
            .filter(::isGoogleMapsUrl)
            .distinct()
            .toList()

    fun extractFromUrl(url: String): Coordinates? = extractCandidate(url)?.coordinates

    fun isShortGoogleMapsUrl(url: String): Boolean {
        val uri = parseUri(url) ?: return false
        val host = uri.host?.lowercase() ?: return false
        return host == "maps.app.goo.gl" ||
            (host == "goo.gl" && uri.path.orEmpty().startsWith("/maps"))
    }

    fun isGoogleMapsUrl(url: String): Boolean {
        val uri = parseUri(url) ?: return false
        if (uri.scheme?.lowercase() !in setOf("http", "https")) return false

        val host = uri.host?.lowercase() ?: return false
        if (host == "maps.app.goo.gl") return true
        if (host == "goo.gl") return uri.path.orEmpty().startsWith("/maps")
        if (!googleDomainPattern.matches(host)) return false

        return host.startsWith("maps.") || uri.path.orEmpty().startsWith("/maps")
    }

    private fun extractCandidate(url: String): CoordinateCandidate? {
        if (!isGoogleMapsUrl(url)) return null
        val decoded = decodeUrl(url)

        coordinatesFromParameter(routeDestinationPattern, decoded)?.let {
            return CoordinateCandidate(it, CoordinateSource.ROUTE_DESTINATION)
        }

        lastCoordinates(exactPlacePattern, decoded, swap = false)?.let {
            return CoordinateCandidate(it, CoordinateSource.EXACT_DATA_MARKER)
        }
        lastCoordinates(alternatePlacePattern, decoded, swap = true)?.let {
            return CoordinateCandidate(it, CoordinateSource.EXACT_DATA_MARKER)
        }

        coordinatesFromParameter(explicitQueryPattern, decoded)?.let {
            return CoordinateCandidate(it, CoordinateSource.EXPLICIT_QUERY)
        }

        // In a place URL, viewport coordinates describe the visible map area and can
        // differ substantially from the shared POI. Never present them as the target.
        if (placePathPattern.containsMatchIn(decoded)) return null

        coordinatesFromParameter(viewportParameterPattern, decoded)?.let {
            return CoordinateCandidate(it, CoordinateSource.VIEWPORT)
        }
        viewportPattern.find(decoded)?.let { match ->
            coordinates(match.groupValues[1], match.groupValues[2])?.let {
                return CoordinateCandidate(it, CoordinateSource.VIEWPORT)
            }
        }

        return coordinatesFromPair(decoded)?.let {
            CoordinateCandidate(it, CoordinateSource.VIEWPORT)
        }
    }

    private fun coordinatesFromParameter(pattern: Regex, decodedUrl: String): Coordinates? {
        pattern.findAll(decodedUrl).forEach { match ->
            coordinatesFromPair(match.groupValues[1])?.let { return it }
        }
        return null
    }

    private fun lastCoordinates(pattern: Regex, value: String, swap: Boolean): Coordinates? {
        var result: Coordinates? = null
        pattern.findAll(value).forEach { match ->
            val latitude = if (swap) match.groupValues[2] else match.groupValues[1]
            val longitude = if (swap) match.groupValues[1] else match.groupValues[2]
            coordinates(latitude, longitude)?.let { result = it }
        }
        return result
    }

    private fun coordinatesFromPair(value: String): Coordinates? {
        val match = coordinatePairPattern.find(value) ?: return null
        return coordinates(match.groupValues[1], match.groupValues[2])
    }

    private fun coordinates(latitude: String, longitude: String): Coordinates? {
        val lat = latitude.toDoubleOrNull() ?: return null
        val lon = longitude.toDoubleOrNull() ?: return null
        return runCatching { Coordinates(lat, lon) }.getOrNull()
    }

    private fun decodeUrl(url: String): String {
        var result = url.replace("&amp;", "&")
        repeat(2) {
            val decoded = runCatching {
                URLDecoder.decode(result.replace("+", "%2B"), StandardCharsets.UTF_8.name())
            }.getOrElse { result }
            if (decoded == result) return result
            result = decoded
        }
        return result
    }

    private fun parseUri(url: String): URI? = runCatching { URI(url) }.getOrNull()

    private fun parameterPattern(names: String): Regex = Regex(
        "(?:[?&](?:$names)=)([^&#]+)",
        RegexOption.IGNORE_CASE,
    )

    private data class CoordinateCandidate(
        val coordinates: Coordinates,
        val source: CoordinateSource,
    )

    private data class RankedCandidate(
        val candidate: CoordinateCandidate,
        val index: Int,
    )

    private enum class CoordinateSource(val priority: Int) {
        VIEWPORT(100),
        EXPLICIT_QUERY(200),
        EXACT_DATA_MARKER(300),
        ROUTE_DESTINATION(400),
    }
}
