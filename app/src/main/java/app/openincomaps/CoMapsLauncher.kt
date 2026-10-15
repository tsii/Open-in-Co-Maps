package app.openincomaps

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri

object CoMapsLauncher {
    private val packageNames = listOf(
        "app.comaps.google",
        "app.comaps.fdroid",
        "app.comaps",
        "app.comaps.huawei",
    )

    fun open(activity: Activity, coordinates: Coordinates): Boolean {
        val uri = Uri.parse(CoMapsGeoUri.create(coordinates))

        packageNames.forEach { packageName ->
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage(packageName)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            try {
                activity.startActivity(intent)
                return true
            } catch (_: ActivityNotFoundException) {
                // Try the next official CoMaps distribution package.
            } catch (_: SecurityException) {
                // A broken package must not prevent trying another distribution.
            }
        }

        return false
    }
}
