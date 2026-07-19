package gov.anzong.androidnga.activity.compose.board

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gov.anzong.androidnga.arouter.ARouterConstants
import gov.anzong.androidnga.base.util.PreferenceUtils
import gov.anzong.androidnga.base.util.ToastUtils
import gov.anzong.androidnga.core.board.data.BoardEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import sp.phone.param.ParamKey
import sp.phone.util.ARouterUtils
import java.util.concurrent.TimeUnit

object ForumBoardViewModel : ViewModel() {

    val boardLiveData: MutableLiveData<List<BoardEntity>> = MutableLiveData()

    val bookmarkSizeLiveData: MutableLiveData<Int> = MutableLiveData(0)

    private val forumBoardModel = ForumBoardModel()

    const val BOARD_REMOTE_REQUEST_TIME_KEY = "board_remote_request_time"

    init {
        boardLiveData.postValue(forumBoardModel.loadBoardData())
        bookmarkSizeLiveData.postValue(forumBoardModel.bookmarkBoard.children?.size)
    }

    fun getBoardData(index: Int = 0): BoardEntity {
        return boardLiveData.value!![index]
    }

    fun addBookmarkBoard(name: String, fid: Int, stid: Int, head: String? = null) {
        bookmarkSizeLiveData.value = forumBoardModel.addBookmarkBoard(name, fid, stid,head)
    }

    fun isBookmarkBoard(fid: Int, stid: Int): Boolean {
        return forumBoardModel.isBookmarkBoard(fid, stid)
    }

    fun getBoardName(fid: Int, stid: Int): String {
        return forumBoardModel.getBoardName(fid, stid)
    }

    fun findBoard(fid: Int, stid: Int = 0): BoardEntity? {
        return forumBoardModel.findBoard(fid, stid)
    }

    fun addBookmarkBoard(name: String, fid: String, stid: String) {
        try {
            if (name.isEmpty()) {
                addBookmarkBoard(name, fid.toInt(), stid.toInt())
            }
        } catch (e: NumberFormatException) {
            ToastUtils.show("请输入正确的版面名称、ID或者合集ID")
        }
    }

    fun removeBookmarkBoard(fid: Int, stid: Int) {
        bookmarkSizeLiveData.value = forumBoardModel.removeBookmarkBoard(fid, stid)
    }

    fun removeAllBookmarkBoard() {
        forumBoardModel.removeAllBookmarkBoard()?.let {
            bookmarkSizeLiveData.value = it
        }
    }

    fun showTopicList(board: BoardEntity) {
        val fid = board.fid
        val stid = board.stid
        ARouterUtils.build(ARouterConstants.ACTIVITY_TOPIC_LIST)
            .withInt(ParamKey.KEY_FID, fid)
            .withInt(ParamKey.KEY_STID, stid)
            .withString(ParamKey.BOARD_HEAD, board.head)
            .withString(ParamKey.KEY_TITLE, board.name)
            .navigation()
        // todo 先临时放在这里，后面再统一定时处理
        requestRemoteBoardList()
    }

    private fun requestRemoteBoardList() {
        val long = PreferenceUtils.getData(BOARD_REMOTE_REQUEST_TIME_KEY, 0L)

        if (System.currentTimeMillis() - long < TimeUnit.DAYS.toMillis(1)) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val job = async {
                return@async forumBoardModel.loadIncrementalBoardList()
            }
            val result = job.await()
            if (result.isNotEmpty()) {
                forumBoardModel.mergeBoardList(result)
            }
            PreferenceUtils.putData(BOARD_REMOTE_REQUEST_TIME_KEY, System.currentTimeMillis())
        }
    }
}