package gov.anzong.androidnga.base.kv

interface IDataStore {

    fun putData(key: String, value: String?)

    fun putData(key: String, value: Int)

    fun putData(key: String, value: Boolean)

    fun putData(key: String, value: Float)

    fun putData(key: String, value: Long)

    fun getData(key: String, defValue: String?): String?

    fun getData(key: String, defValue: Boolean): Boolean

    fun getData(key: String, defValue: Float): Float

    fun getData(key: String, defValue: Long): Long

    fun getData(key: String, defValue: Int): Int
}