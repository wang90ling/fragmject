# Android Jetpack Compose 基础学习资料

> 结合当前仓库 `fragmject` 的真实代码，系统讲解 Compose 的基本布局、常用组件、状态管理、列表、输入框、TopBar、卡片和页面组织方式，并配套可直接参考的代码 demo。

---

## 1. Compose 是什么

Jetpack Compose 是 Android 官方推荐的声明式 UI 框架。和传统 XML + View 的写法相比，它更强调：

- 用函数描述 UI
- 用状态驱动页面更新
- 组件可组合、可复用、可测试

你可以把 Compose 理解成：

```text
State -> UI
```

当状态变化时，界面会自动更新，而不是像传统 View 那样手动 `findViewById()`、`setText()`、`setVisibility()`。

在本仓库中，Compose 已经覆盖了主页面、搜索页、登录页、示例页和大量复用组件，非常适合用来学习真实项目中的写法。

---

## 2. Compose 的基础概念

### 2.1 `@Composable`

Compose 页面和组件都通过 `@Composable` 标记。

```kotlin
@Composable
fun Hello() {
    Text("Hello Compose")
}
```

它表示这个函数会参与 UI 构建。

### 2.2 `Modifier`

`Modifier` 是 Compose 中最重要的修饰器之一，用于控制：

- 大小
- 间距
- 背景
- 点击事件
- 对齐方式
- 裁剪形状

比如：

```kotlin
Text(
    text = "Hello",
    modifier = Modifier
        .padding(16.dp)
        .background(Color.Yellow)
)
```

### 2.3 状态驱动 UI

Compose 的核心就是状态。

```kotlin
var count by remember { mutableStateOf(0) }

Button(onClick = { count++ }) {
    Text("Count = $count")
}
```

按钮点击后 `count` 变化，UI 自动重组。

---

## 3. Compose 的常用布局

下面先讲最基础、最常用的布局容器。

### 3.1 `Row`

`Row` 用于横向排列子组件。

```kotlin
@Composable
fun RowDemo() {
    Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("左边")
        Text("中间")
        Text("右边")
    }
}
```

适合：

- 顶部工具栏
- 用户信息横排布局
- 按钮组
- 图标 + 文本组合

### 3.2 `Column`

`Column` 用于纵向排列子组件。

```kotlin
@Composable
fun ColumnDemo() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("标题")
        Text("副标题")
        Text("说明")
    }
}
```

适合：

- 卡片内容
- 表单页面
- 列表项内部布局

### 3.3 `Box`

`Box` 用于叠放子组件，常用于：

- 文字覆盖图片
- 占位与对齐
- Badge 红点
- 居中内容

```kotlin
@Composable
fun BoxDemo() {
    Box(
        modifier = Modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("居中")
    }
}
```

### 3.4 `Spacer`

`Spacer` 用于留白。

```kotlin
Spacer(modifier = Modifier.height(12.dp))
```

它比直接在某个组件上乱加 padding 更清晰。

### 3.5 `weight`

`weight` 是分配剩余空间的关键。

```kotlin
Row(modifier = Modifier.fillMaxWidth()) {
    Text("左侧", modifier = Modifier.weight(1f))
    Text("右侧")
}
```

在仓库的 `ArticleCard` 里，`weight(1f)` 被大量用于让文字区域占满剩余空间。

---

## 4. 结合项目代码理解布局

### 4.1 顶部栏 `TitleBar`

文件：`app/src/main/java/com/example/fragment/project/components/TitleBar.kt`

这是一个很典型的 Compose 顶部栏封装。

```kotlin
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
```

#### 这段代码体现了什么

1. `CenterAlignedTopAppBar` 是 Material3 的顶栏组件
2. `navigationIcon` 和 `actions` 通过参数暴露，方便外部定制
3. `basicMarquee()` 让长标题可以滚动展示
4. `statusBarsPadding()` 让内容避开状态栏
5. 使用 `MaterialTheme.colorScheme` 获取主题颜色

#### 你可以学到的布局知识

- 顶栏通常是 `Row` / `Box` / `TopAppBar` 的组合
- 可复用组件要把变动部分参数化
- 对齐、留白、状态栏适配都应该提前考虑

#### 类似 demo

```kotlin
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
```

---

## 5. Compose 中如何写输入框

### 5.1 普通 `TextField`

最简单的输入框：

```kotlin
@Composable
fun BasicTextFieldDemo() {
    var value by remember { mutableStateOf("") }

    TextField(
        value = value,
        onValueChange = { value = it },
        label = { Text("请输入内容") }
    )
}
```

适合：

- 表单输入
- 搜索框
- 登录页

### 5.2 仓库中的 `WhiteTextField`

文件：`app/src/main/java/com/example/fragment/project/components/SimpleTextField.kt`

这个组件是对 `TextField` 的一个封装，主要特征是统一颜色风格。

```kotlin
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
```

#### 学习点

- `TextFieldDefaults.colors()` 用于定制颜色
- 通过参数把字体、占位符、键盘配置都暴露出来
- `singleLine = true` 适合搜索框或单行输入

### 5.3 仓库中的 `ClearTextField`

它比普通 `TextField` 更复杂，展示了“如何自定义输入框外观”。

它的特点：

- 使用 `BasicTextField` 作为底层
- 自己拼装前后图标
- 输入时显示清除按钮
- 支持 `placeholder`
- 支持 `interactionSource`

#### 适合什么时候用

当你需要：

- 完全自定义样式
- 输入框里有多种图标布局
- 想控制焦点、边框、光标、下划线

#### 基础 demo

```kotlin
@Composable
fun ClearTextFieldDemo() {
    var value by remember { mutableStateOf("") }

    ClearTextField(
        value = value,
        onValueChange = { value = it },
        onClear = { value = "" },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = { Text("请输入关键词") },
        leadingIcon = { Text("🔍") }
    )
}
```

---

## 6. Compose 中如何写卡片

### 6.1 卡片的典型用途

卡片是 Compose 项目里最常用的复合组件之一，常用于：

- 新闻/文章列表
- 商品列表
- 用户信息展示
- 带封面图的内容流

### 6.2 仓库中的 `ArticleCard`

文件：`app/src/main/java/com/example/fragment/project/components/ArticleCard.kt`

这是一个非常适合学习的综合示例，因为它把多个 Compose 基础能力放在了一起：

- `Column` / `Row` / `Box`
- `Modifier.clickable`
- `weight`
- `clip`
- `RoundedCornerShape`
- `AsyncImage`
- 状态记忆 `remember`
- 协程 `rememberCoroutineScope`
- 点击后做网络操作

#### 代码结构摘要

```kotlin
@Composable
fun ArticleCard(
    data: Article,
    modifier: Modifier = Modifier,
    onNavigate: (route: Any) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    var collected by remember(data.id) { mutableStateOf(data.collect) }
    val collectResId = getCollectResId(collected)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(5.dp))
            .clickable { onNavigate(WebRoute(data.link)) }
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .fillMaxWidth()
    ) {
        // 头像、作者、标签
        // 标题、摘要、封面图
        // 底部分类、收藏按钮
    }
}
```

### 6.3 这个卡片学到了什么

#### 1. `remember` 保存局部状态

```kotlin
var collected by remember(data.id) { mutableStateOf(data.collect) }
```

这里的意思是：当前卡片的收藏状态由 Compose 自己保存，不直接修改数据模型。

#### 2. 点击事件

```kotlin
.clickable { onNavigate(WebRoute(data.link)) }
```

卡片整体可点击，通常用于进入详情页。

#### 3. 图片加载

```kotlin
AsyncImage(
    model = data.httpsEnvelopePic,
    contentDescription = null,
    modifier = Modifier.width(60.dp),
    contentScale = ContentScale.Crop
)
```

Compose 中最常见的图片加载方式之一。

#### 4. 协程和异步操作

```kotlin
scope.launch {
    val myRepo = WanRepositoryProvider.my
    val response = if (collected) {
        myRepo.uncollectArticle(data.id)
    } else {
        myRepo.collectArticle(data.id)
    }
    if (response.errorCode == "0") {
        collected = !collected
    }
}
```

说明 Compose 中也可以安全地启动协程去做网络请求。

### 6.4 简化版卡片 demo

```kotlin
@Composable
fun SimpleCardDemo() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Compose Card")
            Spacer(modifier = Modifier.height(8.dp))
            Text("这是一个简单的卡片示例")
        }
    }
}
```

---

## 7. 列表是怎么写的

列表是 Compose 学习中的重点，因为真实 App 几乎都离不开列表。

### 7.1 `LazyColumn`

最常见的纵向列表：

```kotlin
@Composable
fun ArticleListDemo(items: List<String>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { item ->
            Text(text = item)
        }
    }
}
```

### 7.2 列表项的状态

在真实项目里，列表项通常有：

- 点击状态
- 收藏状态
- 展开状态
- 选中状态

仓库里的 `ArticleCard` 就是一个典型例子。

### 7.3 列表页常见模式

```kotlin
@Composable
fun ListPage(
    viewModel: ListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        uiState.loading -> LoadingView()
        uiState.items.isEmpty() -> EmptyView()
        else -> LazyColumn {
            items(uiState.items) { item ->
                ItemCard(item)
            }
        }
    }
}
```

---

## 8. 图片组件怎么用

Compose 中经常需要显示网络图、头像、封面图。

### 8.1 仓库里的使用方式

在 `ArticleCard` 中可以看到：

```kotlin
AsyncImage(
    model = data.httpsEnvelopePic,
    contentDescription = null,
    modifier = Modifier
        .width(60.dp)
        .padding(start = 10.dp)
        .aspectRatio(2f / 3f),
    contentScale = ContentScale.Crop
)
```

这是 Coil 的 Compose 版本。

### 8.2 图片常用属性

- `model`：图片地址或资源
- `contentDescription`：无障碍描述
- `contentScale`：缩放裁剪方式
- `modifier`：控制尺寸、圆角、点击等

### 8.3 图片 demo

```kotlin
@Composable
fun AvatarDemo(url: String) {
    AsyncImage(
        model = url,
        contentDescription = "头像",
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}
```

---

## 9. 常见交互组件

### 9.1 `Button`

```kotlin
@Composable
fun ButtonDemo() {
    Button(onClick = { /* do something */ }) {
        Text("提交")
    }
}
```

### 9.2 `OutlinedButton`

```kotlin
@Composable
fun OutlinedButtonDemo() {
    OutlinedButton(onClick = { /* do something */ }) {
        Text("取消")
    }
}
```

### 9.3 `IconButton`

```kotlin
@Composable
fun IconButtonDemo(onClear: () -> Unit) {
    IconButton(onClick = onClear) {
        Icon(
            imageVector = Icons.Outlined.Clear,
            contentDescription = null
        )
    }
}
```

### 9.4 仓库里的收藏按钮思路

`ArticleCard` 底部使用点击图标来控制收藏状态，这种写法很适合学习“图标按钮 + 网络操作 + 本地状态切换”。

---

## 10. Compose 中的状态管理基础

### 10.1 `remember`

```kotlin
var text by remember { mutableStateOf("") }
```

作用是保存组件内部状态。

### 10.2 `mutableStateOf`

让变量变成 Compose 可观察状态。

### 10.3 `collectAsStateWithLifecycle`

在项目中，ViewModel 的 Flow 通常会被收集成 Compose 状态：

```kotlin
val uiState by viewModel.uiState.collectAsStateWithLifecycle()
```

这能让页面随着数据变化自动刷新。

### 10.4 状态提升

如果状态需要被多个组件共享，就应该放到更上层：

```kotlin
@Composable
fun Parent() {
    var value by remember { mutableStateOf("") }

    ChildInput(
        value = value,
        onValueChange = { value = it }
    )
}
```

这叫“状态提升”。

---

## 11. Compose 布局实战小结

你可以把布局能力记成下面这张表：

| 布局 | 作用 | 常见场景 |
|---|---|---|
| `Row` | 横向排列 | 顶栏、信息条、按钮组 |
| `Column` | 纵向排列 | 表单、卡片内容 |
| `Box` | 叠放和对齐 | 头像角标、文字覆盖图 |
| `Spacer` | 留白 | 间距控制 |
| `LazyColumn` | 纵向列表 | 文章流、消息流 |
| `LazyRow` | 横向列表 | 标签、横滑推荐 |

---

## 12. 一个完整的 Compose 页面 demo

下面给你一个更完整的综合示例，包含标题栏、输入框、按钮、列表和卡片。

```kotlin
@Composable
fun ComposeBasicsDemo() {
    var keyword by remember { mutableStateOf("") }
    val data = remember {
        listOf("Compose 入门", "Row 和 Column", "状态管理", "列表与卡片")
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TitleBar(title = "Compose 基础示例")

        WhiteTextField(
            value = keyword,
            onValueChange = { keyword = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("请输入关键词") }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(data.filter { it.contains(keyword, ignoreCase = true) }) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = item)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "这是一个 Compose 列表项")
                    }
                }
            }
        }
    }
}
```

这个 demo 把你前面学到的核心知识都串起来了：

- `Column` 页面结构
- 顶部栏 `TitleBar`
- 自定义输入框
- 列表过滤
- `LazyColumn`
- `Card`
- `remember` 状态

---

## 13. 在这个项目里学 Compose 的正确方式

建议你按这个顺序练习：

### 第一步：只看布局

先理解：

- `Row`
- `Column`
- `Box`
- `Spacer`
- `Modifier`

### 第二步：理解状态

再看：

- `remember`
- `mutableStateOf`
- `collectAsStateWithLifecycle`

### 第三步：看组件封装

然后分析：

- `TitleBar`
- `WhiteTextField`
- `ClearTextField`
- `ArticleCard`

### 第四步：看页面如何拼装

最后去看：

- `MainScreen`
- `HomeScreen`
- `SearchScreen`
- `LoginScreen`

这样你的学习路径会更清晰，不容易迷路。

---

## 14. 与当前仓库代码的对应关系

下面这些文件是你学习 Compose 时最值得对照阅读的：

- `app/src/main/java/com/example/fragment/project/components/TitleBar.kt`
- `app/src/main/java/com/example/fragment/project/components/SimpleTextField.kt`
- `app/src/main/java/com/example/fragment/project/components/ArticleCard.kt`
- `app/src/main/java/com/example/fragment/project/ui/main/MainScreen.kt`
- `app/src/main/java/com/example/fragment/project/ui/main/home/HomeScreen.kt`
- `app/src/main/java/com/example/fragment/project/ui/search/SearchScreen.kt`
- `app/src/main/java/com/example/fragment/project/ui/login/LoginScreen.kt`
- `app/src/main/java/com/example/fragment/project/ui/demo/*`

---

## 15. 最后总结

学习 Compose 最重要的不是背 API，而是理解它的思维方式：

- UI 是状态的函数
- 布局由组合组件完成
- 组件应该可复用、可参数化
- 页面只负责展示，逻辑交给 ViewModel
- 数据流最好保持单向

结合这个仓库去学，你会更容易从“会写几个组件”进阶到“能搭一个真正的 Compose 页面”。
