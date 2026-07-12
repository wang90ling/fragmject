package com.example.fragment.project.ui.main.home

import androidx.lifecycle.viewModelScope
import com.example.fragment.project.data.Article
import com.example.fragment.project.data.ArticleList
import com.example.fragment.project.data.BannerList
import com.example.fragment.project.data.Coin
import com.example.fragment.project.data.TopArticle
import com.example.fragment.project.data.bean.request.RecommendRequest
import com.example.fragment.project.data.bean.response.CategoryItem
import com.example.fragment.project.data.bean.response.HomeRecommend
import com.example.fragment.project.data.bean.response.UserRecord
import com.example.fragment.project.data.repository.ArticleRepository
import com.example.fragment.project.data.repository.CachedResult
import com.example.fragment.project.data.repository.WanRepositoryProvider
import com.example.miaow.base.utils.GSonUtils
import com.example.miaow.base.utils.logD
import com.example.miaow.base.vm.BaseViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
    val selectedCategoryId: String = ""
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

            //添加“推荐”tab在第一个选项位置
            val firstCategoryItem:CategoryItem = CategoryItem("推荐","","","","001",0,true)
            val newCategoryListData = listOf(firstCategoryItem) + categoryListData;

            val request = RecommendRequest(1, 20)
            val response = articleRepo.getRecommendList(request)
            logD("wangling response:${response.toString()}")
            val data = response.data ?: HomeRecommend()
            _uiState.value = _uiState.value.copy(
                isRefreshing = false,
                isLoading = hasNextPage(),
                isFinishing = !hasNextPage(),
                homeRecommendResult = data,
                categoryList = newCategoryListData,
                //selectedCategoryId = firstCategoryItem.id
            )
        }
    }

    fun selectCategory(category: CategoryItem) {
        _uiState.value = _uiState.value.copy(
            selectedCategoryId = category.id
        )
        viewModelScope.launch {
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
                        nickName = article.author ?: "",
                        orderAmount = 0,
                        scoreAvg = 0.0,
                        sex = null,
                        userId = article.id ?: "",
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