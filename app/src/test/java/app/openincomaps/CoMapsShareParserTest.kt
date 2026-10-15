// SPDX-License-Identifier: Apache-2.0
// Includes CoMaps upstream ge0 decoder test vectors; see THIRD_PARTY_NOTICES.md.
package app.openincomaps

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CoMapsShareParserTest {
    @Test
    fun `decodes official upstream vectors with cell quantization tolerance`() {
        val vectors = listOf(
            Triple("8wAAAAAAAA", 0.0, 0.0),
            Triple("Byqqqqqqqq", 45.0, 0.0),
            Triple("BaF6F6F6F6", -20.0, 20.0),
            Triple("BP________", -0.000001, -0.000001),
            Triple("BAAAAAAAAA", -90.0, -180.0),
            Triple("B4srhdHVVt", 64.5234, 12.1234),
        )
        vectors.forEach { (code, lat, lon) ->
            val point = requireNotNull(CoMapsShareParser.parse("comaps://$code/Name"))
            assertEquals(lat, point.latitude, 0.000002)
            assertEquals(lon, point.longitude, 0.000002)
        }
    }

    @Test
    fun `recognizes all official compressed prefixes without network`() {
        listOf(
            "https://comaps.at/", "http://comaps.at/", "comaps://", "cm://",
            "ge0://", "http://ge0.me/", "https://ge0.me/",
        ).forEach { prefix ->
            val point = requireNotNull(CoMapsShareParser.parse("${prefix}B4srhdHVVt/Name"))
            assertEquals(64.5234, point.latitude, 0.000002)
            assertEquals(12.1234, point.longitude, 0.000002)
        }
    }

    @Test
    fun `extracts link from CoMaps share text`() {
        val point = CoMapsShareParser.extract(listOf("Fyllingsdalen\nhttps://comaps.at/B4srhdHVVt/Name"))
        assertEquals(64.5234, requireNotNull(point).latitude, 0.000002)
    }

    @Test
    fun `accepts geo point and numeric query with label`() {
        assertEquals(Coordinates(48.2, 16.3), CoMapsShareParser.parse("geo:48.2,16.3?z=17"))
        assertEquals(
            Coordinates(-20.0, 20.0),
            CoMapsShareParser.parse("geo:0,0?q=-20,20(Test%20Place)"),
        )
        assertEquals(
            Coordinates(48.2, 16.3),
            CoMapsShareParser.parse("geo:48.2,16.3?z=17&q=48.2,16.3(Name)"),
        )
    }

    @Test
    fun `rejects nonnumeric geo search invalid coordinates and malformed compressed codes`() {
        listOf(
            "geo:0,0?q=Vienna", "geo:91,10", "geo:NaN,3", "ge0://Byqqqqqqq",
            "comaps://B4srhdHVV!", "comaps://B4srhdHVVtx", "https://example.org/B4srhdHVVt",
            "https://comaps.at.evil.test/B4srhdHVVt", "https://comaps.at/download",
        ).forEach { assertNull(it, CoMapsShareParser.parse(it)) }
    }

    @Test
    fun `does not capture Google Maps inputs from existing direction`() {
        assertNull(CoMapsShareParser.extract(listOf("https://www.google.com/maps?q=48.2,16.3")))
    }
}
