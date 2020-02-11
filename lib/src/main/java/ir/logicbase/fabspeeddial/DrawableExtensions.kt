package ir.logicbase.fabspeeddial

import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat

/**
 * Convenience extension for [DrawableCompat.wrap]  and [DrawableCompat.setTint]
 */
fun Drawable.withTint(@ColorInt tint: Int): Drawable = this.run {
    wrapped.apply { DrawableCompat.setTint(this, tint) }
}

val Drawable.wrapped: Drawable get() = DrawableCompat.wrap(this)