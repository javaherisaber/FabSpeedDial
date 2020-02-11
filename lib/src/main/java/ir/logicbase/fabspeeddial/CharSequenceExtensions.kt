package ir.logicbase.fabspeeddial

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.annotation.ColorInt

fun CharSequence.color(@ColorInt color: Int) = applyTags(arrayOf(this), ForegroundColorSpan(color))

/**
 * Returns a CharSequence that concatenates the specified array of CharSequence
 * objects and then applies a list of zero or more tags to the entire range.
 *
 * @param content an array of character sequences to apply a style to
 * @param tags    the styled span objects to apply to the content
 * such as android.text.style.StyleSpan
 */
private fun applyTags(content: Array<out CharSequence>, vararg tags: Any): CharSequence {
    val text = SpannableStringBuilder()
    openTags(text, tags)

    for (item in content) text.append(item)

    closeTags(text, tags)
    return text
}

/**
 * Iterates over an array of tags and applies them to the beginning of the specified
 * Spannable object so that future text appended to the text will have the styling
 * applied to it. Do not call this method directly.
 */
private fun openTags(text: Spannable, tags: Array<out Any>) {
    for (tag in tags) text.setSpan(tag, 0, 0, Spannable.SPAN_MARK_MARK)
}

/**
 * "Closes" the specified tags on a Spannable by updating the spans to be
 * endpoint-exclusive so that future text appended to the end will not take
 * on the same styling. Do not call this method directly.
 */
private fun closeTags(text: Spannable, tags: Array<out Any>) {
    val len = text.length
    for (tag in tags)
        if (len > 0) text.setSpan(tag, 0, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        else text.removeSpan(tag)
}