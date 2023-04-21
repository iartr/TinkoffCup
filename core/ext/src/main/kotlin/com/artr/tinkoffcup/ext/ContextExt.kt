package com.artr.tinkoffcup.ext

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.style.ImageSpan
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.annotation.AnyRes
import androidx.annotation.ArrayRes
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.annotation.IdRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import io.reactivex.rxjava3.functions.Consumer
import java.io.File

private val handler by lazy { Handler(Looper.getMainLooper()) }

@JvmOverloads
fun Context?.toast(text: CharSequence?, length: Int = Toast.LENGTH_SHORT) {
    if (text.isNullOrEmpty() || this == null) {
        return
    }
    handler.post { Toast.makeText(this, text, length).show() }
}

@JvmOverloads
fun Context?.toast(text: Int, length: Int = Toast.LENGTH_SHORT) = this?.toast(this.getString(text), length)

fun Context.showError(resId: Int) = this.toast(resId)

fun Context.getQuantityString(stringId: Int, quantity: Int): String {
    return resources.getQuantityString(stringId, quantity, quantity)
}

fun Resources.getQuantityStringWithLong(@PluralsRes pluralRes: Int, quantity: Long, vararg args:Any?):String {
    val intQuantityEquivalent = (quantity % 1000).toInt()
    return getQuantityString(pluralRes, intQuantityEquivalent, *args)
}

fun Resources.getQuantityZeroString(@PluralsRes pluralRes: Int, quantity: Int, @StringRes zeroRes: Int, vararg args: Any): String {
    return if (quantity > 0) {
        getQuantityString(pluralRes, quantity, *args)
    } else {
        getString(zeroRes)
    }
}

fun Resources.getQuantityStringOrMany(
    @PluralsRes pluralsRes: Int,
    quantity: Long,
    @StringRes manyRes: Int,
    manyThreshold: Long = 1_000L,
    vararg args: Any?
): String {
    return if (quantity >= manyThreshold) getString(manyRes, *args) else getQuantityStringWithLong(pluralsRes, quantity, *args)
}

fun Context.getDimen(@DimenRes dimenRes: Int) = resources.getDimensionPixelSize(dimenRes)

fun Context.getResourceName(@AnyRes dimenRes: Int) = resources.getResourceName(dimenRes)

fun Context?.getConnectivityManager() = this?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

@SuppressLint("MissingPermission")
private fun Context.getNetworkInfo(): NetworkInfo? {
    val manager = getConnectivityManager()
    return try {
        manager?.activeNetworkInfo
    } catch (e: SecurityException) {
        Log.e("ContextExt", e.message, e)
        null
    }
}

fun Context?.hasPermission(permission: String): Boolean {
    return try {
        this?.checkCallingOrSelfPermission(permission) == PERMISSION_GRANTED
    } catch (ex: RuntimeException) {
        false
    }
}

fun Context?.hasPermissions(vararg permissions: String) = permissions.all { hasPermission(it) }

fun Context?.hasPermissions(permissions: Iterable<String>) = permissions.all { hasPermission(it) }

fun castToActivity(context: Context) = context.toActivitySafe()

fun View.getActivity(): Activity? {
    var view = this
    while (view.context.toActivitySafe() == null) {
        view = view.parent as? View ?: return null
    }
    return view.context.toActivitySafe()
}

fun Context.toActivitySafe(): Activity? {
    var context = this
    while (context !is Activity && context is ContextWrapper) context = context.baseContext
    return if (context is Activity) context else null
}

fun Context.toActivityUnsafe() = toActivitySafe()!!

inline fun <reified T : Activity> Context.toActivitySpecificSafe(): T? {
    var context = this
    while (context !is T && context is ContextWrapper) context = context.baseContext
    return if (context is T) context else null
}

inline fun <reified T : Activity> Context.toActivitySpecificUnsafe() = toActivitySpecificSafe<T>()!!

@ColorInt
fun Context.getColorCompat(@ColorRes id: Int): Int = ContextCompat.getColor(this, id)

fun Context.getDrawableCompat(@DrawableRes id: Int): Drawable? = AppCompatResources.getDrawable(this, id)

fun Context.getFontCompat(@FontRes id: Int): Typeface? = ResourcesCompat.getFont(this, id)

fun Context.getDrawableWithTint(@DrawableRes id: Int, @ColorRes colorId: Int): Drawable {
    return getDrawableAndTint(id, this.getColorCompat(colorId))
}

fun Context.getDrawableWithTintWithAttrRes(@DrawableRes id: Int, @AttrRes colorId: Int): Drawable {
    return getDrawableAndTint(id, this.resolveColor(colorId))
}

fun Context.getDrawableAndTint(@DrawableRes id: Int, @ColorInt colorInt: Int): Drawable {
    val d = DrawableCompat.wrap(getDrawableCompat(id)!!).mutate()
    DrawableCompat.setTint(d, colorInt)
    return d
}

fun Context.resolveDrawableAndTint(@AttrRes id: Int, @AttrRes colorId: Int): Drawable {
    val d = DrawableCompat.wrap(resolveDrawable(id)!!).mutate()
    DrawableCompat.setTint(d, resolveColor(colorId))
    return d
}

fun Context.getDrawableSpan(@DrawableRes id: Int) = createSpan(getDrawableCompat(id)!!)

fun Context.getDrawableSpan(@DrawableRes id: Int, @ColorRes colorId: Int) = createSpan(getDrawableWithTint(id, colorId))

private val typedValue = object : ThreadLocal<TypedValue>() {
    override fun initialValue() = TypedValue()
}

private fun typedValue() = typedValue.get()!!

fun Context.resolveDimen(@AttrRes resId: Int): Int {
    return if (theme.resolveAttribute(resId, typedValue(), true)) {
        TypedValue.complexToDimensionPixelSize(typedValue().data, resources.displayMetrics)
    } else 0
}

fun Context.resolveInt(@AttrRes resId: Int): Int {
    return if (theme.resolveAttribute(resId, typedValue(), true)) {
        typedValue().data
    } else 0
}

fun Context.resolveReference(@AttrRes resId: Int): Int {
    return if (theme.resolveAttribute(resId, typedValue(), true)) {
        typedValue().resourceId
    } else 0
}

fun Context.resolveDrawable(@AttrRes resId: Int): Drawable? {
    return if (theme.resolveAttribute(resId, typedValue(), true)) {
        getDrawableCompat(typedValue().resourceId)
    } else null
}

@ColorInt
fun Context.resolveColor(@AttrRes resId: Int) = resolveInt(resId)

fun Context.resolveColorStateList(@AttrRes resId: Int) = ColorStateList.valueOf(resolveInt(resId))

private fun createSpan(d: Drawable): Spannable {
    d.setBounds(0, 0, d.intrinsicWidth, d.intrinsicHeight)
    val sp = Spannable.Factory.getInstance().newSpannable("F")
    sp.setSpan(ImageSpan(d, ImageSpan.ALIGN_BOTTOM), 0, 1, 0)
    return sp
}

fun Context.startActivitySafe(intent: Intent, onError: Consumer<Throwable>? = null) {
    try {
        startActivity(intent)
    } catch (t: Throwable) {
        onError?.accept(t)
    }
}

fun Context.getLayoutInflater() = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

fun Context.unregisterReceiverSafe(receiver: BroadcastReceiver) {
    try {
        this.unregisterReceiver(receiver)
    } catch (t: Throwable) {
        Log.e("ContextExt", t.message, t)
    }
}

fun Context.getStringArray(@ArrayRes arrayRes: Int) = resources.getStringArray(arrayRes)

fun Context.getColorStateListForColor(@ColorRes res: Int) = ColorStateList.valueOf(ContextCompat.getColor(this, res))

fun Context.startActivityWithNewTaskFlag(intent: Intent) {
    val activity = toActivitySafe()
    if (activity == null) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    (activity ?: this).startActivity(intent)
}

/**
 * Creates a Uri which parses the given encoded URI string.
 * @param uriString an RFC 2396-compliant, encoded URI
 * @throws NullPointerException if uriString is null
 * @return Uri for this given uri string
 */
@Throws(Resources.NotFoundException::class)
fun Context.getUriToResource(@AnyRes resId: Int): Uri? = Uri.parse(
    ContentResolver.SCHEME_ANDROID_RESOURCE +
            "://" + resources.getResourcePackageName(resId) +
            '/' + resources.getResourceTypeName(resId) +
            '/' + resources.getResourceEntryName(resId)
)

fun Context.isAppResource(@AnyRes resId: Int) = packageName == resources.getResourcePackageName(resId)

fun Drawable.colorLayer(@IdRes layer: Int, @ColorInt color: Int) {
    (this as? LayerDrawable)
        ?.findDrawableByLayerId(layer)
        ?.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
}

fun Context.renameDatabase(oldName: String, newName: String) {
    val db = getDatabasePath(oldName)
    if (db.exists()) {
        File(db.parentFile, newName).let { newDb ->
            db.renameTo(newDb)
        }
        listOf("-journal", "-shm", "-wal").forEach { suffix ->
            val old = File(db.parentFile, oldName + suffix)
            if (old.exists()) {
                val new = File(db.parentFile, newName + suffix)
                old.renameTo(new)
            }
        }
    }
}
