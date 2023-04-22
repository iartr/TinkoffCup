package com.artr.tinkoffcup.designsystem

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.withStyledAttributes
import androidx.core.view.isVisible
import androidx.core.view.marginTop

class TinkoffCard2 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {
    private val header: TextView
    private val subheader: TextView
    private val button: Button
    private val starIcon: ImageView

    init {
        LayoutInflater.from(context).inflate(R.layout.tinkoff_card_2, this, true)

        header = findViewById(R.id.tinkoff_card2_header)
        subheader = findViewById(R.id.tinkoff_card2_subheader)
        button = findViewById(R.id.tinkoff_card2_button)
        starIcon = findViewById(R.id.tinkoff_card2_end_image)

        context.withStyledAttributes(attrs, R.styleable.TinkoffCard2) {
            header.text = getString(R.styleable.TinkoffCard2_card_header)
            subheader.text = getString(R.styleable.TinkoffCard2_card_subheader)
            button.text = getString(R.styleable.TinkoffCard2_card_button)
            subheader.setVisible(getBoolean(R.styleable.TinkoffCard2_subheader_visible, false))
            button.setVisible(getBoolean(R.styleable.TinkoffCard2_button_visible, false))
        }

        this.useCompatPadding = true
        this.radius = resources.getDimensionPixelSize(R.dimen.tinkoff_corner_radius_24dp).toFloat()

        val layout = findViewById<ConstraintLayout>(R.id.tinkoff_card2_layout)
        val constraintSet = ConstraintSet()
        constraintSet.clone(layout)

        if (!subheader.isVisible && !button.isVisible) {
            constraintSet.connect(R.id.tinkoff_card2_header, ConstraintSet.BOTTOM, R.id.tinkoff_card2_layout, ConstraintSet.BOTTOM)
            constraintSet.connect(R.id.tinkoff_card2_end_image, ConstraintSet.BOTTOM, R.id.tinkoff_card2_layout, ConstraintSet.BOTTOM)
            constraintSet.applyTo(layout)
            header.setMarginBottom(header.marginTop)
            starIcon.setMarginBottom(starIcon.marginTop)
        }
    }

    fun setOnButtonClickListener(listener: View.OnClickListener?) {
        button.setOnClickListener(listener)
    }
}