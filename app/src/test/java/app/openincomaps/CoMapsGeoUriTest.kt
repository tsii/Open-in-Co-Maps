package app.openincomaps

import org.junit.Assert.assertEquals
import org.junit.Test

class CoMapsGeoUriTest {
    @Test
    fun `puts target coordinates directly into geo URI`() {
        assertEquals(
            "geo:48.20849,16.37208?z=17",
            CoMapsGeoUri.create(Coordinates(48.20849, 16.37208)),
        )
    }

    @Test
    fun `preserves negative coordinates without scientific notation`() {
        assertEquals(
            "geo:-54.683486138,0.000001?z=17",
            CoMapsGeoUri.create(Coordinates(-54.683486138, 0.000001)),
        )
    }

    @Test
    fun `does not use dummy origin or search query`() {
        val uri = CoMapsGeoUri.create(Coordinates(29.8614552, -95.7149054))

        assertEquals("geo:29.8614552,-95.7149054?z=17", uri)
    }
}
