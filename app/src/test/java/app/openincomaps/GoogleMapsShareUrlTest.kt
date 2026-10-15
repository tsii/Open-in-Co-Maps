package app.openincomaps

import java.net.URI
import java.net.URLDecoder
import org.junit.Assert.assertEquals
import org.junit.Test

class GoogleMapsShareUrlTest {
    @Test
    fun `builds official Maps app compatible HTTPS URL`() {
        val result = GoogleMapsShareUrl.create(Coordinates(48.20849, 16.37208))
        assertEquals("https://www.google.com/maps/search/?api=1&query=48.20849%2C16.37208", result)
        val uri = URI(result)
        assertEquals("https", uri.scheme)
        assertEquals("www.google.com", uri.host)
        assertEquals("api=1&query=48.20849,16.37208", URLDecoder.decode(uri.rawQuery, "UTF-8"))
    }

    @Test
    fun `preserves negatives and small coordinates without locale dependence`() {
        assertEquals(
            "https://www.google.com/maps/search/?api=1&query=-20%2C0.000001",
            GoogleMapsShareUrl.create(Coordinates(-20.0, 0.000001)),
        )
    }
}
