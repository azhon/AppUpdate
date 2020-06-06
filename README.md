## [English Doc](https://github.com/azhon/AppUpdate/blob/master/README-EN.md)

<p align="center"><img src="https://github.com/azhon/AppUpdate/blob/master/img/logo.png"></p>
<p align="center">
  <img src="https://img.shields.io/badge/miniSdk-15%2B-blue.svg">
  <img src="https://img.shields.io/badge/jcenter%20version-3.0.0-brightgreen.svg">
  <img src="https://img.shields.io/badge/author-azhon-%23E066FF.svg">
  <img src="https://img.shields.io/badge/license-Apache2.0-orange.svg">
</p>

### 自3.0.0版本开始可以使用[AppUpdate日志查询](http://azhong.tk:8088/app/)可以查看下载的异常信息，也可以查看哪些App正在使用，还可以一起来讨论问题哦！
### [由于Android Q版本限制后台应用启动Activity，所以下载完成会发送一个通知至通知栏（忽略showNotification的值，需要允许发送通知）](https://developer.android.google.cn/guide/components/activities/background-starts)
### [由于Android Q版本限制应用访问外部存储目录，所以移除了setDownloadPath()功能](https://developer.android.google.cn/training/data-storage/files/external-scoped)

## 扫码加入QQ群(群号：828385813)

<img
src="https://github.com/azhon/AppUpdate/blob/master/img/qq_group.png">

## 目录

* [效果图](#效果图)
* [功能介绍](#功能介绍)
* [Demo下载体验](#demo下载体验)
* [DownloadManager](#downloadmanager配置文档)
* [UpdateConfiguration](#updateconfiguration配置文档)
* [使用提醒](#使用提醒)
* [使用步骤](#使用步骤)
* [使用技巧](#使用技巧)
* [版本更新记录](#版本更新记录)

### 效果图

<img src="https://github.com/azhon/AppUpdate/blob/master/img/zh/zh_1.png" width="300">　<img src="https://github.com/azhon/AppUpdate/blob/master/img/zh/zh_2.png" width="300">
<img src="https://github.com/azhon/AppUpdate/blob/master/img/zh/zh_3.png" width="300">　<img src="https://github.com/azhon/AppUpdate/blob/master/img/zh/zh_4.png" width="300">
<img src="https://github.com/azhon/AppUpdate/blob/master/img/zh/zh_5.png" width="300">　<img src="https://github.com/azhon/AppUpdate/blob/master/img/zh/zh_6.png" width="300">

### 功能介绍

* [x] 支持AndroidX
* [x] 支持后台下载
* [x] 支持强制更新
* [x] 支持自定义下载过程
* [x] 支持 设备 >= Android M 动态权限的申请
* [x] 支持通知栏进度条展示(或者自定义显示进度)
* [x] 支持Android N
* [x] 支持Android O
* [x] 支持Android P
* [x] 支持Android Q
* [x] 支持中/英文双语（国际化）
* [x] 支持自定义内置对话框样式
* [x] 支持取消下载(如果发送了通知栏消息，则会移除)
* [x] 支持下载完成 打开新版本后删除旧安装包文件
* [x] 使用HttpURLConnection下载，未集成其他第三方框架

### Demo下载体验

 [点击下载Demo进行体验](https://github.com/azhon/AppUpdate/releases/download/2.8.0/appupdate.apk)

### DownloadManager：配置文档

> 初始化使用`DownloadManager.getInstance(this)`

| 属性             | 描述                                                                                    | 默认值                | 是否必须设置 |
|:-------------- |:----------------------------------------------------------------------------------------- |:--------------------- |:------------ |
| context        | 上下文                                                                                    | null                  | true         |
| apkUrl         | apk的下载地址                                                                             | null                  | true         |
| apkName        | apk下载好的名字                                                                           | null                  | true         |
| downloadPath   | apk下载的位置 (2.7.0以上版本已过时)                                                       | getExternalCacheDir() | false        |
| showNewerToast | 是否提示用户 "当前已是最新版本"                                                           | false                 | false        |
| smallIcon      | 通知栏的图标(资源id)                                                                      | -1                    | true         |
| configuration  | 这个库的额外配置                                                                          | null                  | false        |
| apkVersionCode | 更新apk的versionCode <br>(如果设置了那么库中将会进行版本判断<br>下面的属性也就需要设置了)           | Integer.MIN_VALUE     | false        |
| apkVersionName | 更新apk的versionName                                                                      | null                  | false        |
| apkDescription | 更新描述                                                                                  | null                  | false        |
| apkSize        | 新版本的安装包大小（单位M）                                                               | null                  | false        |
| apkMD5         | 新安装包的md5（32位)                                                                      | null                  | false        |

### UpdateConfiguration：配置文档

| 属性                  | 描述                                   | 默认值       |
|:--------------------- |:-------------------------------------- |:------       |
| notifyId              | 通知栏消息id                           | 1011         |
| notificationChannel   | 适配Android O的渠道通知                | 详情查阅源码 |
| httpManager           | 设置自己的下载过程                     | null         |
| enableLog             | 是否需要日志输出                       | true         |
| onDownloadListener    | 下载过程的回调                         | null         |
| jumpInstallPage       | 下载完成是否自动弹出安装页面           | true         |
| showNotification      | 是否显示通知栏进度（后台下载提示）     | true         |
| forcedUpgrade         | 是否强制升级                           | false        |
| showBgdToast          | 是否提示 "正在后台下载新版本…"        | true         |
| onButtonClickListener | 按钮点击事件回调                       | null         |
| dialogImage           | 对话框背景图片资源(图片规范参考demo)   | -1           |
| dialogButtonColor     | 对话框按钮的颜色                       | -1           |
| dialogButtonTextColor | 对话框按钮的文字颜色                   | -1           |
| dialogProgressBarColor| 对话框进度条和文字颜色                 | -1           |

### 使用提醒

因为目前会配合[AppUpdate日志查询](http://azhong.tk:8088/app/)平台会产生一些网络数据，这些信息收集绝不用于任何恶意用途。
如果你不想使用，可以下载源代码进行集成并将HttpUtil代码移除即可。

* 统计有多少App集成了AppUpdate</br>
HttpUtil#postUsage
* 上报下载的错误信息</br>
HttpUtil#postException

### 使用步骤

#### 第一步： `app/build.gradle`进行依赖

```groovy
implementation 'com.azhon:appupdate:3.0.0'
```

- 如果你使用的是`AndroidX`，请依赖`appupdateX`

```groovy
implementation 'com.azhon:appupdateX:3.0.0'
```

#### 第二步：创建`DownloadManager`，更多用法请查看[这里示例代码](https://github.com/azhon/AppUpdate/blob/master/app/src/main/java/com/azhon/app/MainActivity.java)

```java
DownloadManager manager = DownloadManager.getInstance(this);
manager.setApkName("appupdate.apk")
        .setApkUrl("https://raw.githubusercontent.com/azhon/AppUpdate/master/apk/appupdate.apk")
        .setSmallIcon(R.mipmap.ic_launcher)
        .download();
```
如果需要显示内置的对话框那么你需要调用`manager.setApkVersionCode()`将新版本的versionCode填进去

#### 第三步：混淆打包，只需保持`Activity`、`Service`不混淆

```groovy
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
```

### 使用技巧

* 框架内部支持中/英文 国际化（其他语言只需要在对应的`string.xml`中取相同的名字即可）
* 查看版本库中的Log只需要过滤`AppUpdate`开头的Tag
* 支持校验安装包的MD5避免重复下载，只需要`DownloadManager`设置安装包的MD5即可
* 下载完成 打开新版本后删除旧安装包文件，[实现思路请移步此处](https://github.com/azhon/AppUpdate/wiki/常见问题)

```java
//旧版本apk的文件保存地址
boolean b = ApkUtil.deleteOldApk(this, getExternalCacheDir().getPath() + "/appupdate.apk");
```

* 温馨提示：升级对话框中的内容是可以上下滑动的哦！
* 如果需要实现自己一套下载过程，只需要继承`BaseHttpDownloadManager` 并使用listener更新进度

```java
public class MyDownload extends BaseHttpDownloadManager {}
```

### 版本更新记录

* v3.0.0（2020/06/05）

  * [修复] 解决高版本不能使用http明文网络请求问题
  * [新增] 上报错误信息至服务器

* [更多更新记录点此查看](https://github.com/azhon/AppUpdate/wiki/更新日志)

### 赞赏
> 如果这个库有帮助到你并且你很想支持库的后续开发和维护，那么你可以扫描下方二维码随意打赏我，我将不胜感激[赞赏列表](https://github.com/azhon/AppUpdate/wiki/%E8%B5%9E%E8%B5%8F%E5%88%97%E8%A1%A8)


<img src="https://github.com/azhon/AppUpdate/blob/master/img/money.jpg" width="600"/>
