package com.client.androidnga.account.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alibaba.android.arouter.launcher.ARouter
import com.client.androidnga.base.api.service.IFilterManagerService
import com.client.androidnga.base.network.ApiError
import com.client.androidnga.core.data.html.HtmlData
import com.justwen.androidnga.base.service.api.IUserManagerService
import gov.anzong.androidnga.base.util.StringUtils
import gov.anzong.androidnga.common.util.ForumUtils
import gov.anzong.androidnga.core.decode.ForumDecoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val repository = ProfileRepository()

    private val _uiState: MutableStateFlow<ProfileUiState> =
        MutableStateFlow(ProfileUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<ProfileEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()


    init {
        var uid = savedStateHandle["uid"] ?: ""
        var userName = savedStateHandle["username"] ?: ""
        if (uid.isEmpty() && userName.startsWith("UID")) {
            uid = userName.substring(3)
            userName = ""
        }
        loadProfile(uid, userName)
    }

    fun loadProfile(uid: String, userName: String) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repository.loadProfile(uid = uid, userName = userName)
            }
            if (result.isSuccess()) {
                _uiState.value = toUiState(result.data) ?: ProfileUiState()
            } else {
                _uiState.value = ProfileUiState(isError = true)
                toErrorEvent(result.error)?.let {
                    _event.trySend(it)
                }
            }
        }

    }

    private fun toUiState(bean: ProfileData?): ProfileUiState? {
        val item = bean ?: return null

        // 复杂口径在构造前算好，构造体保持一行一字段
        val uid = item.uid.toString()
        val signRaw = item.sign
        val adminForums = toAdminForumsState(item.adminForums)
        val reputation = toReputationState(item.fame, item.reputation)
        val isFilterUser = ARouter.getInstance()
            .navigation(IFilterManagerService::class.java)
            .isFilterUser(uid)
        val isCurrentUser = ARouter.getInstance()
            .navigation(IUserManagerService::class.java)
            .isActiveAccount(uid)
        val registerDate =
            if (item.regdate > 0) StringUtils.timeStamp2Date1(item.regdate.toString()) else "N/A"
        val muteTime =
            if (item.muteTime > 0) StringUtils.timeStamp2Date1(item.muteTime.toString()) else ""

        return ProfileUiState(
            userName = item.username,
            uid = uid,
            avatarUrl = item.avatar,
            email = item.email,
            posts = item.posts.toString(),
            money = item.money.toString(),
            registerDate = registerDate,
            loc = item.ipLoc,
            tel = item.phone,
            userGroup = item.group,
            // verified == -1 为 NUKED，< -1 为禁言
            isNuked = item.verified == -1,
            isMuted = item.verified < -1,
            muteTime = muteTime,
            isCurrentUser = isCurrentUser,
            isFilterUser = isFilterUser,
            signRaw = signRaw,
            sign = toSignHtml(signRaw.ifBlank { "无签名" }),
            adminForums = adminForums,
            reputation = reputation,
        )
    }

    private fun toAdminForumsState(
        adminForums: Map<String, String>
    ): List<ProfileUiState.AdminForum> {
        return adminForums.mapNotNull { (fid, forumName) ->
            val forumId = fid.toIntOrNull() ?: return@mapNotNull null
            if (forumName.isBlank()) return@mapNotNull null

            ProfileUiState.AdminForum(
                fid = forumId,
                name = forumName
            )
        }
    }

    private fun toReputationState(
        fame: Int = 0,
        reputation: Map<String, Map<String, Any>>
    ): List<ProfileUiState.Reputation> {
        return buildList {
            // 第一项先加入威望
            add(
                ProfileUiState.Reputation(
                    name = "威望",
                    value = (fame / 10.0f).toString(),
                    description = ""
                )
            )

            // 再加入其他声望
            reputation.entries
                .mapNotNullTo(this) { (_, value) ->
                    val name = value["0"]
                        ?.toString()
                        .orEmpty()

                    if (name.isBlank()) {
                        return@mapNotNullTo null
                    }

                    ProfileUiState.Reputation(
                        name = name,
                        value = value["1"]
                            ?.toString()
                            .orEmpty(),
                        description = value["2"]
                            ?.toString()
                            .orEmpty()
                    )
                }
        }
    }

    private fun toSignHtml(sign: String): String {
        val signHtml: String =
            ForumDecoder.decode(
                sign, HtmlData.create(sign, ForumUtils.getAvailableDomain() + "/")
            )
        val bgColorStr = String.format("%06x", 0xfff5d7 and 0xffffff)
        val fgColorStr = String.format("%06x", 0x000000 and 0xffffff)
        return "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"></head>" +
                "<body bgcolor=\"#$bgColorStr\"><font color=\"#$fgColorStr\" size=\"2\">" +
                "<div style=\"border: 3px solid rgb(204, 204, 204); padding: 2px;\">$signHtml</div>" +
                "</font></body></html>"
    }


    private fun toErrorEvent(error: ApiError?): ProfileEvent? {
        error?.cause?.message?.let {
            return ProfileEvent.Toast(it)
        }
        return null
    }

    fun onAction(action: ProfileAction) {
        when (action) {
            // 屏蔽是状态类意图：这里改完状态，菜单文案会随 state 一起更新
            is ProfileAction.ToggleFilterUser -> toggleFilterUser(
                action.uid,
                action.userName,
                action.isAdd
            )

            else -> _event.trySend(ProfileEvent.Navigate(action))
        }
    }

    private fun toggleFilterUser(uid: String, userName: String, isAdd: Boolean) {
        val filterManager = ARouter.getInstance().navigation(IFilterManagerService::class.java)
        if (isAdd) {
            filterManager.addFilterUser(uid, userName)
        } else {
            filterManager.removeFilterUser(uid)
        }
        _uiState.update { it.copy(isFilterUser = isAdd) }
        _event.trySend(
            ProfileEvent.Toast(if (isAdd) "已添加到过滤用户列表" else "已从过滤用户列表移除")
        )
    }
}




