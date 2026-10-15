package app.openincomaps

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ShareReceiverActivity : Activity() {
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private val resolver = GoogleMapsLinkResolver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(createProgressView())
        processIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        processIntent(intent)
    }

    override fun onDestroy() {
        executor.shutdownNow()
        super.onDestroy()
    }

    private fun processIntent(intent: Intent) {
        if (intent.action != Intent.ACTION_SEND) {
            showMessageAndFinish(R.string.error_no_link)
            return
        }

        val candidates = intent.shareCandidates()
        if (candidates.isEmpty()) {
            showMessageAndFinish(R.string.error_no_link)
            return
        }

        executor.execute {
            val coordinates = resolver.resolveFirst(candidates)
            runOnUiThread {
                when {
                    isFinishing || isDestroyed -> Unit
                    coordinates == null -> showMessageAndFinish(R.string.error_no_coordinates)
                    CoMapsLauncher.open(this, coordinates) -> finish()
                    else -> showMessageAndFinish(R.string.error_comaps_not_installed)
                }
            }
        }
    }

    private fun Intent.shareCandidates(): List<String> = buildList {
        getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()?.let(::add)

        val sharedClipData = clipData ?: return@buildList
        for (index in 0 until sharedClipData.itemCount) {
            val item = sharedClipData.getItemAt(index)
            item.text?.toString()?.let(::add)
            item.htmlText?.let(::add)
            item.uri
                ?.takeIf { it.scheme.equals("http", true) || it.scheme.equals("https", true) }
                ?.toString()
                ?.let(::add)
        }
    }.filter(String::isNotBlank).distinct()

    private fun createProgressView(): LinearLayout {
        val padding = (24 * resources.displayMetrics.density).toInt()
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(padding, padding, padding, padding)
            addView(ProgressBar(this@ShareReceiverActivity))
            addView(
                TextView(this@ShareReceiverActivity).apply {
                    setText(R.string.processing_link)
                    gravity = Gravity.CENTER
                    setPadding(0, padding / 2, 0, 0)
                },
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                ),
            )
        }
    }

    private fun showMessageAndFinish(message: Int) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        finish()
    }
}
