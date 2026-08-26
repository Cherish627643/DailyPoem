package com.dailypoem.app.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import com.dailypoem.app.R
import com.dailypoem.app.data.Poem
import java.io.File

/** 把诗词渲染成一张海报图片，并保存到缓存目录供分享。 */
object PoemShareHelper {
    fun prepareUri(context: Context, poem: Poem): Uri {
        val bitmap = render(context, poem)
        val dir = File(context.cacheDir, "shared").apply { mkdirs() }
        val file = File(dir, "poem_${poem.id}.png")
        file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    private fun render(context: Context, poem: Poem): Bitmap {
        val density = context.resources.displayMetrics.density
        fun dp(value: Float): Int = (value * density).toInt()

        val serif = ResourcesCompat.getFont(context, R.font.noto_serif_sc)

        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(48f), dp(56f), dp(48f), dp(56f))
            background = GradientDrawable().apply {
                setColor(0xFFF7F1E5.toInt())
                setStroke(dp(1f), 0xFFD8CFBA.toInt())
                cornerRadius = dp(18f).toFloat()
            }
        }

        val title = TextView(context).apply {
            text = poem.title
            setTextColor(0xFF2E2B28.toInt())
            textSize = 40f
            typeface = serif
            gravity = Gravity.CENTER
        }
        root.addView(title)

        val subtitle = TextView(context).apply {
            text = "${poem.dynasty} · ${poem.author}"
            setTextColor(0xFF6F6A5E.toInt())
            textSize = 18f
            gravity = Gravity.CENTER
        }
        root.addView(
            subtitle,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(12f) }
        )

        val divider = View(context).apply { setBackgroundColor(0xFF6E9B96.toInt()) }
        root.addView(
            divider,
            LinearLayout.LayoutParams(dp(64f), dp(2f)).apply {
                topMargin = dp(24f)
                bottomMargin = dp(24f)
            }
        )

        val content = TextView(context).apply {
            text = poem.content
            setTextColor(0xFF2E2B28.toInt())
            textSize = 30f
            typeface = serif
            gravity = Gravity.CENTER
            setLineSpacing(dp(12f).toFloat(), 1f)
        }
        root.addView(content)

        val footer = TextView(context).apply {
            text = "每日诗词 · 分享"
            setTextColor(0xFF9A9488.toInt())
            textSize = 14f
            gravity = Gravity.CENTER
        }
        root.addView(
            footer,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(36f) }
        )

        val width = 1080
        val widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        root.measure(widthSpec, heightSpec)
        val height = root.measuredHeight
        root.layout(0, 0, width, height)

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        root.draw(Canvas(bitmap))
        return bitmap
    }
}
