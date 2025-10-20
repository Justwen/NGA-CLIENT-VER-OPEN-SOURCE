package gov.anzong.androidnga.activity.compose.filter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alibaba.android.arouter.launcher.ARouter
import gov.anzong.androidnga.arouter.ARouterConstants
import gov.anzong.androidnga.base.util.ToastUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList
import sp.phone.common.User

class FilterState<T>(
    var title: String,
    var tips: String? = null,
    var addAction: ((String) -> Unit)? = null,
    var removeAction: ((Any) -> Unit)? = null,
    var showAction: ((Any) -> Unit)? = null,
    val filterData: MutableLiveData<List<T>> = MutableLiveData(emptyList())
)

class FilterWordViewModel : ViewModel() {

    private val filterWordModel = FilterWordModel()

    val filterStateList: List<FilterState<Any>>

    init {
        filterStateList = listOf(
            FilterState<User>("本地用户屏蔽").apply {
                tips = "对当前设备所有用户生效，不影响其他设备"
                filterData.value = FilterManager.userFilterList.toImmutableList()
                removeAction = { removeLocalFilterUser(it as User) }
                addAction = { addLocalFilterUser(it) }
                showAction = { showUserProfile(it as User) }
            },
            FilterState<FilterKeyword>("本地关键词屏蔽").apply {
                tips = "对当前设备所有用户生效，不影响其他设备，支持正则表达式"
                filterData.value = FilterManager.wordFilterList.toImmutableList()
                removeAction = { removeLocalFilterWord(it.toString()) }
                addAction = { addLocalFilterWord(it) }
            },
            FilterState<String>("官方用户屏蔽").apply {
                tips =
                    "在当前设备和其他设备生效，仅影响当前用户，和官网屏蔽规则保持一致，如需修改，请前往NGA官网\"我的\"->\"屏蔽帖子\"操作"
    //                removeAction = { removeFilterUser(it.toString()) }
    //                addAction = {
    //                    addFilterUser(it)
    //                }
                showAction = {
                    val data = it.toString().split("/")
                    showUserProfile(User(data[0], data[1]))
                }
            },
            FilterState<String>("官方关键词屏蔽").apply {
                tips =
                    "在当前设备和其他设备生效，仅影响当前用户，和官网屏蔽规则保持一致，如需修改，请前往NGA官网\"我的\"->\"屏蔽帖子\"操作"
    //                removeAction = { removeFilterWord(it.toString()) }
    //                addAction = {
    //                    addFilterWord(it)
    //                }
            }
        ).toImmutableList() as List<FilterState<Any>>

    }

    fun requestFilterList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val job = async {
                    return@async filterWordModel.requestRemoteFilterList()
                }
                val result = job.await()
                result.getOrNull()?.let {
                    (filterStateList[2] as FilterState<String>).filterData.postValue(it.first)
                    (filterStateList[3] as FilterState<String>).filterData.postValue(it.second)
                }
                result.exceptionOrNull()?.let {
                    ToastUtils.error(it.message)
                }
            } catch (e: Exception) {
                ToastUtils.error(e.message)
            }
        }
    }

    private fun updateFilterList(
        filterUserList: List<String>,
        filterWordList: List<String>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val job = async {
                    return@async filterWordModel.updateRemoteFilterList(
                        filterUserList,
                        filterWordList
                    )
                }
                val result = job.await()
                result.getOrNull()?.let {
                    (filterStateList[2] as FilterState<String>).filterData.postValue(filterUserList)
                    (filterStateList[3] as FilterState<String>).filterData.postValue(filterWordList)
                    ToastUtils.success(it)
                }
                result.exceptionOrNull()?.let {
                    ToastUtils.error(it.message)
                }
            } catch (e: Exception) {
                ToastUtils.error(e.message)
            }
        }
    }

    private fun showUserProfile(user: User) {
        ARouter.getInstance()
            .build(ARouterConstants.ACTIVITY_PROFILE)
            .withString("mode", "username")
            .withString("username", user.mNickName)
            .navigation()
    }


    private fun addFilterUser(data: String) {
        try {
            data.split("/")[0]
            data.split("/")[1]
        } catch (e: Exception) {
            ToastUtils.error("用户ID/用户名格式错误，请重新输入")
            return
        }

        val filterUserData =
            (filterStateList[2] as FilterState<String>).filterData.value!!.toMutableList()
        val filterWordData =
            (filterStateList[3] as FilterState<String>).filterData.value!!.toMutableList()
        if (filterUserData.contains(data)) {
            return
        }
        filterUserData.add(data)
        updateFilterList(filterUserData, filterWordData)
    }

    private fun addFilterWord(data: String) {
        val filterUserData =
            (filterStateList[2] as FilterState<String>).filterData.value!!.toMutableList()
        val filterWordData =
            (filterStateList[3] as FilterState<String>).filterData.value!!.toMutableList()
        if (filterWordData.contains(data)) {
            return
        }
        filterWordData.add(data)
        updateFilterList(filterUserData, filterWordData)
    }

    private fun removeFilterWord(data: String) {
        val filterUserData =
            (filterStateList[2] as FilterState<String>).filterData.value!!.toMutableList()
        val filterWordData =
            (filterStateList[3] as FilterState<String>).filterData.value!!.toMutableList()
        filterWordData.remove(data)
        updateFilterList(filterUserData, filterWordData)
    }

    private fun removeFilterUser(data: String) {
        val filterUserData =
            (filterStateList[2] as FilterState<String>).filterData.value!!.toMutableList()
        val filterWordData =
            (filterStateList[3] as FilterState<String>).filterData.value!!.toMutableList()
        filterUserData.remove(data)
        updateFilterList(filterUserData, filterWordData)
    }

    private fun addLocalFilterUser(data: String) {
        try {
            val uid = data.split("/")[0]
            val name = data.split("/")[1]
            FilterManager.addFilterUser(name, uid)
            // 临时方案
            filterStateList[0].filterData.value = FilterManager.userFilterList.toImmutableList()
        } catch (e: Exception) {
            ToastUtils.error("用户ID/用户名格式错误，请重新输入")
        }
    }

    private fun removeLocalFilterUser(user: User) {
        FilterManager.removeFilterUser(user)
        // 临时方案
        filterStateList[0].filterData.value = FilterManager.userFilterList.toImmutableList()
    }

    private fun addLocalFilterWord(word: String) {
        FilterManager.addFilterWord(word)
        filterStateList[1].filterData.value = FilterManager.wordFilterList.toImmutableList()
    }

    private fun removeLocalFilterWord(word: String) {
        FilterManager.removeFilterWord(word)
        filterStateList[1].filterData.value = FilterManager.wordFilterList.toImmutableList()
    }

}