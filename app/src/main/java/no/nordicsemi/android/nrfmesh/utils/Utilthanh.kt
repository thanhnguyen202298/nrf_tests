@file:Suppress("unused", "UNUSED_ANONYMOUS_PARAMETER")

package no.nordicsemi.android.nrfmesh.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.VectorDrawable
import android.net.wifi.WifiManager
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.inputmethod.InputMethodManager
import androidx.collection.LongSparseArray
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*


/**
 * Created by Khoa Nguyen on 8/20/18.
 * Copyright (c) 2018. All rights reserved.
 * Email: khoantt91@gmail.com
 */

fun <T, K> getUncommonList(list1: List<T>, list2: List<T>, groupFunction: ((T) -> K)): List<T> {
    val sumList = list1 + list2
    return sumList.groupBy(groupFunction)
            .filter { it.value.size == 1 }
            .flatMap { it.value }
}

fun isMyServiceRunning(mContext: Context, serviceClass: Class<*>): Boolean {
    val manager = mContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val runningServiceInfoList = manager.getRunningServices(Integer.MAX_VALUE)
    for (service in runningServiceInfoList) {
        if (serviceClass.name == service.service.className) {
            return true
        }
    }
    return false
}

fun isValidEmail(target: CharSequence): Boolean = !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()

fun isValidPassword(target: CharSequence): Boolean {
    val pattern = "^(?=.*[0-9A-Z,@#\$%^&+=])(?=.*[a-z])(?=\\S+\$).{8,}\$" +
            "|^(?=.*[a-zA-Z,@#\$%^&+=])(?=.*[0-9])(?=\\S+\$).{8,}\$" +
            "|^(?=.*[0-9a-z,@#\$%^&+=])(?=.*[A-Z])(?=\\S+\$).{8,}\$" +
            "|^(?=.*[0-9A-Za-z])(?=.*[,@#\$%^&+=])(?=\\S+\$).{8,}\$"
    return target.matches(Regex(pattern))
}

fun isValidMacAddress(target: CharSequence): Boolean {
    val pattern = "([\\da-fA-F]{2}(?::|\$)){6}"
    return target.matches(Regex(pattern))
}

fun isValidText(target: CharSequence): Boolean = !TextUtils.isEmpty(target)

fun isValidFolderName(target: CharSequence): Boolean {
    val pattern = "^[^\\\\/?%*:|\"<>.]+\$"
    return target.matches(Regex(pattern))
}

fun hideKeyboard(mContext: Activity) {
    val view = mContext.currentFocus
    if (view != null) {
        val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun getResourceFromAttrs(context: Context?, attrs: Int): Int {
//    val ta = context?.obtainStyledAttributes(intArrayOf(attrs))
//    val id = ta?.getResourceId(0, R.drawable.ic_app_launcher)
//    ta?.recycle()
    return  -1
}

fun appendZero(text: String, lenght: Int): String {
    var textTemp = text
    if (TextUtils.isEmpty(textTemp))
        return ""
    if (textTemp.length == lenght)
        return textTemp
    for (i in textTemp.length until lenght) {
        textTemp = "0$textTemp"

    }
    return textTemp
}

fun showRequireGPSEnabled(context: Context) {
    val builder = AlertDialog.Builder(context)
//    builder.setMessage(context.getString(R.string.general_message_please_enabled_gps))
//            .setCancelable(false)
//            .setPositiveButton(context.getString(R.string.general_title_yes)) { dialog, id -> context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
//            .setNegativeButton(context.getString(R.string.general_title_cancel)) { dialog, id -> dialog.cancel() }
//    val alert = builder.create()
//    alert.show()
}

//fun getBitmap(resourceId: Int, context: Context): Bitmap? {
//    val vectorDrawable = ContextCompat.getDrawable(context, resourceId) as? VectorDrawable
//            ?: return null
//    val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth,
//            vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
//    val canvas = Canvas(bitmap)
//    vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
//    vectorDrawable.draw(canvas)
//    return bitmap
//}

private val hexArray = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'E', 'F')
fun bytesToHex(bytes: ByteArray): String {
    val hexChars = CharArray(bytes.size * 2)
    for (j in bytes.indices) {
        val v = (bytes[j].toInt() and 0xFF)

        hexChars[j * 2] = hexArray[v ushr 4]
        hexChars[j * 2 + 1] = hexArray[v and 0x0F]
    }
    return String(hexChars)
}

private const val HEX_STRING = "0123456789ABCDEF"
private val HEX_CHARS_ARRAY = HEX_STRING.toCharArray()
fun ByteArray.toHex(): String {
    val result = StringBuffer()

    forEach {
        val octet = it.toInt()
        val firstIndex = (octet and 0xF0).ushr(4)
        val secondIndex = octet and 0x0F
        result.append(HEX_CHARS_ARRAY[firstIndex])
        result.append(HEX_CHARS_ARRAY[secondIndex])
    }


    return result.toString()
}

fun String.hexStringToByteArray(): ByteArray {

    val b = ByteArray(this.length / 2)
    for (i in b.indices) {
        val index = i * 2
        val v = Integer.parseInt(this.substring(index, index + 2), 16)
        b[i] = v.toByte()
    }
    return b
}

fun Int.toHex(): String = Integer.toHexString(this).toUpperCase(Locale.getDefault())

fun hexToBytes(s: String): ByteArray {
    val len = s.length
    val data = ByteArray(len / 2)

    var i = 0
    while (i < len) {
        data[i / 2] = ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
        i += 2
    }
    return data
}

fun visibleView(vararg view: View?) = view.forEach { if (it?.visibility != View.VISIBLE) it?.visibility = View.VISIBLE }

fun goneView(vararg view: View?) = view.forEach { if (it?.visibility != View.GONE) it?.visibility = View.GONE }

fun hideView(vararg view: View?) = view.forEach { if (it?.visibility != View.INVISIBLE) it?.visibility = View.INVISIBLE }

val View?.hitRect: Rect get() = Rect().also { this?.getHitRect(it) }

val View?.localVisibleRect: Rect get() = Rect().also { this?.getLocalVisibleRect(it) }

val View?.globalVisibleRect: Rect get() = Rect().also { this?.getGlobalVisibleRect(it) }

val View?.isRectVisible: Boolean get() = this != null && globalVisibleRect != localVisibleRect

internal fun ViewPropertyAnimator.setAnimatorListener(onAnimationEnd: ((Animator?) -> Unit)? = null,
                                                      onAnimationStart: ((Animator?) -> Unit)? = null) = this.setListener(
        object : AnimatorListenerAdapter() {

            override fun onAnimationEnd(animation: Animator?) {
                onAnimationEnd?.invoke(animation)
            }

            override fun onAnimationStart(animation: Animator?) {
                onAnimationStart?.invoke(animation)
            }
        })

//@SuppressLint("WifiManagerPotentialLeak")
//fun getCurrentLocalIp(applicationContext: Context): String {
//    val wm = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
//    val wifiinfo = wm.connectionInfo
//    val ip = InetAddress.getByAddress(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(wifiinfo.ipAddress).array()).hostAddress
//    return if (ip == "0.0.0.0") DEFAULT_IP_ADDRESS else ip
//}
//
//@Suppress("UNCHECKED_CAST")
//fun FragmentStateAdapter.getItem(position: Int): Fragment? {
//    return this::class.superclasses.find { it == FragmentStateAdapter::class }
//            ?.java?.getDeclaredField("mFragments")
//            ?.let { field ->
//                field.isAccessible = true
//                val mFragments = field.get(this) as LongSparseArray<Fragment>
//                return@let mFragments[getItemId(position)]
//            }
//}