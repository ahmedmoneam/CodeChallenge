package com.ahmoneam.instabug.local.sharedpreferences

import android.content.Context
import android.content.SharedPreferences
import com.ahmoneam.instabug.core.di.SL

class SharedPreferences : ISharedPreferences {
    private val mPrefs: SharedPreferences =
        SL[Context::class.java].getSharedPreferences("instabug", Context.MODE_PRIVATE)

    override fun putString(key: String, value: String?) {
        mPrefs.edit().putString(key, value).apply()
    }

    override fun getString(key: String): String? {
        return mPrefs.getString(key, null)
    }

    override fun putBoolean(key: String, value: Boolean) {
        mPrefs.edit().putBoolean(key, value).apply()
    }

    override fun getBoolean(key: String): Boolean {
        return mPrefs.getBoolean(key, false)
    }

    override fun putInt(key: String, value: Int) {
        mPrefs.edit().putInt(key, value).apply()
    }

    override fun putIntAndCommit(key: String, value: Int) {
        mPrefs.edit().putInt(key, value).commit()
    }

    override fun getInt(key: String): Int {
        return mPrefs.getInt(key, 0)
    }

    override fun putLong(key: String, value: Long) {
        mPrefs.edit().putLong(key, value).apply()
    }

    override fun getLong(key: String): Long {
        return mPrefs.getLong(key, 0)
    }

    override fun clearData() {
        mPrefs.edit().clear().apply()
    }

    companion object
}
