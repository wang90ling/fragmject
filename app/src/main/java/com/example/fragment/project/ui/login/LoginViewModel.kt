package com.example.fragment.project.ui.login

import androidx.lifecycle.viewModelScope
import com.example.fragment.project.data.CodeLoginData
import com.example.fragment.project.data.CodeLoginRequest
import com.example.fragment.project.data.repository.UserRepository
import com.example.fragment.project.data.repository.WanRepositoryProvider
import com.example.miaow.base.utils.logD
import com.example.miaow.base.vm.BaseViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val isLogin: Boolean = false,
    val codeLoginData: CodeLoginData? = null
)

class LoginViewModel(
    private val userRepo: UserRepository = WanRepositoryProvider.user,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _messageEvent = Channel<String>(capacity = Channel.BUFFERED)
    val messageEvent = _messageEvent.receiveAsFlow()

    fun loginByPwd(username: String, password: String) {
        if (username.isBlank()) {
            sendMessage("用户名不能为空")
            return
        }
        if (password.isBlank()) {
            sendMessage("密码不能为空")
            return
        }
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            val response = userRepo.loginByPwd(username, password)
            handleLoginResponse(response)
        }
    }

    fun loginByCode(codeLoginRequest: CodeLoginRequest) {
        if (codeLoginRequest.telephone.isBlank()) {
            sendMessage("用户名不能为空")
            return
        }
        if (codeLoginRequest.code.isBlank()) {
            sendMessage("验证码不能为空")
            return
        }
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            val response = userRepo.loginByCode(codeLoginRequest)
            response.data?.let {
                //logD("wangling loginByCode it:${it.toString()}")
            }
            handleLoginResponse(response)
        }
    }

    private fun handleLoginResponse(response: com.example.fragment.project.data.Login) {
        val isSuccess = response.errorCode == "200"
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            isLogin = isSuccess,
            codeLoginData = if (isSuccess) response.data else null
        )
        if (!isSuccess && response.errorMsg.isNotBlank()) {
            sendMessage(response.errorMsg)
        }
    }

    private fun sendMessage(message: String) {
        viewModelScope.launch {
            _messageEvent.send(message)
        }
    }

}