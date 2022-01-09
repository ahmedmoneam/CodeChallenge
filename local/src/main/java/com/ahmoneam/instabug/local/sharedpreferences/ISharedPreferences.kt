package com.ahmoneam.instabug.local.sharedpreferences

interface ISharedPreferences {
    fun putString(key: String, value: String?)
    fun getString(key: String): String?
    fun putBoolean(key: String, value: Boolean)
    fun getBoolean(key: String): Boolean
    fun putInt(key: String, value: Int)
    fun getInt(key: String): Int
    fun putIntAndCommit(key: String, value: Int)
    fun putLong(key: String, value: Long)
    fun getLong(key: String): Long
    fun clearData()
}