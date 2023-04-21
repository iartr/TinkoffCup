package com.artr.tinkoffcup.ext

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Rect
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.util.Size
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
import android.view.ViewGroup
import android.view.ViewParent
import android.view.ViewStub
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import androidx.core.widget.ImageViewCompat
import androidx.core.widget.TextViewCompat
import java.lang.ref.WeakReference

const val HIDE_STATUS_BAR_FLAG: Int = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
        View.SYSTEM_UI_FLAG_FULLSCREEN or
        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View =
    LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)

fun View.getResourceName(): String {
    val id = this.id
    return if (id > 0) context.getResourceName(id)
    else "NO_ID"
}

fun View.getResourcesPath(): String {
    var view = this
    val path = mutableListOf(view.getResourceName())

    while (view.parent is View) {
        view = view.parent as View
        path.add(view.getResourceName())
    }

    val sb = StringBuilder()
    path.reversed().forEachIndexed { i, resource ->
        for (k in 0..i) sb.append("\t")
        sb.append("-->").append(resource).append("\n")
    }
    return sb.toString()
}
fun View.setBackgroundVectorDrawable(@DrawableRes resId: Int) {
    this.background = AppCompatResources.getDrawable(context, resId)
}

var View.isInvisible: Boolean
    get() = this.visibility == View.INVISIBLE
    set(value) {
        this.visibility = if (value) View.INVISIBLE else View.VISIBLE
    }


var View.isVisible: Boolean
    get() = this.visibility == View.VISIBLE
    set(value) {
        if (value != isVisible) {
            this.visibility = if (value) View.VISIBLE else View.GONE
        }
    }

/**
 * Cancels scheduled on predraw run previously requested with [runOnPreDraw]
 * @param token that was previously received from [runOnPreDraw]
 */
fun View.removeOnPreDrawRun(token: Any?) {
    (token as? ViewTreeObserver.OnPreDrawListener)?.let {
        viewTreeObserver.removeOnPreDrawListener(it)
    }
}

/**
 * Runs [callback] ONCE when layout happens.
 */
fun View.runOnLayout(callback: () -> Unit) {
    runOnLayout(callback, 0L)
}

fun View.runOnLayout(callback: () -> Unit, delay: Long = 0) {
    addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
        override fun onLayoutChange(v: View?, left: Int, top: Int, right: Int, bottom: Int,
                                    oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
            removeOnLayoutChangeListener(this)
            postDelayed(callback, delay)
        }
    })
}

/**
 * Runs [callback] when layout happens.
 */
fun View.doOnEachLayout(delay: Long = 0, callback: () -> Unit) {
    addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
        postDelayed(callback, delay)
    }
}

/**
 * Runs [action] on the next layout change of [this] view.
 * If the view is already laid out, then the action gets invoked immediately.
 */
inline fun View.doOnLayout(crossinline action: () -> Unit) {
    if (ViewCompat.isLaidOut(this)) {
        action.invoke()
    } else {
        addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                v.removeOnLayoutChangeListener(this)
                action.invoke()
            }
        })
    }
}

private val insetRect = Rect()
fun View.onApplyWindowInsets(body: (Rect) -> Unit) {
    setOnApplyWindowInsetsListener { _, insets ->
        insetRect.set(
            insets.stableInsetLeft,
            insets.systemWindowInsetTop,
            insets.systemWindowInsetRight,
            insets.systemWindowInsetBottom
        )
        body.invoke(insetRect)
        insets
    }
}

fun View.runOnAttach(r: () -> Unit) {
    if (isAttachedToWindow) {
        throw IllegalStateException("View is already attached to window")
    }
    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) {
            r.invoke()
            v.removeOnAttachStateChangeListener(this)
        }

        override fun onViewDetachedFromWindow(v: View) {
            v.removeOnAttachStateChangeListener(this)
        }
    })
}

fun View.runWhenOnAttach(r: () -> Unit) {
    if (isAttachedToWindow) r.invoke()
    else {
        addOnAttachStateChangeListener(object: View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                r.invoke()
            }

            override fun onViewDetachedFromWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
            }
        })
    }
}

fun View.doOnAttachToWindow(callback: () -> Unit) {
    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) {
            removeOnAttachStateChangeListener(this)
            callback.invoke()
        }

        override fun onViewDetachedFromWindow(v: View) {}
    })
}

inline fun View.doOnDetachFromWindow(crossinline callback: () -> Unit) {
    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewDetachedFromWindow(v: View) {
            removeOnAttachStateChangeListener(this)
            callback.invoke()
        }

        override fun onViewAttachedToWindow(v: View) {}
    })
}

fun View.doOnVisibilityChange(callback: (View) -> Unit) {
    val currentView = this
    val currentVisibility = this.visibility

    val layoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            val newVisibility = currentView.visibility
            if (currentVisibility != newVisibility) {
                callback.invoke(currentView)
                currentView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        }
    }
    this.viewTreeObserver.addOnGlobalLayoutListener(layoutListener)

    this.doOnDetachFromWindow {
        currentView.viewTreeObserver.removeOnGlobalLayoutListener(layoutListener)
    }
}

/**
 * Вызывает [callback], если позиция/размеры View не менялись более [stableDurationMs].
 * Например, это полезно, если View находится в процессе анимации/измерения или типа того
 * и мы хотим сделать что-то, когда позиция/размеры View "устаканятся"
 */
fun View.doOnLayoutPositionStable(
    stableDurationMs: Long = 300L,
    callback: (View) -> Unit
) {
    this.doOnLayoutPositionStable(stableDurationMs, callback, abortCallback = null)
}

/**
 * Вызывает [callback], если позиция/размеры View не менялись более [stableDurationMs].
 * Например, это полезно, если View находится в процессе анимации/измерения или типа того
 * и мы хотим сделать что-то, когда позиция/размеры View "устаканятся"
 */
fun View.doOnLayoutPositionStable(
    stableDurationMs: Long = 300L,
    callback: (View) -> Unit,
    abortCallback: (() -> Unit)? = null
) {
    // Необходимо для отложенного запуска и отмены [callback]
    // Увы, но view.postDelayed & removeCallbacks как-то не умеет корректно убирать delayedRunnable
    // (и я не понял почему)
    val handler = Handler(Looper.getMainLooper())
    val currentView = this

    // Инициализируется позже. Увы, нужные нам callback будут ссылаться друг на друга,
    // потому разносим реализации аля lateinit
    var doOnLayoutPositionStable: Runnable? = null

    // Секция с подписчиками:
    // 1) На каждое обновление layout мы перезапускаем таймер на вызов [callback]
    // 2) На унчитожение View или её отсоединение от экрана мы отменяем все таймеры и вызов [callback]
    // 3) Если позиция/размер View не менялся [stableDurationMs], то делаем вызов [callback]
    val doOnLayoutChangeListener = View.OnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed(doOnLayoutPositionStable!!, stableDurationMs)
    }
    val doOnDetachFromWindowListener = {
        handler.removeCallbacksAndMessages(null)
        abortCallback?.invoke()
        currentView.removeOnLayoutChangeListener(doOnLayoutChangeListener)
    }
    doOnLayoutPositionStable = Runnable {
        handler.removeCallbacksAndMessages(null)
        currentView.removeOnLayoutChangeListener(doOnLayoutChangeListener)
        callback.invoke(currentView)
    }

    // Собственно, делаем подписки на нужные события View и запускаем таймер на вызов [callback]
    currentView.addOnLayoutChangeListener(doOnLayoutChangeListener)
    currentView.doOnDetachFromWindow(doOnDetachFromWindowListener)
    handler.postDelayed(doOnLayoutPositionStable, stableDurationMs)
}

/**
 * Копипаст [View.doOnLayoutPositionStable], но тут отслежиаются координаты, что полезно например при скролле
 * Кроме этого проверяется нахождение View на экране
 * Вызывает [callback], если позиция View не менялись более [stableDurationMs].
 */
fun View.doOnVisibleOnScreenAndStable(
    stableDurationMs: Long = 300L,
    callback: (View) -> Unit
) {
    this.doOnVisibleOnScreenAndStable(stableDurationMs, callback, null)
}

/**
 * Копипаст [View.doOnLayoutPositionStable], но тут отслежиаются координаты, что полезно например при скролле
 * Кроме этого проверяется нахождение View на экране
 * Вызывает [callback], если позиция View не менялись более [stableDurationMs].
 * [abortCallback] исполняется, когда [callback] выполнился или когда [callback] пропускается.
 * [abortCallback] может использоваться для размещения в нём освобождения ресурсов.
 */
fun View.doOnVisibleOnScreenAndStable(
    stableDurationMs: Long = 300L,
    callback: (View) -> Unit,
    abortCallback: ((WeakReference<Handler>) -> Unit)? = null
): WeakReference<Handler> {
    if (!this.isVisible) error("view should be visible")

    val handler = Handler(Looper.getMainLooper())
    val weakHandler = WeakReference(handler)
    val currentView = this

    val emptyRect = Rect(0, 0, 0, 0)
    val screenRect = Rect(0, 0, Screen.width(), Screen.height())
    var prevRect = currentView.getVisibleRect()

    var doOnVisibleOnScreenAndStable: Runnable? = null

    val doOnDetachFromWindowListener = {
        abortCallback?.invoke(weakHandler)
        handler.removeCallbacksAndMessages(null)
    }

    fun repeatCheck() { handler.postDelayed(doOnVisibleOnScreenAndStable!!, stableDurationMs) }

    doOnVisibleOnScreenAndStable = Runnable {
        val newRect = currentView.getVisibleRect()
        if (newRect != emptyRect && newRect == prevRect && screenRect.contains(newRect)) {
            handler.removeCallbacksAndMessages(null)
            callback.invoke(currentView)
        } else if (currentView.isAttachedToWindow && currentView.isVisible) {
            prevRect = newRect
            repeatCheck()
        }
    }

    currentView.doOnDetachFromWindow(doOnDetachFromWindowListener)
    if (currentView.isAttachedToWindow) {
        repeatCheck()
    } else {
        currentView.doOnAttachToWindow {
            repeatCheck()
        }
    }
    return weakHandler
}

fun View.doWhenWindowIsActive(callback: () -> Unit) {
    context.toActivitySafe()?.let { activity ->
        val window = activity.window
        if (window != null && window.isActive) {
            callback.invoke()
        } else {
            this.doOnAttachToWindow(callback)
        }
    }
}

fun View.getPercentageOnScreen(viewBounds: Rect): Float {
    this.getLocalVisibleRect(viewBounds)
    val top = viewBounds.top
    val bottom = viewBounds.bottom
    if (top < 0 && bottom < 0 && top < bottom) {
        return 0f
    } else if (viewBounds.top >= this.bottom) {
        return 0f
    } else {
        val height = Math.abs(top - bottom).toFloat()
        return Math.min(1.0f, Math.round(height / this.height.toFloat() * 100) / 100f)
    }

}

fun Context.getNavigationBarHeight(): Int {
    val orientation = resources.configuration.orientation;
    val resourceId = resources.getIdentifier(if (orientation == Configuration.ORIENTATION_PORTRAIT) "navigation_bar_height" else "navigation_bar_width", "dimen", "android");
    return if (resourceId > 0) resources.getDimensionPixelOffset(resourceId) else 0;
}

fun Activity.isImmersiveModeOrNoNavigationBar(): Boolean {
    val rootView = findViewById<View>(android.R.id.content)
    val decorView = window.decorView
    return if (rootView != null) {
        decorView.bottom == rootView.bottom
    } else {
        true
    }
}

fun View.onViewSizeReady(listener: (View) -> Unit) {
    if (measuredHeight > 0 || measuredWidth > 0) {
        listener.invoke(this)
        return
    }
    this.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
        override fun onLayoutChange(v: View?, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
            this@onViewSizeReady.removeOnLayoutChangeListener(this)
            listener.invoke(this@onViewSizeReady)
        }
    })
}


fun View.preDrawListenerOneshot(listener: (View) -> Unit) {
    this.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            this@preDrawListenerOneshot.viewTreeObserver.removeOnPreDrawListener(this)
            listener.invoke(this@preDrawListenerOneshot)
            return true
        }

    })
}

class OnViewSizeChangedListener(
    private val listener: (View, width: Int, height: Int) -> Unit
) : View.OnLayoutChangeListener {
    private var oldWidth = 0
    private var oldHeight = 0

    override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
        if (v.measuredWidth != oldWidth || v.measuredHeight != oldHeight) {
            oldWidth = v.measuredWidth
            oldHeight = v.measuredHeight
            listener(v, oldWidth, oldHeight)
        }
    }
}

class OnLayoutAndSizeChangedListener(
    private val listener: (View, width: Int, height: Int) -> Unit
) : View.OnLayoutChangeListener {

    override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
        if (left != oldLeft || right != oldRight || top != oldTop || bottom != oldBottom) {
            listener(v, right - left, bottom - top)
        }
    }
}

@Deprecated("please, use view.addOnLayoutChangeListener() with OnViewSizeChangedListener param to be able to remove the listener")
fun View.onViewSizeChanged(listener: (View, width: Int, height: Int) -> Unit) {
    this.addOnLayoutChangeListener(OnViewSizeChangedListener { v, w, h -> listener(v, w, h) })
}

fun View.updatePaddingRelative(start: Int = paddingStart,
                               top: Int = paddingTop,
                               end: Int = paddingEnd,
                               bottom: Int = paddingBottom) {
    setPaddingRelative(start, top, end, bottom)
}

fun View.setMargin(left: Int = marginLeft(),
                   top: Int = marginTop(),
                   right: Int = marginRight(),
                   bottom: Int = marginBottom()) {
    val lp = (layoutParams as? ViewGroup.MarginLayoutParams?) ?: return
    lp.leftMargin = left
    lp.topMargin = top
    lp.rightMargin = right
    lp.bottomMargin = bottom
    layoutParams = lp
}

fun View.marginTop() = (layoutParams as? ViewGroup.MarginLayoutParams?)?.topMargin ?: 0
fun View.marginLeft() = (layoutParams as? ViewGroup.MarginLayoutParams?)?.leftMargin ?: 0
fun View.marginRight() = (layoutParams as? ViewGroup.MarginLayoutParams?)?.rightMargin ?: 0
fun View.marginBottom() = (layoutParams as? ViewGroup.MarginLayoutParams?)?.bottomMargin ?: 0

fun View.topWithMargins() = this.top - this.marginTop()
fun View.bottomWithMargins() = this.bottom + this.marginBottom()

fun View.usedWidth() = when (this.visibility) {
    View.GONE -> 0
    else -> this.marginLeft() + this.measuredWidth + this.marginRight()
}

fun View.usedHeight() = when (this.visibility) {
    View.GONE -> 0
    else -> this.marginTop() + this.measuredHeight + this.marginBottom()
}

fun AppCompatImageView.setTint(color: Int) {
    ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(color))
}

val location by lazy { ThreadLocal<IntArray>().apply { set(intArrayOf(0, 0)) } }
fun View.getViewRect(): Rect {
    getLocationOnScreen(location.get())
    val x = location.get()?.get(0) ?: 0
    val y = location.get()?.get(1) ?: 0
    return Rect(x, y, x + measuredWidth, y + measuredHeight)
}

fun View.getVisibleRect(): Rect {
    val rect = Rect()
    getGlobalVisibleRect(rect)
    return rect
}

fun View.getVisibleRectF(): RectF {
    return RectF().apply {
        set(getVisibleRect())
    }
}

fun View.getVisiblePercentage(): Float {
    return when {
        visibility != View.VISIBLE -> 0f
        windowVisibility != View.VISIBLE -> 0f
        alpha == 0f -> 0f
        !isAttachedToWindow -> 0f
        else -> {
            val viewRect = getVisibleRect()
            (viewRect.width() * viewRect.height()).toFloat() / (measuredHeight * measuredWidth)
        }
    }
}

fun View.setSize(size: Size) {
    setSize(size.width, size.height)
}

fun View.getSize(): Size {
    return Size(width, height)
}

fun View.setSize(width: Int, height: Int) {
    val lp = layoutParams ?: return
    if (width != lp.width || height != lp.height) {
        lp.width = width
        lp.height = height
        layoutParams = lp
    }
}

fun View.setWidth(width: Int) {
    this.setSize(width, this.layoutParams.height)
}

fun View.setHeight(height: Int) {
    this.setSize(this.layoutParams.width, height)
}

fun View.setWeight(weight: Float) {
    (layoutParams as? LinearLayout.LayoutParams?)?.weight = weight
}

fun View.setGravity(gravity: Int) {
    (layoutParams as? FrameLayout.LayoutParams?)?.gravity = gravity
}

fun TextView.setTextAppearanceCompat(@StyleRes resId: Int) {
    TextViewCompat.setTextAppearance(this, resId)
}

fun View.resetRenderProperties(transX: Float = 0f,
                               transY: Float = 0f,
                               transZ: Float = 0f,
                               scale: Float = 1f,
                               rotation: Float = 0f,
                               alpha: Float = 1f) {
    this.translationX = transX
    this.translationY = transY
    this.translationZ = transZ
    this.scaleX = scale
    this.scaleY = scale
    this.rotation = rotation
    this.alpha = alpha
}

@Suppress("UNUSED_EXPRESSION")
fun EditText.doOnActionDone(body: () -> Unit) {
    imeOptions = imeOptions or EditorInfo.IME_ACTION_DONE
    setOnEditorActionListener { _, action, _ ->
        if (action == EditorInfo.IME_ACTION_DONE) {
            body.invoke()
            true
        }
        false
    }
}

fun <T : View?> View.findViewByIdFromBottom(@IdRes id: Int): T? = findViewByIdFromBottomHelper<T>(parent, id)

private fun <T : View?> findViewByIdFromBottomHelper(view: ViewParent?, @IdRes id: Int): T? {
    return if (view is View) {
        val target = view.findViewById<T>(id)
        target ?: findViewByIdFromBottomHelper<T>((view as View).parent, id)
    } else {
        null
    }
}

inline fun <reified T : View?> View.findViewByIdFromBottom(noinline predicate: Function1<View, Boolean>): T? = findViewByIdFromBottomHelper(parent, predicate) as? T

fun findViewByIdFromBottomHelper(view: ViewParent?, predicate: Function1<View, Boolean>): View? {
    return if (view is View && predicate.invoke(view as View)) {
        view
    } else if (view is ViewGroup) {
        for (i in 0 until view.childCount) {
            val child = view.getChildAt(i)
            if (predicate.invoke(child)) {
                return child
            }
        }
        findViewByIdFromBottomHelper((view as View).parent, predicate)
    } else {
        null
    }
}

fun View.setOnClickListenerWithCoordinates(onClick: (Float, Float) -> Unit) {
    val onTouchListener = object : View.OnTouchListener {

        var lastUpEvent: MotionEvent? = null

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(p0: View?, p1: MotionEvent): Boolean {
            lastUpEvent = p1
            return false
        }
    }
    this.setOnTouchListener(onTouchListener)
}

fun View.requestApplyInsetsCompat() {
    requestApplyInsets()
}

fun View.hideStatusBar() {
    if (systemUiVisibility and HIDE_STATUS_BAR_FLAG != HIDE_STATUS_BAR_FLAG) {
        systemUiVisibility = systemUiVisibility or HIDE_STATUS_BAR_FLAG
    }
}

fun View.hideStatusAndNavBar() {
    if (systemUiVisibility and HIDE_STATUS_BAR_FLAG != HIDE_STATUS_BAR_FLAG) {
        systemUiVisibility = systemUiVisibility or (HIDE_STATUS_BAR_FLAG or SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }
}

/**
 * Returns true if a viewStub is substituted with an actual view. It means that this viewStub is
 * detached from its parent view.
 * Returns false otherwise
 */
val ViewStub.isInflated: Boolean
    get() = this.parent == null


var View.accessibilityTraversalAfterCompat: Int
    set(value) {
        accessibilityTraversalAfter = value
    }
    get() = accessibilityTraversalAfter

var View.accessibilityTraversalBeforeCompat: Int
    set(value) {
        accessibilityTraversalBefore = value
    }
    get() = accessibilityTraversalBefore


fun View.measureExactly(width: Int, height: Int) {
    val widthChildSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
    val heightChildSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)

    measure(widthChildSpec, heightChildSpec)
}
fun View?.getLayoutWidth() = this?.layoutParams?.width ?: 0
fun View?.getLayoutHeight() = this?.layoutParams?.height ?: 0

var View.isEnableState: Boolean
    get() = this.isEnabled
    set(value) {
        this.isEnabled = value
        this.alpha = if (value) 1f else 0.3f
    }

fun View.getDimen(@DimenRes dimenResourceId: Int): Int {
    return resources.getDimension(dimenResourceId).toInt()
}

fun View.scale(startScale: Float, endScale: Float, animationListener: Animation.AnimationListener) {
    val anim = ScaleAnimation(startScale, endScale, startScale, endScale,
        Animation.RELATIVE_TO_SELF, 0.5f,
        Animation.RELATIVE_TO_SELF, 0.5f)
    anim.fillAfter = true
    anim.duration = 300
    anim.setAnimationListener(animationListener)
    this.startAnimation(anim)
}

/**
 * It's like View#rootView but ignoring AttachInfo
 */
val View.rootParent: View
    get () {
        var root = this
        while (root.parent is View) {
            root = root.parent as View
        }
        return root
    }

/**
 * Returns [View.getViewTreeObserver] if it is alive or null.
 */
val View.aliveViewTreeObserver: ViewTreeObserver?
    get() {
        val observer = viewTreeObserver
        return if (observer.isAlive) observer else null
    }