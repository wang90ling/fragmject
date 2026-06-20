# fragmject 新手入门教程版：从 0 理解这个 Android Compose 项目

> 这份文档面向第一次接触这个仓库的同学，目标不是“罗列代码”，而是带你按学习路径真正看懂它：
> 1. 这个项目是什么；
> 2. 代码怎么组织；
> 3. App 从启动到页面渲染发生了什么；
> 4. Compose、ViewModel、Flow、Repository 是怎么配合的；
> 5. 你应该按什么顺序学习这些文件。

---

## 1. 先说结论：这个项目适合学什么

`fragmject` 是一个以 `Jetpack Compose` 为主 UI 技术栈的 Android 项目，包含了较完整的业务与基础设施：

- 单 Activity + Compose 页面体系
- `Navigation Compose` 路由
- `ViewModel + StateFlow/Flow` 状态管理
- 网络请求与缓存封装
- WebView 管理与预热
- 图片选择、裁剪、编辑能力
- 扫码、日历、拖拽、滚轮、动画等示例页面
- 多模块工程化拆分

所以它很适合用来学习：

- Kotlin 在 Android 中的实际写法
- Compose 页面如何组织状态和事件
- 业务代码如何分层
- 真正项目里怎么处理冷启动、内存、WebView、图片加载等问题

如果你是新手，最值得关注的不是“每个页面都能看懂”，而是先建立下面这张心智图：

```text
启动入口
  -> Application 做全局初始化
  -> Activity 承载 Compose
  -> NavGraph 负责路由
  -> Screen 负责 UI
  -> ViewModel 负责状态与事件
  -> Repository 负责数据
  -> Base/Library 模块提供通用能力
```

---

## 2. 这个项目由哪些模块组成

仓库是一个多模块 Android 工程，核心模块如下：

### 2.1 `app`

这是主应用模块，负责业务入口和主要页面。

常见内容包括：

- `WanApplication`：全局初始化
- `WanActivity`：应用入口 Activity
- `WanNavGraph`：Compose 导航
- `ui/main`、`ui/login`、`ui/search`、`ui/user`、`ui/demo` 等页面
- `ViewModel`、`Repository`、`database`、`data` 等业务层代码
- `components`：页面复用组件

### 2.2 `library-base`

基础能力库，通常放公共工具和基础组件：

- 网络基础设施
- 通用 Dialog / View / Adapter
- 工具类
- 资源文件
- JSON / HTML / JS 等静态资源

你可以把它理解成“所有模块都可能会用到的通用底座”。

### 2.3 `library-picture`

图片处理相关独立能力库：

- 图片选择器
- 图片预览
- 裁剪
- 编辑器
- 涂鸦、马赛克、贴纸、文本等图层能力

它的职责很明确：把图片能力从主业务里拆出来。

### 2.4 `library-plugin`

插件/编译期增强相关模块，包含字节码插桩、ASM 相关逻辑。

这类模块通常不是业务主线，但能帮助理解：

- Gradle 插件开发
- 字节码修改
- 构建期埋点/耗时统计

---

## 3. 项目启动流程：App 是怎么跑起来的

理解启动流程，是看懂整个项目最重要的一步。

### 3.1 入口类 `WanApplication`

文件：`app/src/main/java/com/example/fragment/project/WanApplication.kt`

它做了两件关键事：

1. 设置全局 base URL
2. 延迟初始化 `OkHttpClient`

你可以把它理解成：**应用刚启动时，只做必要的轻量初始化，避免主线程过重**。

文档中的核心代码可以参考：

```kotlin
override fun onCreate() {
    super.onCreate()
    setBaseUrl("https://www.wanandroid.com/")
    setHttpClientLazy { OkHelper.httpClient(applicationContext) }
}
```

这说明项目在启动阶段尽量控制成本，把真正昂贵的网络客户端创建延后到需要时再做。

### 3.2 入口 Activity `WanActivity`

文件：`app/src/main/java/com/example/fragment/project/WanActivity.kt`

它是应用真正显示 UI 的地方，负责：

- 安装启动页 SplashScreen
- 设置启动动画
- 启用 Edge-to-Edge
- 调用 `setContent` 进入 Compose
- 预热 WebView
- 调试模式下打开 WebView 调试能力

核心逻辑可以概括为：

```text
Activity onCreate
  -> SplashScreen
  -> enableEdgeToEdge
  -> setContent { WanTheme { WanNavGraph() } }
  -> WebViewManager.prepare()
  -> DebugBridge 控制调试开关
```

### 3.3 页面真正从哪里开始渲染

真正的 Compose UI 从这里开始：

```kotlin
setContent {
    WanTheme(window) {
        WanNavGraph()
    }
}
```

这个结构很典型：

- `WanTheme` 统一主题风格
- `WanNavGraph` 统一路由入口
- 具体页面由各个 `Screen` 负责

---

## 4. 你应该先看哪些文件

如果你是第一次读这个项目，建议按这个顺序：

### 第一步：先看入口

1. `app/src/main/AndroidManifest.xml`
2. `app/src/main/java/com/example/fragment/project/WanApplication.kt`
3. `app/src/main/java/com/example/fragment/project/WanActivity.kt`
4. `app/src/main/java/com/example/fragment/project/WanNavGraph.kt`
5. `app/src/main/java/com/example/fragment/project/WanTheme.kt`

### 第二步：看首页结构

1. `app/src/main/java/com/example/fragment/project/ui/main/MainScreen.kt`
2. `app/src/main/java/com/example/fragment/project/ui/main/MainViewModel.kt` 或相关 ViewModel
3. `app/src/main/java/com/example/fragment/project/components/*`

### 第三步：看一个完整业务闭环

推荐从这些页面入手：

- 登录 / 注册
- 首页文章列表
- 搜索
- Web 页面
- 用户信息页

因为这些页面通常能完整看到：

`UI -> ViewModel -> Repository -> Network/DB -> UI`

### 第四步：最后再看高级能力

- `library-picture`
- `library-plugin`
- `ui/demo` 下的复杂示例页

---

## 5. 多模块工程的思路

这个项目拆分模块的意义，不只是“分文件夹”，而是为了职责清晰。

### 5.1 为什么要分模块

分模块的好处：

- 主业务更清楚
- 通用能力可复用
- 编译和维护更友好
- 图片、插件等能力不污染主业务

### 5.2 你可以怎么理解每层职责

```text
app
  -> 真正的业务入口

library-base
  -> 通用底座，像工具箱

library-picture
  -> 单独的大功能域

library-plugin
  -> 构建期增强
```

### 5.3 新手要注意什么

如果你刚开始看代码，**不要试图一次看懂所有模块**。

更好的方式是：

- 先只看 `app`
- 再把 `library-base` 当作“被调用的工具库”
- `library-picture` 作为一个独立子系统
- `library-plugin` 最后再看

---

## 6. Compose 页面是怎么组织的

这个项目的 UI 基本遵循 Compose 常见写法。

### 6.1 常见结构

一个页面通常分成：

- `Screen`：页面入口
- `ViewModel`：管理状态和业务逻辑
- `components`：可复用 UI 组件
- `uiState`：页面状态

### 6.2 为什么 Compose 适合这样写

Compose 的核心优势是：

- UI 可以根据状态自动刷新
- 不需要手动操作大量 `View`
- 页面结构更接近“函数 + 状态”的思维方式

你可以把它理解成：

```text
State changed -> Composable recomposed -> UI 更新
```

### 6.3 页面常见模式

在这个项目里，很多页面都能看到类似模式：

```kotlin
@Composable
fun XxxScreen(viewModel: XxxViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // 根据 uiState 渲染页面
}
```

这意味着：

- 页面状态由 ViewModel 持有
- Compose 只负责展示
- 业务操作通过事件回传给 ViewModel

---

## 7. ViewModel、Flow、Repository 是怎么协作的

这是读懂项目的核心。

### 7.1 分层思想

项目里大量采用如下分层：

```text
Composable 层
  -> ViewModel
    -> Repository
      -> Network / Database / Cache
```

### 7.2 Repository 的作用

你可以把 Repository 理解成“数据提供者”。

在 `app/src/main/java/com/example/fragment/project/data/repository/Repositories.kt` 里，项目把能力拆成多个接口：

- `ArticleRepository`
- `ProjectRepository`
- `UserRepository`
- `MyRepository`
- `CommonRepository`

它们分别负责不同业务域。

例如 `ArticleRepository` 会处理：

- 首页文章
- 分类文章
- 搜索文章
- 收藏文章
- 带缓存的首页流

### 7.3 为什么要定义接口

接口的意义是：

- 方便替换实现
- 方便测试
- 降低耦合

也就是说，ViewModel 只需要知道“我要文章”，不需要关心“文章来自网络还是缓存”。

### 7.4 Flow 的作用

在这个项目里，`Flow` 常用于：

- 持续发射数据
- 表示缓存 + 网络更新
- 与 Compose 状态联动

当 Repository 暴露 `Flow<CachedResult<...>>` 时，通常意味着它支持一种“先显示缓存，再刷新网络”的思路。

---

## 8. 一个典型页面的完整数据流

假设你打开首页文章列表，大致过程是：

```text
1. Screen 进入组合
2. ViewModel 开始请求数据
3. Repository 发起网络或读取缓存
4. 数据回传到 uiState
5. Compose 根据 uiState 重新绘制
```

如果是分页加载，流程会更长一点：

```text
页面滑到底部
  -> 触发加载下一页
  -> ViewModel 调 Repository
  -> 新数据合并到列表
  -> 页面更新
```

你可以把这个模式理解成：

- UI 不直接请求接口
- UI 只发事件
- 数据处理集中在 ViewModel / Repository

---

## 9. 导航系统：页面之间怎么跳转

项目使用 `Navigation Compose` 来做路由。

### 9.1 典型理解方式

`WanNavGraph` 负责把所有页面串起来。

你可以把它看成“应用的地图”：

- 哪些页面存在
- 页面之间如何跳转
- 每个 route 对应哪个 Composable

### 9.2 为什么单 Activity 很常见

Compose 项目里通常推荐：

- 一个 Activity 承载整个应用
- 通过 Navigation 管理页面切换

这样做的好处：

- 页面切换更统一
- 状态保留更容易
- 生命周期更清晰

---

## 10. 主题、暗黑模式和 UI 风格

`WanTheme` 负责整个应用的视觉统一。

主题一般处理：

- 色彩
- 字体
- 圆角
- Material 体系
- 暗黑模式适配

### 10.1 你为什么要先看 Theme

如果不先看主题，读页面时很容易被各种颜色和样式干扰。

先看主题，你就会知道：

- 页面里的颜色是“写死的”还是“从主题来的”
- 暗色模式切换是否统一
- 全局样式是否有封装

### 10.2 新手建议

读 Compose 项目时，先搞清楚：

- `MaterialTheme` 的颜色从哪里来
- 页面是否使用了自定义组件
- 是否有统一间距、圆角、文字样式

---

## 11. 启动优化与性能意识

这个项目不是只关注“能跑”，还关注“跑得更快更稳”。

### 11.1 OkHttp 延迟初始化

`WanApplication` 里没有立即创建 `OkHttpClient`，而是通过 lazy 方式延后。

这能减少启动阶段压力。

### 11.2 WebView 预热

`WanActivity` 中会调用 `WebViewManager.prepare(applicationContext)`。

这样做通常是为了：

- 降低首次打开 Web 页的卡顿
- 让 WebView 相关资源提前准备好

### 11.3 内存压力处理

`WanApplication` 还实现了 `onTrimMemory` 和 `onLowMemory`。

这说明项目考虑了：

- 应用切后台后的内存回收
- 系统内存紧张时的降级策略
- WebView 资源池释放

这类代码对真实项目很有价值，因为很多 Android App 的问题都不是“功能错误”，而是“内存和启动体验问题”。

---

## 12. 常见目录怎么理解

下面是一些你读代码时会高频遇到的目录。

### 12.1 `components`

一般存放 Compose 可复用组件，比如：

- 按钮
- 标题栏
- 空状态
- 加载中
- 卡片

看组件时你要关注：

- 它是“页面专用”还是“全局可复用”
- 它接受了哪些参数
- 它是否被多个页面共用

### 12.2 `ui/demo`

这里通常是示例页面，适合学习单个 Compose 技术点：

- 动画
- 拖拽
- 日期选择
- 滚轮选择
- 媒体播放
- 扫码

这些页面很适合当作 Compose 小实验。

### 12.3 `data`

存放数据模型，例如：

- `Article`
- `Banner`
- `Coin`
- `Login`
- `Project`
- `User`

你看到这些类时，先不要纠结 UI，而是关注它们如何映射接口数据。

### 12.4 `database`

存放本地数据库相关：

- `AppDatabase`
- `Dao`
- 数据表

如果你想理解离线能力，就从这里看起。

---

## 13. 一个新手最容易忽略的点

### 13.1 不要先看“所有页面”

很多新手会从 `ui/demo` 开始乱看，结果越看越散。

更推荐的顺序是：

1. 启动入口
2. 主题
3. 导航
4. 一个完整业务页
5. 再看组件与工具

### 13.2 不要先纠结所有工具类

这个项目工具类很多，但大部分只是支撑。

你应该优先理解：

- 页面怎么显示
- 数据怎么流动
- 状态怎么更新

### 13.3 不要把 Compose 当成“新写法的 XML”

Compose 的思维方式更像：

- 先定义状态
- 再根据状态渲染 UI
- UI 是状态的结果

这和传统 View 的命令式写法差别很大。

---

## 14. 推荐学习路径

如果你的目标是“看懂并能改这个项目”，建议按下面路径学。

### 路径 A：最快入门

1. `WanActivity`
2. `WanTheme`
3. `WanNavGraph`
4. `MainScreen`
5. 任意一个 `ViewModel`
6. 对应 `Repository`

### 路径 B：完整掌握 Compose 应用结构

1. `Application` 启动流程
2. `Activity + setContent`
3. `Composable` 组织方式
4. `ViewModel` 状态管理
5. `Flow` 与状态收集
6. `Navigation Compose`
7. 网络和缓存
8. 图片/WebView/数据库等能力

### 路径 C：适合边学边做

你可以自己选一个页面，尝试做这些修改：

- 改标题
- 改主题色
- 加一个按钮
- 在列表里加一个字段
- 在 ViewModel 里加一个 loading 状态
- 给页面增加一个空状态

这样最容易把“看懂”变成“会用”。

---

## 15. 适合重点阅读的文件清单

### 启动与总入口

- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/com/example/fragment/project/WanApplication.kt`
- `app/src/main/java/com/example/fragment/project/WanActivity.kt`
- `app/src/main/java/com/example/fragment/project/WanNavGraph.kt`
- `app/src/main/java/com/example/fragment/project/WanTheme.kt`

### 架构与数据

- `app/src/main/java/com/example/fragment/project/data/repository/Repositories.kt`
- `app/src/main/java/com/example/fragment/project/data/repository/RepositoryImpl.kt`
- `app/src/main/java/com/example/fragment/project/data/repository/WanRepositoryProvider.kt`
- `app/src/main/java/com/example/fragment/project/data/repository/CachedFetch.kt`

### 首页与常用页面

- `app/src/main/java/com/example/fragment/project/ui/main/MainScreen.kt`
- `app/src/main/java/com/example/fragment/project/ui/main/home/HomeScreen.kt`
- `app/src/main/java/com/example/fragment/project/ui/main/nav/NavScreen.kt`
- `app/src/main/java/com/example/fragment/project/ui/main/project/ProjectScreen.kt`
- `app/src/main/java/com/example/fragment/project/ui/main/my/MyScreen.kt`

### 基础组件

- `app/src/main/java/com/example/fragment/project/components/*`

### 图片模块

- `library-picture/src/main/java/com/example/miaow/picture/selector/*`
- `library-picture/src/main/java/com/example/miaow/picture/editor/*`
- `library-picture/src/main/java/com/example/miaow/picture/clip/*`

### 基础库

- `library-base/src/main/java/com/example/miaow/base/http/*`
- `library-base/src/main/java/com/example/miaow/base/utils/*`
- `library-base/src/main/java/com/example/miaow/base/view/*`
- `library-base/src/main/java/com/example/miaow/base/dialog/*`

---

## 16. 版本和依赖文件怎么看

### 16.1 `gradle/libs.versions.toml`

这是版本目录文件，统一管理：

- Kotlin
- Compose
- Coroutines
- Room
- OkHttp
- Retrofit
- Coil
- Navigation
- Glance

它的好处是：

- 版本集中管理
- 升级方便
- 依赖声明更整洁

### 16.2 根目录 `build.gradle.kts`

这里通常声明：

- 插件但不实际应用
- 所有模块共享的构建配置

### 16.3 各模块 `build.gradle.kts`

模块自己的依赖、编译参数、Compose 开关、KSP、Room 等都在各自模块里配置。

---

## 17. 你读这份项目时应该建立的几个概念

### 17.1 UI 只负责展示

Compose 页面不应该承担太多业务处理。

### 17.2 ViewModel 承担状态与事件

页面想做什么，交给 ViewModel。

### 17.3 Repository 负责数据来源

网络、缓存、数据库，尽量收敛到 Repository。

### 17.4 基础模块做“共用能力”

不要把所有通用代码塞回 `app`。

### 17.5 先读主链路，再读周边能力

主链路：启动 -> 导航 -> 页面 -> ViewModel -> Repository。

---

## 18. 最后给新手的建议

如果你是第一次读这个项目，不要试图“全懂”。

最好的方式是：

1. 先看启动和首页
2. 再找一个业务页面完整走通
3. 然后回头看组件和工具
4. 最后再学图片模块、插件模块、复杂示例页

只要你能把这条链路理解清楚：

```text
Activity -> Theme -> NavGraph -> Screen -> ViewModel -> Repository
```

你就已经掌握了这个项目最重要的骨架。

---

## 19. 按文件逐个讲解：建议你从这些文件开始

下面这部分不是“把每个文件都讲一遍”，而是帮你挑出最值得优先读的关键文件，并告诉你读它们时该关注什么。

### 19.1 启动入口类

#### `app/src/main/java/com/example/fragment/project/WanApplication.kt`

这是应用级初始化入口。重点看：

- `setBaseUrl(...)`：全局网络地址从这里统一设置
- `setHttpClientLazy { ... }`：网络客户端延迟创建，减少冷启动开销
- `newImageLoader()`：图片加载器配置，包括 GIF / SVG / 视频帧解码
- `onTrimMemory(...)` 与 `onLowMemory()`：应用内存回收策略

你读这个文件时，要重点理解“为什么把重活延后做”，而不是只看语法。

#### `app/src/main/java/com/example/fragment/project/WanActivity.kt`

这是应用的 UI 容器。重点看：

- SplashScreen 退出动画
- `enableEdgeToEdge()` 的全屏适配
- `setContent { WanTheme { WanNavGraph() } }`
- `WebViewManager.prepare(...)` 的预热行为
- 调试开关 `DebugBridge.allowWebContentsDebugging`

这类文件的意义是：把整个 Compose 世界的入口搭起来。

### 19.2 路由和主题

#### `app/src/main/java/com/example/fragment/project/WanNavGraph.kt`

它定义了页面之间的跳转关系。建议你关注：

- route 是怎么命名的
- startDestination 是哪个页面
- 参数是怎么传递的
- 哪些页面属于主页面，哪些属于二级页面

#### `app/src/main/java/com/example/fragment/project/WanTheme.kt`

它定义整个 App 的视觉规范。重点看：

- 颜色体系
- 暗色模式判断
- 字体和排版
- 状态栏 / 导航栏配色

### 19.3 首页和主框架

#### `app/src/main/java/com/example/fragment/project/ui/main/MainScreen.kt`

主页面通常是这个应用最重要的壳。重点看：

- 底部导航如何组织
- 多页面如何切换
- 页面状态如何保存
- 子页面怎么组合进主壳

#### `app/src/main/java/com/example/fragment/project/ui/main/home/HomeScreen.kt`

首页往往是“状态管理”最集中、最能代表项目风格的地方。重点看：

- 列表状态怎么收集
- 加载中、空状态、错误状态如何切换
- 下拉刷新 / 上拉加载是否被封装

### 19.4 业务层：ViewModel / Repository / 数据

#### `app/src/main/java/com/example/fragment/project/data/repository/Repositories.kt`

这是理解项目架构的关键文件。重点看：

- `ArticleRepository`
- `ProjectRepository`
- `UserRepository`
- `MyRepository`
- `CommonRepository`

它们说明了项目如何按业务域拆分数据接口。

#### `app/src/main/java/com/example/fragment/project/data/repository/RepositoryImpl.kt`

这里通常是接口的真实实现。重点看：

- 数据从哪里来
- 网络请求如何发起
- 是否对结果做了统一转换
- 是否有缓存、重试或分页处理

#### `app/src/main/java/com/example/fragment/project/data/repository/WanRepositoryProvider.kt`

这里体现了项目如何“提供仓库对象”。新手重点理解：

- 为什么不直接在页面里 new Repository
- 为什么要集中提供实例
- 这样做对测试和替换实现有什么好处

#### `app/src/main/java/com/example/fragment/project/data/repository/CachedFetch.kt`

这个文件很适合学习缓存策略。重点看：

- cached result 是如何包装的
- 网络刷新和本地缓存如何配合
- Flow 如何发出缓存 + 最新数据

### 19.5 Compose 页面与组件

#### `app/src/main/java/com/example/fragment/project/components/*`

这里是最适合学习“如何写 Compose 组件”的目录。你可以关注：

- 组件是否足够通用
- 参数设计是否简洁
- 是否把状态提升到了外层
- 是否尽量做成无副作用组件

#### `app/src/main/java/com/example/fragment/project/ui/demo/*`

这些 demo 页面最适合学习单个技术点，比如：

- 动画
- 拖拽
- 日期选择
- 日历
- 滚轮
- ExoPlayer
- 扫码

建议你把这里当作“Compose 小实验室”。

### 19.6 基础库

#### `library-base/src/main/java/com/example/miaow/base/http/*`

这里是网络层的基础设施。重点看：

- 请求客户端如何创建
- 响应如何转换
- 是否有统一封装
- Token、Cookie、缓存等机制如何组织

#### `library-base/src/main/java/com/example/miaow/base/utils/*`

这里是典型的工具类集合。建议只在真正需要时再读，因为工具类数量多，容易打散注意力。

#### `library-base/src/main/java/com/example/miaow/base/view/*`

这里存放一些基础 UI 控件或自定义 View。新手如果只学 Compose，可以先略读；如果你想理解项目里旧 View 与 Compose 如何共存，再深入看。

### 19.7 图片模块

#### `library-picture/src/main/java/com/example/miaow/picture/selector/*`

负责图片选择器。

#### `library-picture/src/main/java/com/example/miaow/picture/editor/*`

负责图片编辑器。

#### `library-picture/src/main/java/com/example/miaow/picture/clip/*`

负责裁剪。

这个模块很适合学习“如何把一个复杂能力拆成独立子系统”。

### 19.8 插件模块

#### `library-plugin/src/main/kotlin/com/example/miaow/plugin/*`

如果你对 Gradle 插件、ASM、编译期插桩感兴趣，这里是入口。但它不是入门必须，建议最后再看。

---

## 20. 项目架构图：从启动到页面的一条主链路

下面这张结构图可以帮助你把整个项目串起来。

```text
┌──────────────────────────────────────────────┐
│                  Application                 │
│  WanApplication                              │
│  - 全局初始化                                 │
│  - 网络基地址                                 │
│  - ImageLoader                               │
│  - 内存回收策略                               │
└──────────────────────────────────────────────┘
                      │
                      ▼
┌──────────────────────────────────────────────┐
│                    Activity                   │
│  WanActivity                                 │
│  - SplashScreen                               │
│  - Edge-to-Edge                               │
│  - setContent                                 │
│  - WebView 预热                               │
└──────────────────────────────────────────────┘
                      │
                      ▼
┌──────────────────────────────────────────────┐
│                 Compose Root                  │
│  WanTheme -> WanNavGraph                      │
│  - 主题                                       │
│  - 路由                                       │
│  - 页面切换                                   │
└──────────────────────────────────────────────┘
                      │
          ┌───────────┼───────────┐
          ▼           ▼           ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│   Screen A    │ │   Screen B    │ │   Screen C    │
│  Compose UI   │ │  Compose UI   │ │  Compose UI   │
└──────────────┘ └──────────────┘ └──────────────┘
          │           │           │
          └───────────┼───────────┘
                      ▼
┌──────────────────────────────────────────────┐
│                  ViewModel                   │
│  - 状态管理                                   │
│  - 事件处理                                   │
│  - 协程 / Flow                                │
└──────────────────────────────────────────────┘
                      │
                      ▼
┌──────────────────────────────────────────────┐
│                 Repository                   │
│  - 数据聚合                                   │
│  - 缓存策略                                   │
│  - 网络请求                                   │
│  - 数据库                                     │
└──────────────────────────────────────────────┘
                      │
          ┌───────────┼───────────┐
          ▼           ▼           ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│   Network     │ │   Cache/DB    │ │  Library API  │
│  Retrofit/... │ │ Room/Flow     │ │ Picture/Web...│
└──────────────┘ └──────────────┘ └──────────────┘
```

### 20.1 你可以这样理解这张图

- `Application` 负责“全局准备工作”
- `Activity` 负责“页面容器”
- `Compose Root` 负责“主题和路由”
- `Screen` 负责“画 UI”
- `ViewModel` 负责“状态和事件”
- `Repository` 负责“数据来源和组装”
- 底层能力负责“真正的实现细节”

---

## 21. 学习路线图：建议的阅读顺序

如果你想最快看懂整个项目，建议按下面顺序走。

### 第 1 阶段：只看主链路

1. `AndroidManifest.xml`
2. `WanApplication.kt`
3. `WanActivity.kt`
4. `WanTheme.kt`
5. `WanNavGraph.kt`
6. `MainScreen.kt`

这一阶段的目标是：知道 App 是怎么启动和切页的。

### 第 2 阶段：看一个完整业务闭环

建议选择首页或登录页。

你要试着找到：

- 页面状态从哪里来
- ViewModel 做了什么
- Repository 调了什么
- 网络返回后 UI 怎么更新

### 第 3 阶段：补齐数据层理解

1. `Repositories.kt`
2. `RepositoryImpl.kt`
3. `WanRepositoryProvider.kt`
4. `CachedFetch.kt`

这一阶段的目标是：理解项目是怎么把数据抽象得比较干净的。

### 第 4 阶段：再看通用能力

- `library-base`
- `library-picture`
- `ui/demo`
- `library-plugin`

---

## 22. 总结

`fragmject` 不是一个只展示语法的 Compose Demo，而是一个把 **Compose、MVVM、Flow、Navigation、多模块、WebView、图片能力、缓存和性能优化** 都放在一起的真实工程样本；对于新手来说，按“启动入口 -> 页面 -> 状态 -> 数据 -> 底层能力”的顺序学习，收获最大。
