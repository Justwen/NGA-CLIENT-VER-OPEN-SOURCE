package gov.anzong.androidnga.base.kv

import android.content.Context
import android.content.SharedPreferences
import gov.anzong.androidnga.base.util.ContextUtils

class SharedPreferenceDataStore(fileName: String? = null, private val autoApply: Boolean = true) :
    IDataStore {

    private val kvStore: SharedPreferences

    private val kvStoreEditor: SharedPreferences.Editor

    init {
        val context = ContextUtils.getContext()
        val name = fileName ?: context.packageName
        kvStore = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        kvStoreEditor = kvStore.edit()
    }

    override fun putData(key: String, value: String?) {
        kvStoreEditor.putString(key, value)
        if (autoApply) {
            kvStoreEditor.apply()
        }
    }

    override fun putData(key: String, value: Int) {
        kvStoreEditor.putInt(key, value)
        if (autoApply) {
            kvStoreEditor.apply()
        }
    }

    override fun putData(key: String, value: Boolean) {
        kvStoreEditor.putBoolean(key, value)
        if (autoApply) {
            kvStoreEditor.apply()
        }
    }

    override fun putData(key: String, value: Float) {
        kvStoreEditor.putFloat(key, value)
        if (autoApply) {
            kvStoreEditor.apply()
        }
    }

    override fun putData(key: String, value: Long) {
        kvStoreEditor.putLong(key, value)
        if (autoApply) {
            kvStoreEditor.apply()
        }
    }

    override fun getData(key: String, defValue: String?): String? {
        return kvStore.getString(key, defValue)
    }

    override fun getData(key: String, defValue: Boolean): Boolean {
        return kvStore.getBoolean(key, defValue)
    }

    override fun getData(key: String, defValue: Float): Float {
        return kvStore.getFloat(key, defValue)
    }

    override fun getData(key: String, defValue: Long): Long {
        return kvStore.getLong(key, defValue)
    }

    override fun getData(key: String, defValue: Int): Int {
        return kvStore.getInt(key, defValue)
    }

    fun apply() {
        kvStoreEditor.apply()
    }
}