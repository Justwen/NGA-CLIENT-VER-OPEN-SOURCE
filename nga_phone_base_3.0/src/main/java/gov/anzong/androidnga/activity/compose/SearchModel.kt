package gov.anzong.androidnga.activity.compose

import com.alibaba.fastjson.JSON
import gov.anzong.androidnga.activity.compose.SearchViewModel.Companion.SEARCH_MODE_BOARD
import gov.anzong.androidnga.activity.compose.SearchViewModel.Companion.SEARCH_MODE_TOPIC
import gov.anzong.androidnga.activity.compose.SearchViewModel.Companion.SEARCH_MODE_USER
import gov.anzong.androidnga.base.util.PreferenceUtils
import gov.anzong.androidnga.common.PreferenceKey
import java.util.LinkedList

class SearchModel {

    private val topiKeyList: LinkedList<String>
        get() {
            val localKeyList = JSON.parseArray(
                PreferenceUtils.getData(getPreferenceKey(SEARCH_MODE_TOPIC), ""),
                String::class.java
            ) ?: emptyList()
            return if (localKeyList.size > SEARCH_KEY_MAX_COUNT) {
                LinkedList(localKeyList.subList(0, SEARCH_KEY_MAX_COUNT))
            } else {
                LinkedList(localKeyList)
            }
        }

    private val boardKeyList: LinkedList<String>
        get() {
            val localKeyList = JSON.parseArray(
                PreferenceUtils.getData(getPreferenceKey(SEARCH_MODE_BOARD), ""),
                String::class.java
            ) ?: emptyList()
            return if (localKeyList.size > SEARCH_KEY_MAX_COUNT) {
                LinkedList(localKeyList.subList(0, SEARCH_KEY_MAX_COUNT))
            } else {
                LinkedList(localKeyList)
            }
        }

    private val userKeyList: LinkedList<String>
        get() {
            val localKeyList = JSON.parseArray(
                PreferenceUtils.getData(getPreferenceKey(SEARCH_MODE_USER), ""),
                String::class.java
            ) ?: emptyList()
            return if (localKeyList.size > SEARCH_KEY_MAX_COUNT) {
                LinkedList(localKeyList.subList(0, SEARCH_KEY_MAX_COUNT))
            } else {
                LinkedList(localKeyList)
            }
        }

    private fun getPreferenceKey(searchMode: Int): String {
        return when (searchMode) {
            SEARCH_MODE_USER -> PreferenceKey.KEY_SEARCH_HISTORY_USER
            SEARCH_MODE_TOPIC -> PreferenceKey.KEY_SEARCH_HISTORY_TOPIC
            SEARCH_MODE_BOARD -> PreferenceKey.KEY_SEARCH_HISTORY_BOARD
            else -> ""
        }
    }

    private fun getHistoryKeyList(searchMode: Int): MutableList<String> {
        return when (searchMode) {
            SEARCH_MODE_USER -> userKeyList
            SEARCH_MODE_TOPIC -> topiKeyList
            SEARCH_MODE_BOARD -> boardKeyList
            else -> LinkedList()
        }
    }

    fun getSearchHistory(searchMode: Int): List<String> {
        return getHistoryKeyList(searchMode).toList()
    }

    fun putHistory(searchMode: Int, key: String): List<String>? {
        val keyList = getHistoryKeyList(searchMode)
        if (!keyList.contains(key)) {
            keyList.add(0, key)
            if (keyList.size > SEARCH_KEY_MAX_COUNT) {
                try {
                    keyList.removeLast()
                } catch (e: Throwable) {
                    keyList.removeAt(keyList.size - 1)
                }
            }
            saveHistory(searchMode, keyList)
            return keyList.toList()
        }
        return null
    }

    fun deleteHistory(searchMode: Int, key: String): List<String>? {
        val keyList = getHistoryKeyList(searchMode)
        if (keyList.remove(key)) {
            saveHistory(searchMode, keyList)
            return keyList.toList()
        } else {
            return null
        }
    }

    private fun saveHistory(searchMode: Int, keyList: List<String>) {
        PreferenceUtils.putData(getPreferenceKey(searchMode), JSON.toJSONString(keyList))
    }

    companion object {
        const val SEARCH_KEY_MAX_COUNT = 20
    }

}