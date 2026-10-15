package app.openincomaps

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GoogleMapsUrlParserTest {
    @Test
    fun `extracts coordinates from Google Maps query parameter`() {
        assertCoordinates(
            48.208174,
            16.373819,
            GoogleMapsUrlParser.extractCoordinates(
                "https://www.google.com/maps?q=48.208174,16.373819",
            ),
        )
    }

    @Test
    fun `extracts percent encoded API query from surrounding share text`() {
        assertCoordinates(
            52.520008,
            13.404954,
            GoogleMapsUrlParser.extractCoordinates(
                "Schau mal: https://www.google.com/maps/search/?api=1&query=52.520008%2C13.404954 Viel Spaß!",
            ),
        )
    }

    @Test
    fun `prefers explicit query coordinates over viewport`() {
        assertCoordinates(
            48.2,
            16.3,
            GoogleMapsUrlParser.extractCoordinates(
                "https://www.google.com/maps/@47.0,15.0,8z?q=48.2,16.3",
            ),
        )
    }

    @Test
    fun `extracts exact place coordinates instead of viewport`() {
        assertCoordinates(
            48.20849,
            16.37208,
            GoogleMapsUrlParser.extractCoordinates(
                "https://www.google.com/maps/place/Wien/@48.21,16.37,14z/data=!4m6!3m5!1s0x0:0x0!8m2!3d48.20849!4d16.37208",
            ),
        )
    }

    @Test
    fun `exact place marker beats query center and viewport coordinates`() {
        assertCoordinates(
            48.20849,
            16.37208,
            GoogleMapsUrlParser.extractCoordinates(
                "https://www.google.com/maps/place/Wien/@47.0,15.0,8z/" +
                    "data=!8m2!3d48.20849!4d16.37208?q=46.0,14.0&center=45.0,13.0",
            ),
        )
    }

    @Test
    fun `place URL without an exact target does not return its viewport`() {
        assertNull(
            GoogleMapsUrlParser.extractCoordinates(
                "https://www.google.com/maps/place/Specific+POI/@47.5,9.7,16z?center=47.6,9.8",
            ),
        )
    }

    @Test
    fun `latest exact marker wins when Google data contains multiple markers`() {
        assertCoordinates(
            48.20849,
            16.37208,
            GoogleMapsUrlParser.extractCoordinates(
                "https://www.google.com/maps/place/Wien/data=" +
                    "!8m2!3d47.0!4d15.0!8m2!3d48.20849!4d16.37208",
            ),
        )
    }

    @Test
    fun `supports alternate longitude latitude data markers`() {
        assertCoordinates(
            -33.8568,
            151.2153,
            GoogleMapsUrlParser.extractCoordinates(
                "https://www.google.com/maps/place/Test/data=!4m2!3m1!2d151.2153!3d-33.8568",
            ),
        )
    }

    @Test
    fun `extracts viewport coordinates`() {
        assertCoordinates(
            -33.8568,
            151.2153,
            GoogleMapsUrlParser.extractCoordinates(
                "https://maps.google.com/maps/@-33.8568,151.2153,17z",
            ),
        )
    }

    @Test
    fun `extracts destination coordinates`() {
        assertCoordinates(
            40.6892,
            -74.0445,
            GoogleMapsUrlParser.extractCoordinates(
                "https://www.google.com/maps/dir/?api=1&origin=Times+Square&destination=40.6892%2C-74.0445",
            ),
        )
    }

    @Test
    fun `supports regional Google domains`() {
        assertCoordinates(
            51.5,
            -0.12,
            GoogleMapsUrlParser.extractCoordinates(
                "https://www.google.co.uk/maps?q=51.5,-0.12",
            ),
        )
    }

    @Test
    fun `best coordinate source wins across all links in share text`() {
        assertCoordinates(
            48.20849,
            16.37208,
            GoogleMapsUrlParser.extractCoordinates(
                "Viewport https://www.google.com/maps/@47.0,15.0,8z " +
                    "target https://www.google.com/maps/place/Wien/data=!8m2!3d48.20849!4d16.37208",
            ),
        )
    }

    @Test
    fun `rejects coordinates outside valid ranges`() {
        assertNull(
            GoogleMapsUrlParser.extractCoordinates(
                "https://www.google.com/maps?q=91.0,181.0",
            ),
        )
    }

    @Test
    fun `ignores coordinates from non Google URLs`() {
        assertNull(
            GoogleMapsUrlParser.extractCoordinates(
                "https://example.org/maps?q=48.2,16.3",
            ),
        )
    }

    @Test
    fun `recognizes supported short link hosts only`() {
        assertTrue(GoogleMapsUrlParser.isShortGoogleMapsUrl("https://maps.app.goo.gl/AbCdEf"))
        assertTrue(GoogleMapsUrlParser.isShortGoogleMapsUrl("https://goo.gl/maps/AbCdEf"))
        assertFalse(GoogleMapsUrlParser.isShortGoogleMapsUrl("https://goo.gl/forms/AbCdEf"))
        assertFalse(GoogleMapsUrlParser.isShortGoogleMapsUrl("https://maps.app.goo.gl.evil.example/AbCdEf"))
    }

    @Test
    fun `removes share sentence punctuation after URL`() {
        val urls = GoogleMapsUrlParser.extractGoogleMapsUrls(
            "Ort (https://maps.google.de/maps?ll=53.55,9.99).",
        )

        assertEquals(listOf("https://maps.google.de/maps?ll=53.55,9.99"), urls)
    }

    @Test
    fun `formats coordinates without scientific notation or negative zero`() {
        assertEquals("0", Coordinates(-0.0, 0.000001).latitudeText())
        assertEquals("0.000001", Coordinates(-0.0, 0.000001).longitudeText())
    }

    private fun assertCoordinates(
        latitude: Double,
        longitude: Double,
        actual: Coordinates?,
    ) {
        requireNotNull(actual)
        assertEquals(latitude, actual.latitude, 0.0000001)
        assertEquals(longitude, actual.longitude, 0.0000001)
    }
}
