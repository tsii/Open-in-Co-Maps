package app.openincomaps

import java.net.HttpURLConnection
import java.net.URI
import java.net.URL

fun interface RedirectFollower {
    fun follow(url: String): List<String>
}

class GoogleMapsLinkResolver(
    private val redirectFollower: RedirectFollower = HttpRedirectFollower(),
) {
    fun resolveFirst(sharedTexts: Iterable<String>): Coordinates? =
        resolve(sharedTexts.joinToString(separator = "\n"))

    fun resolve(sharedText: String): Coordinates? {
        val resolvedUrls = GoogleMapsUrlParser.extractGoogleMapsUrls(sharedText)
            .flatMap { url ->
                if (GoogleMapsUrlParser.isShortGoogleMapsUrl(url)) {
                    runCatching { redirectFollower.follow(url) }
                        .getOrDefault(emptyList())
                } else {
                    listOf(url)
                }
            }

        return GoogleMapsUrlParser.extractBestFromUrls(resolvedUrls)
    }
}

class HttpRedirectFollower(
    private val maxRedirects: Int = 5,
    private val connectTimeoutMillis: Int = 4_000,
    private val readTimeoutMillis: Int = 4_000,
) : RedirectFollower {
    override fun follow(url: String): List<String> {
        if (!isSafeGoogleMapsHttpsUrl(url)) return emptyList()

        val visited = linkedSetOf<String>()
        var current = url

        repeat(maxRedirects + 1) { redirectCount ->
            if (!visited.add(current)) return visited.toList()
            if (redirectCount == maxRedirects) return visited.toList()

            val connection = openConnection(current)
            try {
                val status = connection.responseCode
                if (status !in REDIRECT_STATUS_CODES) return visited.toList()

                val location = connection.getHeaderField("Location") ?: return visited.toList()
                val next = runCatching { URI(current).resolve(location).toString() }.getOrNull()
                    ?: return visited.toList()
                if (!isSafeGoogleMapsHttpsUrl(next)) return visited.toList()
                current = next
            } finally {
                connection.disconnect()
            }
        }

        return visited.toList()
    }

    private fun openConnection(url: String): HttpURLConnection =
        (URL(url).openConnection() as HttpURLConnection).apply {
            instanceFollowRedirects = false
            requestMethod = "GET"
            connectTimeout = connectTimeoutMillis
            readTimeout = readTimeoutMillis
            useCaches = false
            setRequestProperty("User-Agent", "OpenInCoMaps/1.0.1 (Android)")
            setRequestProperty("Accept", "text/html,application/xhtml+xml")
        }

    internal fun isSafeGoogleMapsHttpsUrl(url: String): Boolean {
        val uri = runCatching { URI(url) }.getOrNull() ?: return false
        return uri.scheme.equals("https", ignoreCase = true) &&
            uri.userInfo == null &&
            GoogleMapsUrlParser.isGoogleMapsUrl(url)
    }

    private companion object {
        val REDIRECT_STATUS_CODES = setOf(301, 302, 303, 307, 308)
    }
}
