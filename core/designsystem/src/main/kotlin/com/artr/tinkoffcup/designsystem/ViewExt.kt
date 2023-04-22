package com.artr.tinkoffcup.designsystem

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.Px
import androidx.constraintlayout.widget.ConstraintLayout

internal fun View.setVisible(visible: Boolean) {
    if (visible) {
        visibility = View.VISIBLE
    } else {
        visibility = View.GONE
    }
}

internal fun View.setMarginStart(start: Int) {
    val lp = this.layoutParams
    if (lp is ViewGroup.MarginLayoutParams && lp.marginStart != start) {
        lp.marginStart = start
        this.layoutParams = lp
    }
}

internal fun View.setMarginEnd(end: Int) {
    val lp = this.layoutParams
    if (lp is ViewGroup.MarginLayoutParams && lp.marginEnd != end) {
        lp.marginEnd = end
        this.layoutParams = lp
    }
}

internal fun View.setMarginBottom(margin: Int) {
    val lp = this.layoutParams
    val bottomMargin = when (lp) {
        is ViewGroup.MarginLayoutParams -> lp.bottomMargin
        is FrameLayout.LayoutParams -> lp.bottomMargin
        else -> return
    }

    if (bottomMargin != margin) {
        when (lp) {
            is ViewGroup.MarginLayoutParams -> { lp.bottomMargin = margin }
            is FrameLayout.LayoutParams -> { lp.bottomMargin = margin }
            else -> return
        }
        this.layoutParams = lp
    }
}

internal fun View.setMarginTop(@Px top: Int) {
    val lp = layoutParams
    if (lp is ViewGroup.MarginLayoutParams) {
        if (lp.topMargin != top) {
            lp.topMargin = top
            layoutParams = lp
        }
    }
}

internal fun View.setMargins(@Px start: Int, @Px top: Int, @Px end: Int, @Px bottom: Int) {
    val lp = layoutParams
    if (lp is ViewGroup.MarginLayoutParams &&
        (lp.marginStart != start || lp.topMargin != top || lp.marginEnd != end || lp.bottomMargin != bottom)) {
        lp.marginStart = start
        lp.topMargin = top
        lp.marginEnd = end
        lp.bottomMargin = bottom
        layoutParams = lp
    }
}