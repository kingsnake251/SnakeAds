package com.snakeads.prolib.helper

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

interface SharedPreferenceHelper {
    fun setString(key: String, value: String)
    fun getString(key: String): String?

    fun setInt(key: String, value: Int)
    fun getInt(key: String): Int?

    fun setBoolean(key: String, value: Boolean)
    fun getBoolean(key: String): Boolean?

}

@Singleton
class PreferenceHelper @Inject constructor(
    @ApplicationContext context: Context
) : SharedPreferenceHelper {
    companion object {
        const val APP_PREFS = "app_prefs"
    }


    private val sharedPreferences by lazy {
        context.getSharedPreferences(APP_PREFS,Context.MODE_PRIVATE)
    }

    override fun setString(key: String, value: String) {
        sharedPreferences
            .edit()
            .putString(key, value)
            .apply()
    }

    override fun getString(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    override fun setInt(key: String, value: Int) {
        sharedPreferences
            .edit()
            .putInt(key, value)
            .apply()
    }

    override fun getInt(key: String): Int {
        return sharedPreferences.getInt(key, 0)
    }

    override fun setBoolean(key: String, value: Boolean) {
        sharedPreferences
            .edit()
            .putBoolean(key, value)
            .apply()
    }

    override fun getBoolean(key: String): Boolean? {
        return sharedPreferences.getBoolean(key, false)
    }
}