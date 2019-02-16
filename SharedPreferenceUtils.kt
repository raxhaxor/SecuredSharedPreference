import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Base64


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
/**
 * Created by Akash Saggu(R4X)
 */
class SharedPreferenceUtils(context: Context) {
    val mSharedPreferences: SharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(context) }
    var mEditor: SharedPreferences.Editor = mSharedPreferences.edit()

    inline fun <reified T : Any> get(key: String, defaultValue: T): T {
        val encodedKey = encode(key)
        var value = mSharedPreferences.getString(encodedKey, "default")
        value = if (value == "default") {
            defaultValue.toString()
        } else {
            decode(value)
        }
        return when (T::class) {
            String::class -> value as T
            Int::class -> value.toInt() as T
            Long::class -> value.toLong() as T
            Boolean::class -> value.toBoolean() as T
            else -> throw IllegalArgumentException("This default value type is not accepted only pass String, Long, Int, Boolean.")
        }
    }

    inline fun <reified T : Any> set(key: String, value: T) {
        val encryptedKey = encode(key)
        val encodedValue = encode(value.toString())
        mEditor.putString(encryptedKey, encodedValue).also { mEditor.commit() }
    }


    inline fun <reified T> clear(vararg restore: Pair<String, T>) {
        mEditor.clear()
        mEditor.commit()
        restore.forEach {
            when (T::class) {
                String::class -> set(it.first, it.second as String)
                Int::class -> set(it.first, it.second as Int)
                Long::class -> set(it.first, it.second as Long)
                Boolean::class -> set(it.first, it.second as Boolean)
                else -> throw IllegalArgumentException("This value type is not accepted only pass String, Long, Int, Boolean.")
            }
        }
    }


    fun encode(plain: String): String {
        val b64encoded = Base64.encodeToString(plain.toByteArray(), Base64.DEFAULT)

        // Reverse the string
        val reverse = StringBuffer(b64encoded).reverse().toString()

        val tmp = StringBuilder()
        val offset = 4
        for (i in 0 until reverse.length) {
            tmp.append((reverse[i].toInt() + offset).toChar())
        }
        return tmp.toString()
    }

    fun decode(secret: String): String {
        val tmp = StringBuilder()
        val offset = 4
        for (i in 0 until secret.length) {
            tmp.append((secret[i].toInt() - offset).toChar())
        }

        val reversed = StringBuffer(tmp.toString()).reverse().toString()
        return String(Base64.decode(reversed, Base64.DEFAULT))
    }

 /*   inline fun <reified T : Any> get(key: String, defaultValue: T): T {
           val encryptedKey = encode(key)
           return when (T::class) {
               String::class -> mSharedPreferences.getString(encryptedKey, defaultValue as String) as T
               Int::class -> mSharedPreferences.getInt(encryptedKey, defaultValue as Int) as T
               Long::class -> mSharedPreferences.getLong(encryptedKey, defaultValue as Long) as T
               Boolean::class -> mSharedPreferences.getBoolean(encryptedKey, defaultValue as Boolean) as T
               Set::class -> mSharedPreferences.getStringSet(encryptedKey, defaultValue as Set<String>) as T
               else -> throw IllegalArgumentException("This default value type is not accepted only pass String, Long, Int, Boolean.")
           }
       }

       inline fun <reified T : Any> set(key: String, value: T) {
           val encryptedKey = encode(key)
           when (T::class) {
               String::class -> mEditor.putString(encryptedKey, value as String).also { mEditor.commit() }
               Int::class -> mEditor.putInt(encryptedKey, value as Int).also { mEditor.commit() }
               Long::class -> mEditor.putLong(encryptedKey, value as Long).also { mEditor.commit() }
               Boolean::class -> mEditor.putBoolean(encryptedKey, value as Boolean).also { mEditor.commit() }
               Set::class -> mEditor.putStringSet(encryptedKey, value as Set<String>).also { mEditor.commit() }
               else -> throw IllegalArgumentException("This value type is not accepted only pass String, Long, Int, Boolean.")
           }
       }*/


}
