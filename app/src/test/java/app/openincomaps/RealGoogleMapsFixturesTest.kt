package app.openincomaps

import org.junit.Assert.assertEquals
import org.junit.Test

class RealGoogleMapsFixturesTest {
    private val fixtures = listOf(
        RealFixture(
            shortUrl = "https://maps.app.goo.gl/4WecM5WuQN7e5CC67",
            redirectUrl = "https://www.google.com/maps/place/19690+Little+Pine+Ln,+Katy,+TX+77449,+USA/" +
                "@29.8614552,-95.7174803,17z/data=!3m1!4b1!4m6!3m5!1s0x8640d7f1eff7c30b:" +
                "0x88eed24436232a5a!8m2!3d29.8614552!4d-95.7149054!16s%2Fg%2F11c882sdhs" +
                "?entry=tts",
            expected = Coordinates(29.8614552, -95.7149054),
        ),
        RealFixture(
            shortUrl = "https://maps.app.goo.gl/Crj1o13NznuE5fZn8",
            redirectUrl = "https://www.google.com/maps/place/Fyllingsdalen+Tunnel/" +
                "@60.3476523,5.2912732,17z/data=!3m1!4b1!4m6!3m5!1s0x463cfbe3e77c69f9:" +
                "0xf3ca928ce204d439!8m2!3d60.3476523!4d5.2938481!16s%2Fg%2F11ty26f4jg" +
                "?entry=tts",
            expected = Coordinates(60.3476523, 5.2938481),
        ),
    )

    @Test
    fun `extracts exact targets from captured real Google Maps redirect URLs`() {
        fixtures.forEach { fixture ->
            assertEquals(fixture.expected, GoogleMapsUrlParser.extractFromUrl(fixture.redirectUrl))
        }
    }

    @Test
    fun `resolves captured real short links to exact targets`() {
        val redirects = fixtures.associate { it.shortUrl to it.redirectUrl }
        val resolver = GoogleMapsLinkResolver(
            RedirectFollower { shortUrl ->
                listOf(shortUrl, requireNotNull(redirects[shortUrl]))
            },
        )

        fixtures.forEach { fixture ->
            assertEquals(fixture.expected, resolver.resolve(fixture.shortUrl))
        }
    }

    @Test
    fun `builds direct CoMaps URI from each real target`() {
        val expectedUris = listOf(
            "geo:29.8614552,-95.7149054?z=17",
            "geo:60.3476523,5.2938481?z=17",
        )

        assertEquals(expectedUris, fixtures.map { CoMapsGeoUri.create(it.expected) })
    }

    private data class RealFixture(
        val shortUrl: String,
        val redirectUrl: String,
        val expected: Coordinates,
    )
}
