package app.openincomaps

import java.math.BigDecimal

data class Coordinates(val latitude: Double, val longitude: Double) {
    init {
        require(latitude.isFinite() && latitude in -90.0..90.0) {
            "Latitude must be between -90 and 90"
        }
        require(longitude.isFinite() && longitude in -180.0..180.0) {
            "Longitude must be between -180 and 180"
        }
    }

    fun latitudeText(): String = latitude.toPlainCoordinate()

    fun longitudeText(): String = longitude.toPlainCoordinate()

    private fun Double.toPlainCoordinate(): String =
        if (this == 0.0) {
            "0"
        } else {
            BigDecimal.valueOf(this).stripTrailingZeros().toPlainString()
        }
}
