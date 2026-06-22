package com.example.fragment.project.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fragment.project.MainRoute
import com.example.fragment.project.AppTheme
import com.example.fragment.project.components.LoadingContent
import com.example.fragment.project.data.CodeLoginRequest
import com.example.miaow.base.utils.logD
import kotlinx.coroutines.launch


/**
 * @author wangling
 * @date 2026/6/20 16:08
 * @description
 * 新版登录页：尽量还原截图中的视觉效果与结构。
 * 分为手机号验证码登录和手机号密码登录
 */
@Composable
fun LoginNewScreen(
    viewModel: LoginViewModel = viewModel(),
    onNavigate: (route: Any) -> Unit = {},
    onNavigateUp: () -> Unit = {},
    onPopBackStack: (route: Any) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var phoneText by rememberSaveable { mutableStateOf("") }
    var codeText by rememberSaveable { mutableStateOf("") }
    var agreed by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uiState.isLogin) {
        if (uiState.isLogin) onPopBackStack(MainRoute)
    }
    LaunchedEffect(Unit) {
        viewModel.messageEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }
    LaunchedEffect(uiState.codeLoginData) {
        logD("wangling codeLoginData:${uiState.codeLoginData.toString()}")
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) { data -> Snackbar(snackbarData = data) } }
    ) { innerPadding ->
        LoadingContent(isLoading = uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 22.dp)
                        .padding(top = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(
                            onClick = onNavigateUp,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = Color(0xFF1F1F1F),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "登录遇到问题?",
                            fontSize = 14.sp,
                            color = Color(0xFF686868),
                            modifier = Modifier.clickable { }
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "Hi ~欢迎登录",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF101010),
                        modifier = Modifier.padding(start = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(25.dp))

                    PhoneField(
                        value = phoneText,
                        onValueChange = { phoneText = it },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    CodeField(
                        value = codeText,
                        onValueChange = { codeText = it },
                        onClickGetCode = {},
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    ConsentRow(
                        agreed = agreed,
                        onToggle = { agreed = !agreed },
                        modifier = Modifier.padding(start = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(34.dp))

                    Button(
                        onClick = {
                            if (agreed) {
                                viewModel.loginByCode(
                                    CodeLoginRequest(
                                        phoneCountryCode = "+86",
                                        telephone = phoneText,
                                        code = codeText,
                                    )
                                )
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar("请先阅读并同意用户协议和隐私政策")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(27.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF36B6FF),
                                            Color(0xFF7070F6),
                                            Color(0xFFD949F7)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "登录",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "账号登录",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF363636),
                        modifier = Modifier.padding(start = 10.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "其他登录方式",
                            fontSize = 14.sp,
                            color = Color(0xFF9B9B9B)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SocialLoginButton(label = "QQ")
                            SocialLoginButton(label = "微信")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PhoneField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(25.dp),
        singleLine = true,
        placeholder = {
            Text(
                text = "请输入手机号",
                color = Color(0xFFB6B6B6),
                fontSize = 14.sp
            )
        },
        leadingIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 10.dp)
            ) {
                Text(
                    text = "+86",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF121212)
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color(0xFF757575),
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFA589FF),
            unfocusedBorderColor = Color(0xFFA589FF),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            cursorColor = Color(0xFFA589FF),
            focusedTextColor = Color(0xFF111111),
            unfocusedTextColor = Color(0xFF111111)
        )
    )
}

@Composable
private fun CodeField(
    value: String,
    onValueChange: (String) -> Unit,
    onClickGetCode: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(25.dp),
        singleLine = true,
        placeholder = {
            Text(
                text = "请输入验证码",
                color = Color(0xFFB6B6B6),
                fontSize = 14.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Shield,
                contentDescription = null,
                tint = Color(0xFF8A8A8A),
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(20.dp)
                        .background(Color(0xFFE6E6E6))
                )
                Text(
                    text = "获取验证码",
                    color = Color(0xFFA589FF),
                    fontSize = 15.sp,
                    modifier = Modifier
                        .padding(start = 12.dp, end = 10.dp)
                        .clickable(onClick = onClickGetCode)
                )
            }
        },
        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFA589FF),
            unfocusedBorderColor = Color(0xFFA589FF),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            cursorColor = Color(0xFFA589FF),
            focusedTextColor = Color(0xFF111111),
            unfocusedTextColor = Color(0xFF111111)
        )
    )
}

@Composable
private fun ConsentRow(
    agreed: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.clickable(onClick = onToggle),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(21.dp)
                .border(1.4.dp, Color(0xFFB9B9B9), CircleShape)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (agreed) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color(0xFFA589FF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(8.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(9.dp))
        Text(
            text = "我已阅读并同意用户协议和隐私政策",
            fontSize = 13.sp,
            color = Color(0xFF7D7D7D),
            lineHeight = 17.sp
        )
    }
}

@Composable
private fun SocialLoginButton(label: String) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .border(1.dp, Color(0xFFE5E5E5), CircleShape)
            .background(Color.White, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF222222),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun LoginNewScreenPreview() {
    AppTheme {
        LoginNewScreen()
    }
}
