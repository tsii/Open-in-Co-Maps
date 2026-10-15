package app.openincomaps

object CoMapsGeoUri {
    private const val DEFAULT_ZOOM = 17

    fun create(coordinates: Coordinates): String =
        "geo:${coordinates.latitudeText()},${coordinates.longitudeText()}?z=$DEFAULT_ZOOM"
}
