package ir.logicbase.fabspeeddial

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

fun ExtendedFloatingActionButton.setOnSpeedDialClickListener(
    items: List<Pair<CharSequence?, Drawable>>,
    onOptionClickListener: (View, Int) -> Unit,
    textAppearance: Int,
    runGuard: () -> Boolean = { false }
) = this.setOnClickListener(SpeedDialClickListener(items, onOptionClickListener, textAppearance, runGuard))

fun FloatingActionButton.setOnSpeedDialClickListener(
    items: List<Pair<CharSequence?, Drawable>>,
    onOptionClickListener: (View, Int) -> Unit,
    textAppearance: Int,
    runGuard: () -> Boolean = { false }
) = this.setOnClickListener(SpeedDialClickListener(items, onOptionClickListener, textAppearance, runGuard))

class SpeedDialClickListener(
    private val items: List<Pair<CharSequence?, Drawable>>,
    private val onOptionClickListener: (View, Int) -> Unit,
    @StyleRes private val textAppearance: Int,
    private val runGuard: () -> Boolean = { false },
    private val dismissListener: ((View) -> Unit)? = null,
    @ColorInt private val tint: Int = Color.WHITE,
    private val shrinkable: Boolean = true
) : View.OnClickListener {

    override fun onClick(button: View) {
        if (button !is ExtendedFloatingActionButton && button !is FloatingActionButton
            && button !is MaterialButton && !runGuard()
        ) {
            return
        }

        if (shrinkable && button is ExtendedFloatingActionButton) {
            button.shrink()
        }

        speedDial(
            anchor = button, buttonTint = tint, items = items, dismissListener = dismiss@{ index ->
                if (shrinkable && button is ExtendedFloatingActionButton) {
                    button.extend()
                }
                if (index == null) {
                    dismissListener?.invoke(button)
                } else {
                    onOptionClickListener(button, index)
                }
            },
            textAppearance = textAppearance
        )
    }
}