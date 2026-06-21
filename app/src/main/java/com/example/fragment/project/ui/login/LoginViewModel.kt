package com.example.fragment.project.ui.login

import androidx.lifecycle.viewModelScope
import com.example.fragment.project.data.CodeLoginData
import com.example.fragment.project.data.CodeLoginRequest
import com.example.fragment.project.data.repository.UserRepository
import com.example.fragment.project.data.repository.WanRepositoryProvider
import com.example.fragment.project.utils.WanHelper
import com.example.miaow.base.utils.logD
import com.example.miaow.base.vm.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val isLogin: Boolean = false,
    val message: String = "",
    val codeLoginData: CodeLoginData? = null
)

class LoginViewModel(
    private val userRepo: UserRepository = WanRepositoryProvider.user,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())

    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun resetMessage() {
        _uiState.update {
            it.copy(message = "")
        }
    }

    /**
     * 通过密码登录
     */
    fun loginByPwd(username: String, password: String) {
        if (username.isBlank()) {
            _uiState.update {
                it.copy(message = "用户名不能为空")
            }
            return
        }
        if (password.isBlank()) {
            _uiState.update {
                it.copy(message = "密码不能为空")
            }
            return
        }
        _uiState.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            val response = userRepo.loginByPwd(username, password)
            _uiState.update { state ->
                response.data?.let { it ->
                    //WanHelper.setUser(user)

                }
                state.copy(
                    isLoading = false,
                    isLogin = response.errorCode == "0",
                    message = response.errorMsg
                )
            }
        }
    }

    /**
     * 通过验证码登录
     */
    fun loginByCode(codeLoginRequest: CodeLoginRequest) {
        if (codeLoginRequest.telephone.isBlank()) {
            _uiState.update {
                it.copy(message = "用户名不能为空")
            }
            return
        }
        if (codeLoginRequest.code.isBlank()) {
            _uiState.update {
                it.copy(message = "验证码不能为空")
            }
            return
        }
        _uiState.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            val response = userRepo.loginByCode(codeLoginRequest)
            _uiState.update { state ->
                response.data?.let { it ->
                    logD("wangling loginByCode it:"+it.toString())
                    //WanHelper.setUser(user)
                }
                state.copy(
                    isLoading = false,
                    isLogin = response.errorCode == "0",
                    message = response.errorMsg,
                    codeLoginData = response.data,
                )
            }
        }
    }

}