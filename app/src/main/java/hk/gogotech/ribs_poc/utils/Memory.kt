package hk.gogotech.ribs_poc.utils

import android.content.Context
import android.preference.PreferenceManager
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

class Memory(mContext: Context) {
    private val context = mContext

    fun saveData(key: String, value: String?) {
        PreferenceManager
                .getDefaultSharedPreferences(context)
                .edit().putString(key, value).commit()
    }

    fun saveData(key: String, value: Int) {
        PreferenceManager
                .getDefaultSharedPreferences(context)
                .edit().putInt(key, value).commit()
    }

    fun saveData(key: String, value: Float) {
        PreferenceManager
                .getDefaultSharedPreferences(context)
                .edit().putFloat(key, value).commit()
    }

    fun saveData(key: String, value: Boolean) {
        PreferenceManager
                .getDefaultSharedPreferences(context)
                .edit().putBoolean(key, value).commit()
    }

    fun saveData(key: String, value: Long) {
        PreferenceManager
                .getDefaultSharedPreferences(context)
                .edit().putLong(key, value).commit()
    }

    fun saveObjectData(key: String, `object`: Serializable) {
        try {
            val fos = context.openFileOutput(key, Context.MODE_PRIVATE)
            val os = ObjectOutputStream(fos)
            os.writeObject(`object`)
            os.close()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun getObjectData(key: String): Any? {
        return getObjectData(key, null)
    }

    fun getObjectData(key: String, defaultObj: Any?): Any? {
        var obj: Any? = null
        try {
            val fis = context.openFileInput(key)
            val `is` = ObjectInputStream(fis)
            obj = `is`.readObject()
            `is`.close()
            fis.close()
        } catch (e: IOException) {
            context.deleteFile(key)
        } catch (e: ClassNotFoundException) {
        }

        return if (obj == null) defaultObj else obj
    }


    fun getStringData(key: String): String {
        return PreferenceManager
                .getDefaultSharedPreferences(context).getString(key, "")
    }

    fun getStringData(key: String, defaultValue: String): String {
        return PreferenceManager
                .getDefaultSharedPreferences(context).getString(key, defaultValue)
    }

    fun getIntData(key: String): Int {
        return PreferenceManager
                .getDefaultSharedPreferences(context).getInt(key, -1)
    }

    fun getIntData(key: String, defaultValue: Int): Int {
        return PreferenceManager
                .getDefaultSharedPreferences(context).getInt(key, defaultValue)
    }

    fun getLongData(key: String): Long {
        return PreferenceManager
                .getDefaultSharedPreferences(context).getLong(key, -1)
    }

    fun getFloatData(key: String): Float {
        return PreferenceManager
                .getDefaultSharedPreferences(context).getFloat(key, -1.0f)
    }

    fun getBooleanData(key: String): Boolean {
        return PreferenceManager
                .getDefaultSharedPreferences(context).getBoolean(key, false)
    }

    fun getBooleanData(key: String, defaultValue: Boolean): Boolean {
        return PreferenceManager
                .getDefaultSharedPreferences(context).getBoolean(key, defaultValue)
    }

}