## [English Doc](https://github.com/azhon/AppUpdate/blob/main/README-EN.md)

<p align="center"><img src="https://github.com/azhon/AppUpdate/blob/main/img/logo.png"></p>
<p align="center">
  <img src="https://img.shields.io/badge/miniSdk-16%2B-blue.svg">
  <img src="https://img.shields.io/badge/mavenCentral-4.2.10-brightgreen.svg">
  <img src="https://img.shields.io/badge/author-azhon-%23E066FF.svg">
  <img src="https://img.shields.io/badge/license-Apache2.0-orange.svg">
</p>

## 本库核心逻辑：
#### 1、当调用了`apkVersionCode()`设置了最新的VersionCode，则内部会自动判断是否显示对话框、下载和安装
- 适配Android 13通知运行权限，且当设置`showNotification(true)`时，点击对话框的升级按钮会申请通知栏权限，无论是否同意都将会继续下载
#### 2、当没有调用`apkVersionCode()`，就可以把它当成一个下载器，内部只会进行下载和安装
#### 3、由于Android 10限制后台应用启动Activity，所以下载完成会发送一个通知至通知栏（忽略showNotification的值，需要允许发送通知）
#### 4、[更多用法请点此查看](https://github.com/azhon/AppUpdate/blob/main/app/src/main/java/com/azhon/app/MainActivity.kt#L79)

### 相关文档链接
- [限制后台启动Activity](https://developer.android.google.cn/guide/components/activities/background-starts)
- [通知栏适配](https://developer.android.google.cn/guide/topics/ui/notifiers/notifications?hl=zh-cn)

## 扫码加入QQ群(群号：828385813)
- 或者添加加微信：hb958460248（备注 AppUpdate）

<img
src="https://github.com/azhon/AppUpdate/blob/main/img/qq_group.png">

## 目录

* [效果图](#效果图)
* [功能介绍](#功能介绍)
* [Demo下载体验](#demo下载体验)
* [使用步骤](#使用步骤)
* [使用技巧](#使用技巧)
* [版本更新记录](#版本更新记录)

### 效果图

<img src="https://github.com/azhon/AppUpdate/blob/main/img/zh/zh_1.png" width="300">　<img src="https://github.com/azhon/AppUpdate/blob/main/img/zh/zh_2.png" width="300">
<img src="https://github.com/azhon/AppUpdate/blob/main/img/zh/zh_3.png" width="300">　<img src="https://github.com/azhon/AppUpdate/blob/main/img/zh/zh_4.png" width="300">
<img src="https://github.com/azhon/AppUpdate/blob/main/img/zh/zh_5.png" width="300">　<img src="https://github.com/azhon/AppUpdate/blob/main/img/zh/zh_6.png" width="300">
<img src="https://github.com/azhon/AppUpdate/blob/main/img/zh/zh_7.png" width="300">

### 功能介绍

* [x] 支持Java、Kotlin
* [x] 支持AndroidX
* [x] 支持后台下载
* [x] 支持强制更新
* [x] 支持自定义下载过程
* [x] 支持Android4.1及以上版本
* [x] 支持通知栏进度条展示，适配到Android 13
* [x] 支持中文/繁体/英文语言（国际化）
* [x] 支持自定义内置对话框样式
* [x] 支持取消下载(如果发送了通知栏消息，则会移除)
* [x] 支持下载完成，打开新版本后删除旧安装包文件
* [x] 不需要申请存储权限
* [x] 使用HttpURLConnection下载，未集成其他第三方框架

### Demo下载体验

 [点击下载Demo进行体验](https://github.com/azhon/AppUpdate/releases/tag/demo)


### 使用步骤

#### 第一步：`app/build.gradle`添加依赖

```groovy
implementation 'io.github.azhon:appupdate:4.2.10'
```

<details>
<summary>由于GooglePlay政策禁止应用内更新，所以可以通过多渠道的方式进处理</summary>

- [GooglePlay政策](https://support.google.com/googleplay/android-developer/answer/9888379?hl=en&ref_topic=9877467)
- 本库提供了一个没有任何实现的版本[点击查看详细内容](https://github.com/azhon/AppUpdate/blob/main/app/build.gradle)
```groovy
android {
    //...
    productFlavors {
        other {}
        googlePlay {}
    }
}

dependencies {
    otherImplementation 'io.github.azhon:appupdate:latest-version'
    googlePlayImplementation 'io.github.azhon:appupdate-no-op:latest-version'
}
```
</details>

#### 第二步：创建`DownloadManager`

```java
val manager = DownloadManager.Builder(this).run {
    apkUrl("your apk url")
    apkName("appupdate.apk")
    smallIcon(R.mipmap.ic_launcher)
    //设置了此参数，那么内部会自动判断是否需要显示更新对话框，否则需要自己判断是否需要更新
    apkVersionCode(2)
    //同时下面三个参数也必须要设置
    apkVersionName('v4.2.2')
    apkSize("7.7MB")
    apkDescription("更新描述信息(取服务端返回数据)")
    //省略一些非必须参数...
    build()
}
manager?.download()
```
#### 第三步：混淆打包，只需保持`Activity`、`Service`不混淆

```groovy
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
```

### 使用技巧

* 框架内部支持国际化（其他语言只需要在对应的`string.xml`中取相同的名字即可）
* 如果你需要修改框架内部的一些文字，你只需要在`string.xml`中取相同的名字即可以覆盖框架内设定的
* 查看版本库中的Log只需要过滤`AppUpdate`开头的Tag
* 支持校验安装包的MD5避免重复下载，只需要`Builder`设置安装包的MD5即可
* 下载完成 打开新版本后删除旧安装包文件

```java
//旧版本apk的文件保存地址
val result = ApkUtil.deleteOldApk(this, "${externalCacheDir?.path}/appupdate.apk")
```

* 温馨提示：升级对话框中的内容是可以上下滑动的哦！
* 如果需要实现自己一套下载过程，只需要继承`BaseHttpDownloadManager`

```java
class MyDownload : BaseHttpDownloadManager() {}
```

### 版本更新记录

* v4.2.10（2023/07/18）

  * [优化] [合并PR](https://github.com/azhon/AppUpdate/pull/154)

* [更多更新记录点此查看](https://github.com/azhon/AppUpdate/wiki/Home)

### 赞赏
> 如果这个库有帮助到你并且你很想支持库的后续开发和维护，那么你可以扫描下方二维码随意打赏我，我将不胜感激[赞赏列表](https://github.com/azhon/AppUpdate/wiki/%E8%B5%9E%E8%B5%8F%E5%88%97%E8%A1%A8)


<img src="https://github.com/azhon/AppUpdate/blob/main/img/money.jpg" width="600"/>
