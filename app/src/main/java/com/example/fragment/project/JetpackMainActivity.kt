package com.example.fragment.project

import android.R.attr.data
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import coil.compose.AsyncImage

//JetpackMainActivity 主要显示Jetpack组件知识点
class JetpackMainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        // 自定义退出过渡：150ms alpha + 轻微缩放，避免 splash 与首页之间的"硬切"
        splashScreen.setOnExitAnimationListener { provider ->
            val splashView = provider.view
            val alpha = ObjectAnimator.ofFloat(splashView, View.ALPHA, 1f, 0f)
            val scaleX = ObjectAnimator.ofFloat(splashView, View.SCALE_X, 1f, 1.2f)
            val scaleY = ObjectAnimator.ofFloat(splashView, View.SCALE_Y, 1f, 1.2f)
            AnimatorSet().apply {
                interpolator = AccelerateInterpolator()
                duration = 150L
                playTogether(alpha, scaleX, scaleY)
                doOnEndCompat { provider.remove() }
                start()
            }
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            //RowDemo()
            //ButtonDemo()
            //ColumnDemo()
            //BoxDemo()
            //WeightDemo()
            //SimpleTitleBarDemo()
            //TextFieldDemo()
            //WhiteTextFieldDemo()
            AsyncImageDemo()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

@Composable
fun RowDemo() {
    Row(

        /**
         * Modifier是Compose中最重要的修饰器之一，用于控制：
         * 大小，间距，背景，点击事件，对齐方式，裁剪形状等
         */
        modifier = Modifier.padding(30.dp)
            .background(Color.Yellow),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("左边")
        Text("中间")
        Text("右边")
    }
}

//按钮Button demo
@Composable
fun ButtonDemo(){
    var count by remember { mutableStateOf(0) }

    Button(
        modifier = Modifier.padding(30.dp)
            .background(Color.Yellow),
        onClick = { count++ }) {
        Text("Count = $count")
    }
}


//Column demo
@Composable
fun ColumnDemo() {
    Column(
        modifier = Modifier.padding(50.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("标题")
        Text("副标题")
        Text("说明")
    }
}

//Box用于叠放子组件
/**
 * 常用于文字覆盖图片
 * 占位与对齐
 * Badge红点
 * 居中内容
 */
@Composable
fun BoxDemo(){
    //Spacer(modifier = Modifier.height(30.dp))
    Box(
        modifier = Modifier.size(180.dp)
            .background(Color.Blue),
        contentAlignment = Alignment.Center){
        Text("wangling 居中")
    }
}

@Composable
fun WeightDemo(){
    //weight(1f) 被大量用于让文字区域占满剩余空间
    Row(modifier = Modifier.fillMaxWidth().padding(80.dp)) {
        Text("左侧", modifier = Modifier.weight(1f))
        Text("右侧")
    }
}

@Composable
fun SimpleTitleBarDemo() {
    TitleBar(
        title = "Compose 学习",
        navigationIcon = {
            Text(
                text = "返回",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        },
        actions = {
            Text(
                text = "保存",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
) {
    CenterAlignedTopAppBar(
        title = {
            Box(
                modifier = Modifier.height(45.dp)
            ) {
                Text(
                    text = title,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .basicMarquee(),
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        modifier = modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .statusBarsPadding()
            .height(45.dp),
        navigationIcon = navigationIcon,
        actions = actions,
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

/**
 * TextField内容输入框
 */
@Composable
fun TextFieldDemo(){
    var value by remember { mutableStateOf("") }

    //简单的表单输入
    /*TextField(
        modifier = Modifier.padding(50.dp),
        value = value,
        onValueChange = { value = it },
        label = { Text("请输入内容") }
    )*/

}

//用于封装TextFiled，主要特征是统一颜色风格
@Composable
fun WhiteTextFieldDemo(){
    var value by remember { mutableStateOf("") }
    WhiteTextField(
        value = value,
        onValueChange = {value = it},
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        placeholder = {Text("请输入关键字")},
    )
}


//TextField 的一个封装，主要特征是统一颜色风格
@Composable
fun WhiteTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    placeholder: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        textStyle = textStyle,
        placeholder = placeholder,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
            unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.onPrimaryContainer,
            errorCursorColor = MaterialTheme.colorScheme.onPrimaryContainer,
            focusedIndicatorColor = MaterialTheme.colorScheme.onPrimaryContainer,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.onPrimaryContainer,
            focusedPlaceholderColor = MaterialTheme.colorScheme.onPrimaryContainer,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
    )
}


@Composable
fun AsyncImageDemo(){
    //Compose中常见的图片加载方式之一
    AsyncImage(
        model = R.mipmap.avatar_1_raster,
        contentDescription = null,
        modifier = Modifier
            .width(120.dp)
            .padding(start = 30.dp, top = 50.dp)
            .aspectRatio(3f / 3f),
        contentScale = ContentScale.Crop
    )
}


/**
 * AnimatorSet 没有 KTX 的 doOnEnd，简单适配一下，避免引入额外依赖。
 */
private inline fun AnimatorSet.doOnEndCompat(crossinline action: () -> Unit) {
    addListener(object : android.animation.AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: android.animation.Animator) {
            action()
        }
    })
}