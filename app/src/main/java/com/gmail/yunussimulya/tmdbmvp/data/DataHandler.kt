package com.gmail.yunussimulya.tmdbmvp.data

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.util.Base64
import android.util.DisplayMetrics
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Address
import android.location.Geocoder
import android.media.ExifInterface
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols


/**
 * Created by Yunus on 3/21/2018.
 */
class DataHandler {

    companion object {

        fun getFolderSize(f : File) : Long {
            var result : Long = 0
            if (f.isDirectory) {
                for (file in f.listFiles()) {
                    result += getFolderSize(f)
                }
            } else {
                result = f.length()
            }
            return result
        }

        fun getBitmapFromURL(link: String) : Bitmap? {
            try {
                val url = URL(link)
                val connection = url.openConnection() as HttpURLConnection
                connection.setDoInput(true)
                connection.connect()
                val input = connection.getInputStream()
                return BitmapFactory.decodeStream(input)
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }

        }

        fun convertDpToPixel(dp : Float) : Float {
            var result : Float = (dp * (Resources.getSystem().displayMetrics as Float / DisplayMetrics.DENSITY_DEFAULT))
            return result
        }

        fun convertPixelToDp(px : Float) : Float {
            var result : Float = (px / (Resources.getSystem().displayMetrics as Float / DisplayMetrics.DENSITY_DEFAULT))
            return result
        }

        fun getB64Auth(username : String, password : String) : String {
            val source : String = "$username:$password"
            val key = Base64.encodeToString(source.toByteArray(), Base64.URL_SAFE or Base64.NO_WRAP)
            return "Basic $key"
        }

        fun isEmail(email : String) : Boolean {
            val p : Pattern = Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])) {1}|([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})\$")
            return p.matcher(email).matches()
        }

        fun getCalendarFromString(dateString : String, datePattern : String) : Calendar {
            var calendar : Calendar = Calendar.getInstance()
            var format : SimpleDateFormat = SimpleDateFormat(datePattern)
            try {
                var date : Date = format.parse(dateString)
                calendar.time = date
            } catch (e : Exception) {
                e.printStackTrace()
            }
            return calendar
        }

        fun getDateFromString(dateString : String, datePattern : String, outputPattern : String) : String {
            var result = ""
            var format = SimpleDateFormat(datePattern)
            var targetFormat = SimpleDateFormat(outputPattern)
            try {
                result = targetFormat.format(format.parse(dateString))
            } catch (e : Exception) {
                e.printStackTrace()
            }
            return result
        }

        fun getDate(dateString : String, datePattern : String) : Date {
            var format = SimpleDateFormat(datePattern)
            try {
                return format.parse(dateString)
            } catch (e : Exception) {
                e.printStackTrace()
                return Calendar.getInstance().time
            }
        }

        fun reverseGeocode(context : Context, latitude : Double, longitude : Double) : Address? {
            if (Geocoder.isPresent()) {
                var geocoder = Geocoder(context, Locale("id"))
                try {
                    var addresses = geocoder.getFromLocation(latitude, longitude, 1)
                    return addresses.get(0)
                } catch (e : Exception) {
                    e.printStackTrace()
                }
            }
            return null
        }

        fun convertStreamToString(inputStream : InputStream) : String {
            val reader = BufferedReader(InputStreamReader(inputStream))
            var sb = StringBuilder()
            try {

                do {
                    val line = reader.readLine()
                    sb.append(line)
                } while (line != null)
            } catch (e : IOException) {
                e.printStackTrace()
            } finally {
                try {
                    inputStream.close()
                } catch (e : IOException) {
                    e.printStackTrace()
                }
            }
            return sb.toString()
        }

        fun getCameraPhotoOrientation(context : Context, imagePath : String) : Int {
            var result : Int = 0
            try {
                var imageFile = File(imagePath)
                var exif = ExifInterface(imageFile.absolutePath)
                var orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                result = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90
                    else -> 0
                }
            } catch (e : Exception) {
                e.printStackTrace()
            }
            return result
        }

        fun getResizedBitmap(img : Bitmap, newHeight : Int, newWidth : Int) : Bitmap? {
            try {
                var width = img.width
                var height = img.height
                var scaleWidth : Float = newWidth as Float / width
                var scaleHeight : Float = newHeight as Float / height
                var matrix = Matrix()
                matrix.postScale(scaleWidth, scaleHeight)
                return Bitmap.createBitmap(img, 0, 0, width, height, matrix, true)
            } catch (e : Exception) {
                return img
                e.printStackTrace()
            }
        }

        fun getResizedBitmapAndRotate(img : Bitmap, newHeight : Int, newWidth : Int, degree : Float) : Bitmap? {
            try {
                var width = img.width
                var height = img.height
                var scaleWidth : Float = newWidth as Float / width
                var scaleHeight : Float = newHeight as Float / height
                var matrix = Matrix()
                matrix.postScale(scaleWidth, scaleHeight)
                matrix.postRotate(degree, width as Float / 2, height as Float / 2)
                return Bitmap.createBitmap(img, 0, 0, width, height, matrix, true)
            } catch (e : Exception) {
                return img
                e.printStackTrace()
            }
        }

        fun decodeBitmapFromPathWithSampleSize(path : String, sampleSize : Int) : Bitmap {
            var options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.RGB_565
            options.inSampleSize = sampleSize
            return BitmapFactory.decodeFile(path, options)
        }

        fun getCurrency(input : Double) : String {
            var symbols = DecimalFormatSymbols()
            symbols.groupingSeparator = ','
            symbols.decimalSeparator = '.'
            return DecimalFormat("Ro #,###", symbols).format(input)
        }

        fun escapeJavaString(input : String) : String {
            var builder = StringBuilder()
            try {
                for (i : Int in 0..input.length) {
                    val c : Char = input.get(i)
                    if (!Character.isLetterOrDigit(c) && !Character.isSpaceChar(c) && !Character.isWhitespace(c)) {
                        var unicode = c.toString()
                        var code : Int = c as Int
                        if (code !in 0..255) {
                            unicode = "\\u" + Integer.toHexString(c)
                        }
                        builder.append(unicode)
                    } else {
                        builder.append(c)
                    }
                }
            } catch (e : Exception) {

            }
            return builder.toString()
        }

        fun isApplicationInstalled(activity : Activity, uri : String) : Boolean {
            var pm = activity.packageManager
            try {
                pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
                return true
            } catch (e : PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
             return false
        }

        fun getAge(birthdayInMillis : Long) : Int {
            if (birthdayInMillis <= 0) return 0
            val calendar = Calendar.getInstance()
            val todayYear = calendar.get(Calendar.YEAR)
            val todayDay = calendar.get(Calendar.DAY_OF_YEAR)
            calendar.timeInMillis = birthdayInMillis
            val birthdayYear = calendar.get(Calendar.YEAR)
            val birthdayDay = calendar.get(Calendar.DAY_OF_YEAR)
            var age = todayYear - birthdayYear
            if (todayDay < birthdayDay) age--
            return age
        }

        fun versionCompare(version1 : String, version2 : String) : Int {
            var s1 = Scanner(version1)
            var s2 = Scanner(version2)
            s1.useDelimiter("\\.")
            s2.useDelimiter("\\.")
            while (s1.hasNextInt() && s2.hasNextInt()) {
                var v1 = s1.nextInt()
                var v2 = s2.nextInt()
                if (v1 < v2) {
                    return -1
                } else if (v1 > v2) {
                    return 1
                }
            }
            if (s1.hasNextInt()) {
                return 1
            } else if (s2.hasNextInt()) {
                return -1
            }
            return 0
        }

        fun extractNumber(source : String) : String {
            var result : String = source.trim()
            try {
                var pt = Pattern.compile("[^0-9]")
                var match = pt.matcher(source)
                while (match.find()) {
                    var s = match.group()
                    result = result.replace("s", "")
                }
            } catch (e : Exception) {
                e.printStackTrace()
            }
            return result
        }
    }
}