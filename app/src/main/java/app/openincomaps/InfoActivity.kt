package app.openincomaps

import android.app.Activity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

class InfoActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(createContentView())
    }

    private fun createContentView(): LinearLayout {
        val density = resources.displayMetrics.density
        val padding = (32 * density).toInt()

        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(padding, padding, padding, padding)

            addView(
                TextView(this@InfoActivity).apply {
                    setText(R.string.app_name)
                    textSize = 26f
                    gravity = Gravity.CENTER
                    setTextColor(getColor(R.color.primary))
                },
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                ),
            )

            addView(
                TextView(this@InfoActivity).apply {
                    setText(R.string.instructions)
                    textSize = 17f
                    gravity = Gravity.CENTER
                    setPadding(0, (24 * density).toInt(), 0, 0)
                    movementMethod = LinkMovementMethod.getInstance()
                },
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                ),
            )
        }
    }
}
