package com.client.androidnga.account.profile

import androidx.compose.runtime.Immutable

@Immutable
data class ProfileUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val userName: String = "N/A",
    val uid: String = "0",
    val avatarUrl: String = "",
    val email: String = "",
    val tel: String = "",
    val userGroup: String = "",
    val posts: String = "",
    val money: String = "",
    val registerDate: String = "",
    val loc: String = "",
    val isNuked: Boolean = false,
    val isMuted: Boolean = false,
    val muteTime: String = "",
    val isCurrentUser: Boolean = false,
    val isFilterUser: Boolean = false,
    val sign: String = "",
    val signRaw: String = "",
    val reputation: List<Reputation> = emptyList(),
    val adminForums: List<AdminForum> = emptyList(),
) {
    data class AdminForum(val fid: Int, val name: String)

    data class Reputation(val name: String, val value: String, val description: String = "")
}

sealed interface ProfileAction {

    val uid: String
    val userName: String

    data class Finish(override val uid: String = "", override val userName: String = "") :
        ProfileAction

    data class SearchPost(override val uid: String, override val userName: String) :
        ProfileAction

    data class SearchReply(override val uid: String, override val userName: String) :
        ProfileAction

    data class OpenByBrowser(override val uid: String, override val userName: String) :
        ProfileAction

    data class SendMessage(override val uid: String, override val userName: String) :
        ProfileAction

    data class ToggleFilterUser(
        override val uid: String, override val userName: String, val isAdd: Boolean
    ) : ProfileAction

    data class ModifyAvatar(override val uid: String, override val userName: String) :
        ProfileAction

    data class ModifySign(
        override val uid: String = "",
        override val userName: String = "",
        val sign: String
    ) : ProfileAction

    data class OpenForum(
        val fid: Int,
        val forumName: String,
        override val uid: String = "",
        override val userName: String = ""
    ) : ProfileAction

}

sealed interface ProfileEvent {

    /**
     * 一次性提示，执行完即结束，不参与状态重组。
     */
    data class Toast(val msg: String) : ProfileEvent

    /**
     * 需要 Activity 执行的跳转。
     * 只承载导航类意图，状态类意图（如 [ProfileAction.ToggleFilterUser]）由 ViewModel 自行处理，不会下发到这里。
     */
    data class Navigate(val action: ProfileAction) : ProfileEvent

}
