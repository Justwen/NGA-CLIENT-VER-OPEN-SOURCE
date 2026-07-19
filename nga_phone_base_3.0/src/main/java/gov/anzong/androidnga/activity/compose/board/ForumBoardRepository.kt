package gov.anzong.androidnga.activity.compose.board

import android.content.Context
import com.alibaba.fastjson.JSON
import com.justwen.androidnga.base.network.retrofit.RetrofitHelper
import gov.anzong.androidnga.Utils
import gov.anzong.androidnga.activity.compose.board.ForumBoardViewModel.BOARD_REMOTE_REQUEST_TIME_KEY
import gov.anzong.androidnga.activity.compose.board.data.ForumsListBean
import gov.anzong.androidnga.base.util.PreferenceUtils
import gov.anzong.androidnga.base.utils.Files
import gov.anzong.androidnga.common.util.LogUtils
import gov.anzong.androidnga.core.board.data.BoardEntity
import java.io.File

object ForumBoardRepository {

    private const val BOARD_FILE_NAME = "board_list.json"

    private const val BOARD_BOOKMARK_FILE_NAME = "board_bookmark.json"

    private const val BOARD_REMOTE_FILE_NAME = "board_list_remote.json"

    private const val FORUM_URL: String = "app_api.php?__lib=home&__act=category"

    private const val BOARD_LOCAL_VERSION_CURRENT = 5

    private const val BOARD_LOCAL_VERSION_KEY = "board_local_version"

    fun loadLocalBoardList(context: Context): MutableList<BoardEntity> {
        val boardJson: String
        val fileName = BOARD_FILE_NAME
        val dataFile = File(context.filesDir, fileName)

        checkLocalDataVersion(dataFile)

        boardJson = if (!dataFile.exists()) {
            Files.readAssetString(context, fileName)
        } else {
            Files.readFile(dataFile)
        }
        return JSON.parseArray(
            boardJson, BoardEntity::class.java
        )
    }

    private fun checkLocalDataVersion(file: File) {
        val currentVersion =
            PreferenceUtils.getData(BOARD_LOCAL_VERSION_KEY, BOARD_LOCAL_VERSION_CURRENT)
        if (currentVersion != BOARD_LOCAL_VERSION_CURRENT) {
            PreferenceUtils.putData(BOARD_LOCAL_VERSION_KEY, BOARD_LOCAL_VERSION_CURRENT)
            PreferenceUtils.putData(BOARD_REMOTE_REQUEST_TIME_KEY, 0L)
            if (file.exists()) {
                Files.delete(file)
            }
        }
    }

    fun loadBookmarkBoardList(context: Context): BoardEntity {
        val fileName = BOARD_BOOKMARK_FILE_NAME
        val dataFile = File(context.filesDir, fileName)

        val bookmarkBoard = BoardEntity().apply {
            id = "bookmark"
            name = "我的收藏"
            type = BoardEntity.BoardType.BOOKMARK
            children = mutableListOf()
        }
        if (dataFile.exists()) {
            val boardJson = Files.readFile(dataFile)
            val boardList: MutableList<BoardEntity> = JSON.parseArray(
                boardJson, BoardEntity::class.java
            )
            bookmarkBoard.children!!.addAll(boardList)
        }
        return bookmarkBoard
    }

    fun writeBookmarkBoard(context: Context, boardList: List<BoardEntity>) {
        val boardJson = JSON.toJSONString(boardList)
        val fileName = BOARD_BOOKMARK_FILE_NAME
        val dataFile = File(context.filesDir, fileName)
        Files.writeFile(dataFile, boardJson)
    }


    fun writeLocalBoardList(context: Context, boardList: List<BoardEntity>) {
        PreferenceUtils.putData(BOARD_LOCAL_VERSION_KEY, BOARD_LOCAL_VERSION_CURRENT)
        val boardJson = JSON.toJSONString(boardList)
        val fileName = BOARD_FILE_NAME
        val dataFile = File(context.filesDir, fileName)
        Files.writeFile(dataFile, boardJson)
    }

    suspend fun requestRemoteBoardList(context: Context): ForumsListBean? {
        try {
            val url = Utils.getNGAHost() + FORUM_URL
            val result = RetrofitHelper.getInstance().serviceKt.getString(url)
            val bean = JSON.parseObject(result, ForumsListBean::class.java)
            if (bean != null) {
                writeRemoteBoardList(context, result)
            }
            return bean
        }  catch (e: Exception) {
            LogUtils.e("ForumBoardRepository", "requestRemoteBoardList: ${e.message}")
            return null
        }
    }

    fun loadRemoteBoardList(context: Context): ForumsListBean? {
        val fileName = BOARD_REMOTE_FILE_NAME
        val dataFile = File(context.filesDir, fileName)

        if (!dataFile.exists()) {
            return null
        }
        val result = Files.readFile(dataFile)
        return JSON.parseObject(result, ForumsListBean::class.java)
    }

    private fun writeRemoteBoardList(context: Context, boardJson: String) {
        val fileName = BOARD_REMOTE_FILE_NAME
        val dataFile = File(context.filesDir, fileName)
        PreferenceUtils.putData(BOARD_LOCAL_VERSION_KEY, BOARD_LOCAL_VERSION_CURRENT)
        Files.writeFile(dataFile, boardJson)
    }

}