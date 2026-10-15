package app.openincomaps

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GoogleMapsLinkResolverTest {
    @Test
    fun `returns direct coordinates without making a network request`() {
        var calls = 0
        val resolver = GoogleMapsLinkResolver(
            RedirectFollower {
                calls += 1
                emptyList()
            },
        )

        val result = resolver.resolve("https://www.google.com/maps?q=48.2,16.3")

        assertEquals(Coordinates(48.2, 16.3), result)
        assertEquals(0, calls)
    }

    @Test
    fun `extracts exact coordinates from a short link redirect chain`() {
        val resolver = GoogleMapsLinkResolver(
            RedirectFollower { shortUrl ->
                listOf(
                    shortUrl,
                    "https://www.google.com/maps/@47.5,9.7,16z",
                    "https://www.google.com/maps/place/Test/@47.5,9.7,16z/" +
                        "data=!8m2!3d48.20849!4d16.37208",
                )
            },
        )

        val result = resolver.resolve("Shared place: https://maps.app.goo.gl/AbCdEf")

        assertEquals(Coordinates(48.20849, 16.37208), result)
    }

    @Test
    fun `does not mistake a place viewport for the shared location`() {
        val resolver = GoogleMapsLinkResolver(
            RedirectFollower { shortUrl ->
                listOf(
                    shortUrl,
                    "https://www.google.com/maps/place/Test/@47.5,9.7,16z",
                )
            },
        )

        assertNull(resolver.resolve("https://maps.app.goo.gl/AbCdEf"))
    }

    @Test
    fun `does not follow a non short Google URL without coordinates`() {
        var calls = 0
        val resolver = GoogleMapsLinkResolver(
            RedirectFollower {
                calls += 1
                listOf("https://www.google.com/maps?q=1,2")
            },
        )

        val result = resolver.resolve("https://www.google.com/maps/place/NoCoordinates")

        assertNull(result)
        assertEquals(0, calls)
    }

    @Test
    fun `checks every shared text candidate until a link is found`() {
        val resolver = GoogleMapsLinkResolver()

        val result = resolver.resolveFirst(
            listOf(
                "Besuche diesen schönen Ort",
                "Kein Link in diesem Eintrag",
                "https://www.google.com/maps?q=48.208174,16.373819",
            ),
        )

        assertEquals(Coordinates(48.208174, 16.373819), result)
    }

    @Test
    fun `chooses an exact target from later shared candidates over an earlier viewport`() {
        val resolver = GoogleMapsLinkResolver()

        val result = resolver.resolveFirst(
            listOf(
                "https://www.google.com/maps/@47.0,15.0,8z",
                "https://www.google.com/maps/place/Wien/data=!8m2!3d48.20849!4d16.37208",
            ),
        )

        assertEquals(Coordinates(48.20849, 16.37208), result)
    }

    @Test
    fun `handles redirect resolver failures`() {
        val resolver = GoogleMapsLinkResolver(
            RedirectFollower { throw java.io.IOException("offline") },
        )

        assertNull(resolver.resolve("https://maps.app.goo.gl/AbCdEf"))
    }

    @Test
    fun `redirect follower only accepts secure Google Maps URLs without user info`() {
        val follower = HttpRedirectFollower()

        assertTrue(follower.isSafeGoogleMapsHttpsUrl("https://maps.app.goo.gl/AbCdEf"))
        assertTrue(follower.isSafeGoogleMapsHttpsUrl("https://www.google.com/maps?q=1,2"))
        assertFalse(follower.isSafeGoogleMapsHttpsUrl("http://maps.app.goo.gl/AbCdEf"))
        assertFalse(follower.isSafeGoogleMapsHttpsUrl("https://user@www.google.com/maps?q=1,2"))
        assertFalse(follower.isSafeGoogleMapsHttpsUrl("https://maps.app.goo.gl.evil.example/AbCdEf"))
        assertFalse(follower.isSafeGoogleMapsHttpsUrl("https://example.org/maps?q=1,2"))
    }
}
