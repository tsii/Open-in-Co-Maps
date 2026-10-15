package app.openincomaps

/** Official Maps URLs format: launches Google Maps when installed, otherwise its website. */
object GoogleMapsShareUrl {
    fun create(coordinates: Coordinates): String =
        "https://www.google.com/maps/search/?api=1&query=" +
            "${coordinates.latitudeText()}%2C${coordinates.longitudeText()}"
}
