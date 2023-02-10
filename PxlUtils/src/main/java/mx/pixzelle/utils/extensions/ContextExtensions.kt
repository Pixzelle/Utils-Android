package mx.pixzelle.utils.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import mx.pixzelle.utils.enums.DeviceResolution
import java.io.*
import java.util.*

fun Context.safeContext(): Context =
    takeUnless {
        try {
            isDeviceProtectedStorage
        } catch (ex: NoSuchMethodError) {
            false
        }
    }?.let {
        it.applicationContext.let { context ->
            ContextCompat.createDeviceProtectedStorageContext(context) ?: context
        }
    } ?: this

inline fun <reified T : Activity> Context.startActivity(block: Intent.() -> Unit = {}) {
    startActivity(Intent(this, T::class.java).apply(block))
}

inline fun <reified T : Activity> Fragment.startActivity(block: Intent.() -> Unit = {}) {
    startActivity(Intent(requireContext(), T::class.java).apply(block))
}

tailrec fun Context.activity(): Activity? = when (this) {
    is Activity -> this
    else -> (this as? ContextWrapper)?.baseContext?.activity()
}

fun Context.hideKeyboard(view: View) =
    (getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager)!!
        .hideSoftInputFromWindow(view.windowToken, 0)

fun Context.deviceResolution(): DeviceResolution {
    val density = resources.displayMetrics.density;
    return if (density <= 0.75) {
        DeviceResolution.LDPI
    } else if (density > 0.75 && density <= 1.0) {
        DeviceResolution.MDPI
    } else if (density > 1.0 && density <= 1.5) {
        DeviceResolution.HDPI
    } else if (density > 1.5 && density <= 2.0) {
        DeviceResolution.XHDPI
    } else if (density > 2.0 && density <= 3.0) {
        DeviceResolution.XXHDPI
    } else if (density > 3.0) {
        DeviceResolution.XXXHDPI
    } else {
        DeviceResolution.OTHER
    }
}

fun Context.openBrowser(url: String) {
    val browserIntent = Intent(Intent.ACTION_VIEW)
    browserIntent.data = Uri.parse(url)
    startActivity(browserIntent)
}

fun Context.openSettings(launcher: ActivityResultLauncher<Intent>) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    launcher.launch(intent)
}

fun Context.navigateToMaps(latitude: Double, longitude: Double) {
    val uri: String = java.lang.String.format(
        Locale.ENGLISH,
        "http://maps.google.com/maps?daddr=%f,%f", latitude, longitude
    )
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
    intent.setPackage("com.google.android.apps.maps")
    try {
        startActivity(intent)
    } catch (ex: ActivityNotFoundException) {
        try {
            val unrestrictedIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(unrestrictedIntent)
        } catch (_: ActivityNotFoundException) {

        }
    }
}

fun Context.navigateToWaze(latitude: Double, longitude: Double) {
    try {
        if (isPackageInstalled("com.waze")) {
            val url = "https://waze.com/ul?ll=${latitude},${longitude}&navigate=yes"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } else {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"))
            startActivity(intent)
        }
    } catch (ex: ActivityNotFoundException) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"))
        startActivity(intent)
    }
}

fun Context.isPackageInstalled(packageName: String, flags: Int = 0): Boolean {
    val pm: PackageManager = packageManager
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
        } else {
            @Suppress("DEPRECATION")
            pm.getPackageInfo(packageName, 0)
        }
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}

@SuppressLint("MissingPermission")
fun Context.hasInternetConnection(): Boolean {
    val connectivity = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivity.getNetworkCapabilities(connectivity.activeNetwork)

    return network?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
}

fun Context.isAppOnForeground(): Boolean {
    val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val appProcesses = activityManager.runningAppProcesses ?: return false
    val packageName = packageName
    return appProcesses.any { it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && it.processName == packageName }
}

fun Context.readFileFromAssets(filename: String): String {
    val inputStream: InputStream
    inputStream = BufferedInputStream(assets.open(filename))
    val buffer = CharArray(2048)
    val reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
    var n: Int
    val writer = StringWriter()
    n = reader.read(buffer)
    while (n != -1) {
        writer.write(buffer, 0, n)
        n = reader.read(buffer)
    }
    return writer.toString()
}

fun AssetManager.copyAssetFolder(srcName: String, dstName: String): Boolean {
    return try {
        var result : Boolean
        val fileList = this.list(srcName) ?: return false
        if (fileList.isEmpty()) {
            result = copyAssetFile(srcName, dstName)
        } else {
            val file = File(dstName)
            result = file.mkdirs()
            for (filename in fileList) {
                result = result and copyAssetFolder(
                    srcName + File.separator.toString() + filename,
                    dstName + File.separator.toString() + filename
                )
            }
        }
        result
    } catch (e: IOException) {
        e.printStackTrace()
        false
    }
}

fun AssetManager.copyAssetFile(srcName: String, dstName: String): Boolean {
    return try {
        val inStream = this.open(srcName)
        val outFile = File(dstName)
        val out: OutputStream = FileOutputStream(outFile)
        val buffer = ByteArray(1024)
        var read: Int
        while (inStream.read(buffer).also { read = it } != -1) {
            out.write(buffer, 0, read)
        }
        inStream.close()
        out.close()
        true
    } catch (e: IOException) {
        e.printStackTrace()
        false
    }
}
