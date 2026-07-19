package gov.anzong.androidnga.core.board.data

import androidx.annotation.IntDef
import com.alibaba.fastjson.annotation.JSONField
import gov.anzong.androidnga.common.base.JavaBean

class BoardEntity : JavaBean {

    lateinit var name: String

    var id: String = ""

    // 父板块id
    @JSONField(serialize = false)
    var parentId: String? = null

    @BoardType
    var type: Int = BoardType.BOARD

    var fid: Int = 0

    var stid: Int = 0

    var children: MutableList<BoardEntity>? = null

    var head :String? = null

    @IntDef(BoardType.BOARD, BoardType.ASSEMBLE, BoardType.GROUP, BoardType.BOOKMARK)
    annotation class BoardType {
        companion object {
            // 正常板块
            const val BOARD: Int = 0

            // 合集板块
            const val ASSEMBLE: Int = 1

            // 板块分类
            const val GROUP: Int = 2

            // 板块分类
            const val BOOKMARK: Int = 3

        }
    }

    override fun equals(other: Any?): Boolean {
        return if (other is BoardEntity) {
            id == other.id
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
