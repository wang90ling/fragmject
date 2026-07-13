package com.example.fragment.project.ui.main.home

import androidx.lifecycle.viewModelScope
import com.example.fragment.project.data.bean.request.RecommendRequest
import com.example.fragment.project.data.bean.response.CategoryItem
import com.example.fragment.project.data.bean.response.HomeRecommend
import com.example.fragment.project.data.bean.response.UserRecord
import com.example.fragment.project.data.repository.ArticleRepository
import com.example.fragment.project.data.repository.WanRepositoryProvider
import com.example.miaow.base.utils.logD
import com.example.miaow.base.vm.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 不可变 UiState：所有字段均为 val，list 通过 copy 时生成新引用，
 * 这样 StateFlow 的 distinctUntilChanged 才能正确触发 Compose 重组。
 */
data class HomeUiState(
    val isRefreshing: Boolean = false,
    val isLoading: Boolean = false,
    val isFinishing: Boolean = false,
    val homeRecommendResult: HomeRecommend = HomeRecommend(),
    val categoryList: List<CategoryItem> = emptyList(),
    val selectedCategoryId: String = "",
    val topTab: TopTabPage = TopTabPage.Home,
)

class HomeViewModel(
    // 通过默认参数注入 Repository，单测时可传入替身实现
    private val articleRepo: ArticleRepository = WanRepositoryProvider.article,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())

    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        getHome()
    }

    fun getHome(userTriggered: Boolean = false) {
        // 首次加载时记录当前 tab，避免被覆盖
        val currentTopTab = _uiState.value.topTab
        _uiState.value = _uiState.value.copy(
            isRefreshing = userTriggered,
            isLoading = false,
            isFinishing = false
        )
        viewModelScope.launch {

            val categoryResponse = articleRepo.getCategoryList()
            logD("wangling categoryResponse:"+categoryResponse.toString())
            val categoryListData:List<CategoryItem> = categoryResponse.data ?:emptyList();
            logD("wangling categoryListData:"+categoryListData.toString())

            //添加"推荐"tab在第一个选项位置
            val firstCategoryItem:CategoryItem = CategoryItem("推荐","","","","001",0,true)
            val newCategoryListData = listOf(firstCategoryItem) + categoryListData;

            val request = RecommendRequest(1, 20)
            val response = articleRepo.getRecommendList(request)
            logD("wangling response:${response.toString()}")
            val data = response.data ?: HomeRecommend()
            // 首次加载时如果 selectedCategoryId 还没设置，初始化为"推荐"tab
            val currentSelected = _uiState.value.selectedCategoryId.ifBlank { firstCategoryItem.id }
            _uiState.value = _uiState.value.copy(
                isRefreshing = false,
                isLoading = hasNextPage(),
                isFinishing = !hasNextPage(),
                homeRecommendResult = data,
                categoryList = newCategoryListData,
                selectedCategoryId = currentSelected,
                topTab = currentTopTab,
            )
        }
    }

    /**
     * 切换顶部 Tab（点Ta / 派单厅 / 树洞 / 休闲玩）。
     * 仅切换内容区域视图，不影响：
     * 1. 选中的二级分类（selectedCategoryId），避免"点Ta"内的状态被误清；
     * 2. 当前列表数据（homeRecommendResult），避免反复重新请求接口。
     * 3. 重复点击同一 Tab 时不做任何变更，防止动画叠加造成状态抖动。
     */
    fun selectTopTab(page: TopTabPage) {
        if (_uiState.value.topTab == page) return
        _uiState.value = _uiState.value.copy(topTab = page)
    }

    fun selectCategory(category: CategoryItem) {
        _uiState.value = _uiState.value.copy(
            selectedCategoryId = category.id
        )
        viewModelScope.launch {
            if(category.id === "001"){
                val request = RecommendRequest(1, 20)
                val response = articleRepo.getRecommendList(request)
                logD("wangling response:${response.toString()}")
                val data = response.data ?: HomeRecommend()
                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    isLoading = hasNextPage(),
                    isFinishing = !hasNextPage(),
                    homeRecommendResult = data,
                )
            }else{
                val request = RecommendRequest(1, 20,category.id)
                val response = articleRepo.getRecommendListByTabId(request)
                logD("wangling response:${response.data?.records.toString()}")
                val data = response.data ?: HomeRecommend()
                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    isLoading = hasNextPage(),
                    isFinishing = !hasNextPage(),
                    homeRecommendResult = data,
                )
            }

        }
    }


    fun getNext() {
        _uiState.value = _uiState.value.copy(
            isRefreshing = false,
            isLoading = false,
            isFinishing = false
        )
        viewModelScope.launch {
            val response = articleRepo.getArticleList(getNextPage())
            updatePageCont(response.data?.pageCount?.toInt())
            val appended = response.data?.datas
            val current = _uiState.value
            val newRecords = if (!appended.isNullOrEmpty()) {
                (current.homeRecommendResult.records ?: emptyList()) + appended.map { article ->
                    UserRecord(
                        accompanyLevel = 0,
                        avatar = "",
                        categoryId = null,
                        categoryList = null,
                        coverImage = null,
                        grade = null,
                        introduced = null,
                        level = 0,
                        nickName = article.author,
                        orderAmount = 0,
                        scoreAvg = 0.0,
                        sex = null,
                        userId = article.id,
                        userPropListDto = null,
                        userPropDetailDto = null,
                        onlineFlag = null,
                        roomMedal = null,
                        roomLevelConfigDto = null,
                    )
                }
            } else {
                current.homeRecommendResult.records
            }
            val newResult = HomeRecommend(
                pageNo = current.homeRecommendResult.pageNo,
                pageSize = current.homeRecommendResult.pageSize,
                pages = current.homeRecommendResult.pages,
                records = newRecords,
                total = current.homeRecommendResult.total,
            )
            _uiState.value = current.copy(
                isRefreshing = false,
                isLoading = hasNextPage(),
                isFinishing = !hasNextPage(),
                homeRecommendResult = newResult,
            )
        }
    }
}