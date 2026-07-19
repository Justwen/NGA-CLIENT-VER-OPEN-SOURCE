package gov.anzong.androidnga.core.board.data

import gov.anzong.androidnga.common.base.JavaBean

@Deprecated("")
class Board : JavaBean {

    var fid: Int = 0

    var name: String? = null

    var stid: Int = 0

    var boardKey: BoardKey? = null

    var boardHead: String? = null

    class BoardKey : JavaBean {

        var fid: Int? = null

        var stid: Int? = null
    }

}
