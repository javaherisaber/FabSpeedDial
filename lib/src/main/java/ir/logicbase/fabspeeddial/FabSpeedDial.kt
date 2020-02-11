package ir.logicbase.fabspeeddial

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.WindowManager
import android.view.animation.*
import android.view.animation.Animation.RELATIVE_TO_PARENT
import android.view.animation.Animation.RELATIVE_TO_SELF
import android.view.animation.LayoutAnimationController.ORDER_NORMAL
import android.widget.LinearLayout
import android.widget.LinearLayout.VERTICAL
import android.widget.PopupWindow
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel

fun speedDial(
    anchor: View,
    items: List<Pair<CharSequence?, Drawable>>,
    dismissListener: (Int?) -> Unit,
    @StyleRes textAppearance: Int,
    @ColorInt buttonTint: Int = anchor.context.themeColorAt(R.attr.colorPrimary),
    @ColorInt backgroundColor: Int = Color.argb(60, 0, 0, 0),
    @StyleRes animationStyle: Int = R.style.PopUpFade,
    layoutAnimationController: LayoutAnimationController = LayoutAnimationController(
        speedDialAnimation,
        INITIAL_DELAY
    ).apply { order = ORDER_NORMAL }
) = LinearLayout(anchor.context).run root@{
    val paddingSide = anchor.paddingStart + (anchor.height / 6)
    val paddingBottom = anchor.paddingBottom + anchor.height + (anchor.height / 4) + context.resources.getDimensionPixelSize(
        R.dimen.single_margin
    )
    val isRTL = ViewCompat.getLayoutDirection(anchor) == ViewCompat.LAYOUT_DIRECTION_RTL
    if (isRTL) {
        updatePadding(top = paddingBottom, right = paddingSide) // top is mirror of bottom and right is mirror of left
    } else {
        updatePadding(top = paddingBottom, left = paddingSide) // top is mirror of bottom and left is mirror of right
    }
    // layout is mirrored because we want to add view to bottom right of LinearLayout instead of top left
    rotationY = MIRROR
    rotationX = MIRROR
    clipChildren = false
    clipToPadding = false
    orientation = VERTICAL
    layoutAnimation = layoutAnimationController

    popOver(anchor = anchor, backgroundColor = backgroundColor) {
        this.isClippingEnabled = false
        this.animationStyle = animationStyle

        var dismissReason: Int? = null
        setOnDismissListener { dismissListener(dismissReason) }

        items.mapIndexed { index, pair ->
            speedDialLayout(
                pair,
                buttonTint,
                View.OnClickListener { dismissReason = index; dismiss() },
                textAppearance
            )
        }.forEach(this@root::addView)
    }
}

/**
 * Pops an orphaned [View] over the specified [anchor] using a [PopupWindow]
 */
fun View.popOver(
    @ColorInt backgroundColor: Int = Color.argb(60, 0, 0, 0),
    anchor: View,
    options: PopupWindow.() -> Unit = {}
) {
    require(!ViewCompat.isAttachedToWindow(this)) { "The View being attached must be an orphan" }
    PopupWindow(
        this,
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT,
        true
    ).run {
        isOutsideTouchable = true
        contentView.setBackgroundColor(backgroundColor)
        contentView.setOnTouchListener { _, _ -> dismiss(); true }
        options(this)
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        val navigationBarHeight = if (resourceId > 0 && !ViewConfiguration.get(context).hasPermanentMenuKey()) { resources.getDimensionPixelSize(resourceId) } else 0
        showAtLocation(anchor, Gravity.START, 0, -navigationBarHeight)
    }
}


fun LinearLayout.speedDialLayout(
    pair: Pair<CharSequence?, Drawable>, tint: Int,
    clickListener: View.OnClickListener,
    textAppearance: Int
) = LinearLayout(context).apply {
    rotationY = MIRROR
    rotationX = MIRROR
    clipChildren = false
    clipToPadding = false
    layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)

    updatePadding(top = context.resources.getDimensionPixelSize(R.dimen.single_margin))
    setOnClickListener(clickListener)

    addView(speedDialLabel(tint, pair.first, clickListener, textAppearance))
    addView(speedDialFab(tint, pair, clickListener))
}

private fun LinearLayout.speedDialLabel(
    tint: Int,
    label: CharSequence?,
    clicker: View.OnClickListener,
    textAppearance: Int
) = AppCompatTextView(context).apply {
    val dp4 = context.resources.getDimensionPixelSize(R.dimen.quarter_margin)
    val dp8 = context.resources.getDimensionPixelSize(R.dimen.half_margin)

    isClickable = true
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
        elevation = dp8.toFloat()
        background = context.ripple(tint) { setAllCornerSizes(dp8.toFloat()) }
    }

    isVisible = label != null
    text = label
    setTextAppearance(context, textAppearance)

    layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
        marginEnd = context.resources.getDimensionPixelSize(R.dimen.single_margin)
        gravity = Gravity.CENTER_VERTICAL
    }

    updatePadding(left = dp8, top = dp4, right = dp8, bottom = dp4)
    setOnClickListener(clicker)
}

private fun LinearLayout.speedDialFab(
    @ColorInt tint: Int,
    pair: Pair<CharSequence?, Drawable>,
    clicker: View.OnClickListener
) = AppCompatImageButton(context).apply {
    val dp40 = context.resources.getDimensionPixelSize(R.dimen.double_and_half_margin)
    val dp8 = context.resources.getDimensionPixelSize(R.dimen.half_margin)

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
        elevation = dp8.toFloat()
        imageTintList = null
        background = context.ripple(tint) { setAllCornerSizes(dp40.toFloat()) }
    }

    layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
        gravity = Gravity.CENTER_VERTICAL
        height = dp40
        width = dp40
    }

    setOnClickListener(clicker)
    setImageDrawable(
        if (pair.second !is BitmapDrawable) BitmapDrawable(
            context.resources,
            pair.second.toBitmap()
        ) else pair.second
    )
}

@SuppressLint("NewApi")
private fun Context.ripple(
    @ColorInt tint: Int,
    shapeModifier: ShapeAppearanceModel.Builder.() -> Unit
): RippleDrawable = RippleDrawable(
    ColorStateList.valueOf(translucentBlack),
    MaterialShapeDrawable(ShapeAppearanceModel.builder().run {
        shapeModifier(this)
        build()
    }).apply {
        tintList = ColorStateList.valueOf(tint)
        setShadowColor(Color.DKGRAY)
        initializeElevationOverlay(this@ripple)
    },
    null
)

private val speedDialAnimation: Animation
    get() = AnimationSet(false).apply {
        duration = 100L
        addAnimation(alpha())
        addAnimation(scale())
        addAnimation(translate())
    }

private fun alpha() = AlphaAnimation(0F, 1F).accelerateDecelerate()

private fun translate(): Animation = TranslateAnimation(
    RELATIVE_TO_PARENT,
    0F,
    RELATIVE_TO_PARENT,
    0F,
    RELATIVE_TO_PARENT,
    SPEED_DIAL_TRANSLATION_Y,
    RELATIVE_TO_PARENT,
    0F
).accelerateDecelerate()

private fun scale(): Animation = ScaleAnimation(
    SPEED_DIAL_SCALE,
    1F,
    SPEED_DIAL_SCALE,
    1F,
    RELATIVE_TO_SELF,
    SPEED_DIAL_SCALE_PIVOT,
    RELATIVE_TO_SELF,
    SPEED_DIAL_SCALE_PIVOT
).accelerateDecelerate()

private const val SPEED_DIAL_TRANSLATION_Y = -0.2F
private const val SPEED_DIAL_SCALE_PIVOT = 0.5F
private const val SPEED_DIAL_SCALE = 0.5F
private const val INITIAL_DELAY = 0.1F
private const val MIRROR = 180F

private val translucentBlack = Color.argb(50, 0, 0, 0)

private fun Animation.accelerateDecelerate() =
    apply { interpolator = AccelerateDecelerateInterpolator() }
