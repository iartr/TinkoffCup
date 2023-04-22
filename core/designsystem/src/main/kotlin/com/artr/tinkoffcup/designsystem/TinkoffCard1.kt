package com.artr.tinkoffcup.designsystem

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.withStyledAttributes

/**
 * Tinkoff card with title, description, start image and end close icon
 * Customization:
 * 1. app:card_title - title text
 * 2. app:card_description - description text
 * 3. app:end_icon_visible - set close icon visibility
 */
class TinkoffCard1 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {
    private val startImage: ImageView
    private val title: TextView
    private val description: TextView
    private val endIcon: ImageView

    init {
        LayoutInflater.from(context).inflate(R.layout.tinkoff_card_1, this, true)

        startImage = findViewById(R.id.tinkoff_card1_image)
        title = findViewById(R.id.tinkoff_card1_title)
        description = findViewById(R.id.tinkoff_card1_description)
        endIcon = findViewById(R.id.tinkoff_card2_end_image)

        context.withStyledAttributes(attrs, R.styleable.TinkoffCard1) {
            title.text = getString(R.styleable.TinkoffCard1_card_title)
            description.text = getString(R.styleable.TinkoffCard1_card_description)
            endIcon.setVisible(getBoolean(R.styleable.TinkoffCard1_end_icon_visible, false))
        }

        this.useCompatPadding = true
        this.radius = resources.getDimensionPixelSize(R.dimen.tinkoff_corner_radius_24dp).toFloat()
    }

}