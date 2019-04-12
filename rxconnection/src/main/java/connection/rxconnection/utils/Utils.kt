package connection.rxconnection.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri

import java.net.InetAddress
import java.net.NetworkInterface
import java.util.ArrayList
import java.util.Collections
import java.util.regex.Pattern


/**
 * Created by donydewantrie on 11/6/16.
 */

object Utils {

    var noSpecialChar = "^[\\w_\\s]+$"
    var address = "^[\\w\\s.]+$"
    var usernameRegex = "^[\\w_]+$"
    var inputForm = "^[\\w,\\s]+$"
    var onlyCharUnderScore = "^[a-zA-Z\\s]+$"
    var onlyDigits = "^\\d+$"

    val macAddress: String
        get() {
            try {
                val all = Collections.list(NetworkInterface.getNetworkInterfaces())
                for (nif in all) {
                    if (!nif.name.equals("wlan0", ignoreCase = true)) continue

                    val macBytes = nif.hardwareAddress ?: return ""

                    val res1 = StringBuilder()
                    for (b in macBytes) {
                        res1.append(String.format("%02X:", b))
                    }

                    if (res1.length > 0) {
                        res1.deleteCharAt(res1.length - 1)
                    }
                    return res1.toString()
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            return "02:00:00:00:00:00"
        }

    fun openPlayStore(activity: Activity) {
        try {
            activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + activity.packageName)))
        } catch (anfe: android.content.ActivityNotFoundException) {
            activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + activity.packageName)))
        }

    }

    fun getIPAddress(useIPv4: Boolean): String {
        try {
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                val addrs = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress) {
                        val sAddr = addr.hostAddress
                        val isIPv4 = sAddr.indexOf(':') < 0

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr
                        } else {
                            if (!isIPv4) {
                                val delim = sAddr.indexOf('%')
                                return if (delim < 0) sAddr.toUpperCase() else sAddr.substring(0, delim).toUpperCase()
                            }
                        }
                    }
                }
            }
        } catch (ex: Exception) {
        }

        return ""
    }

    fun regexUsername(username: String): Boolean {
        return regex(username, usernameRegex)
    }

    fun regexOnlyChar(character: String): Boolean {
        return regex(character, onlyCharUnderScore)
    }

    fun regexName(name: String): Boolean {
        return regex(name, onlyCharUnderScore)
    }

    fun regex(input: String, regex: String): Boolean {
        val pattern = Pattern.compile(regex)
        return pattern.matcher(input).matches()
    }

    fun isMyServiceRunning(serviceClass: Class<*>, context: Context): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    /*   public static <T extends ModelBaseSpinner> T[] newObject(T[] ts, Class<T> tClass) {
        ArrayList<T> arrayList = new ArrayList<>(Arrays.asList(ts));
        ArrayList<T> listT = new ArrayList<>();
        T t = null;
        try {
            t = tClass.newInstance();
            t.setId(0);
            t.setName("-");
            listT.add(t);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        listT.addAll(arrayList);
        return (T[]) listT.toArray(new ModelBaseSpinner[listT.size()]);
    }
*/
}
