package gov.anzong.androidnga.activity.compose.board

import com.alibaba.fastjson.JSON
import gov.anzong.androidnga.base.util.ContextUtils
import gov.anzong.androidnga.base.util.PreferenceUtils
import gov.anzong.androidnga.base.utils.ThreadProvider
import gov.anzong.androidnga.common.PreferenceKey
import gov.anzong.androidnga.core.board.data.Board
import gov.anzong.androidnga.core.board.data.BoardEntity
import java.util.Collections

class ForumBoardModel {

    private val boardList: MutableList<BoardEntity> = mutableListOf()

    private val boardMap: HashMap<String, BoardEntity> = HashMap()

    private val localBoardList: MutableList<BoardEntity>

    val bookmarkBoard: BoardEntity

    init {
        val context = ContextUtils.getContext()
        bookmarkBoard = ForumBoardRepository.loadBookmarkBoardList(context)
        localBoardList = ForumBoardRepository.loadLocalBoardList(context)
        boardList.add(bookmarkBoard)
        boardList.addAll(localBoardList)
        boardList.forEach {
            initBoardMap(it, null)
        }
        transferBookmarkBoards()
    }

    private fun initBoardMap(boardEntity: BoardEntity, parent: BoardEntity? = null) {
        with(boardEntity) {
            parentId = parent?.id
            id = generateBoardId(fid, stid, parentId) ?: id
        }
        boardMap[boardEntity.id] = boardEntity
        boardEntity.children?.let {
            it.forEach { data ->
                initBoardMap(data, boardEntity)
            }
        }
    }

    private fun transferBookmarkBoards() {
        if (!bookmarkBoard.children.isNullOrEmpty()) {
            return
        }
        val bookmarkJson = PreferenceUtils.getData(PreferenceKey.BOOKMARK_BOARD, "")
        if (bookmarkJson.isNullOrEmpty()) {
            return
        }
        val bookmarks: List<Board> = JSON.parseArray(bookmarkJson, Board::class.java)

        bookmarks.forEach {
            val boardEntity = BoardEntity()
            boardEntity.fid = it.fid
            boardEntity.name = it.name.toString()
            if (it.stid != 0) {
                boardEntity.stid = it.stid
            }
            boardEntity.id = generateBoardId(boardEntity.fid, boardEntity.stid).toString()
            bookmarkBoard.children!!.add(boardEntity)
        }
        saveBookmarkBoard()
    }

    private fun saveBookmarkBoard() {
        ThreadProvider.runOnSingleThread {
            ForumBoardRepository.writeBookmarkBoard(
                ContextUtils.getContext(), bookmarkBoard.children!!.toList()
            )
        }
    }

    fun loadBoardData(): MutableList<BoardEntity> {
        return boardList
    }

    fun addBookmarkBoard(name: String, fid: Int, stid: Int, head: String? = null): Int {
        val id = generateBoardId(fid, stid)
        val boardEntity = BoardEntity().also {
            it.fid = fid
            it.stid = stid
            it.id = id!!
            it.name = name
            it.head = head
        }
        bookmarkBoard.children?.let {
            if (!it.contains(boardEntity)) {
                it.add(boardEntity)
                saveBookmarkBoard()
                return it.size
            }
        }
        return 0
    }

    fun removeBookmarkBoard(fid: Int, stid: Int): Int {
        val id = generateBoardId(fid, stid)
        bookmarkBoard.children?.let {
            val iterator = it.iterator()
            while (iterator.hasNext()) {
                val boardEntity = iterator.next()
                if (boardEntity.id == id) {
                    iterator.remove()
                    break

                }
            }
            saveBookmarkBoard()
            return it.size
        }
        return 0
    }

    fun removeAllBookmarkBoard(): Int? {
        return bookmarkBoard.children?.let {
            it.clear()
            saveBookmarkBoard()
            return 0
        }
    }

    fun isBookmarkBoard(fid: Int, stid: Int): Boolean {
        val id = generateBoardId(fid, stid)
        bookmarkBoard.children?.let {
            it.forEach {
                if (it.id == id) {
                    return true
                }
            }
        }
        return false
    }

    private fun generateBoardId(fid: Int, stid: Int, parentId: String? = null): String? {
        var id: String? = null
        if (fid != 0) {
            id = fid.toString()
        }
        if (stid != 0) {
            id = id + "_" + stid
        }
        return id
    }

    fun swapBookmark(from: Int, to: Int) {
        val boards: List<BoardEntity> = bookmarkBoard.children!!
        if (from < to) {
            for (i in from until to) {
                Collections.swap(boards, i, i + 1)
            }
        } else {
            for (i in from downTo to + 1) {
                Collections.swap(boards, i, i - 1)
            }
        }
        saveBookmarkBoard()
    }

    suspend fun loadIncrementalBoardList(): List<BoardEntity> {
        val forumsListBean = ForumBoardRepository.requestRemoteBoardList(ContextUtils.getContext())
        val addChildList: MutableList<BoardEntity> = mutableListOf()
        forumsListBean?.result?.forEach {
            if (it.id == "other" || it.id == "wow" || it.id == "company") {
                it.groups?.forEach { it ->
                    val groupId = it.id
                    it.forums?.forEach { child ->
                        generateBoardId(child.id, child.stid)?.let { it ->
                            if (!boardMap.contains(it)) {
                                val boardEntity = BoardEntity().apply {
                                    id = it
                                    fid = child.id
                                    stid = child.stid
                                    parentId = groupId
                                    name = child.name!!
                                }
                                addChildList.add(boardEntity)
                            }
                        }
                    }
                }
            }
        }
        return addChildList
    }

    fun mergeBoardList(addChildList: List<BoardEntity>) {
        addChildList.forEach {
            boardMap[it.id] = it
            val parent = boardMap[it.parentId]
            parent?.children?.add(it)
        }
        saveData()
    }

    private fun saveData() {
        ThreadProvider.runOnSingleThread {
            ForumBoardRepository.writeLocalBoardList(
                ContextUtils.getContext(), localBoardList.toList()
            )
        }
    }

    fun getBoardName(fid: Int, stid: Int): String {
        val boardEntity = findBoard(fid, stid)
        return boardEntity?.name ?: ""
    }

    fun findBoard(fid: Int, stid: Int = 0): BoardEntity? {
        val id = generateBoardId(fid, stid)
        return boardMap[id]
    }

}