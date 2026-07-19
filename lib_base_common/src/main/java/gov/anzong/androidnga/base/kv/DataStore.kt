package gov.anzong.androidnga.base.kv


object DataStore {

    enum class DataStoreType {
        SHARED_PREFERENCE,
    }

    private val defaultDataStore: IDataStore = SharedPreferenceDataStore()

    fun workAs(
        fileName: String?,
        type: DataStoreType = DataStoreType.SHARED_PREFERENCE,
        autoApply: Boolean = true
    ): IDataStore {
        if (type == DataStoreType.SHARED_PREFERENCE && fileName == null) {
            return defaultDataStore
        }
        if (type == DataStoreType.SHARED_PREFERENCE) {
            return SharedPreferenceDataStore(fileName, autoApply)
        } else {
            throw IllegalArgumentException("Unsupported data store type: $type")
        }
    }
}

