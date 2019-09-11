## [English Doc](https://github.com/azhon/AppUpdate/blob/master/README-EN.md)

<p align="center"><img src="https://github.com/azhon/AppUpdate/blob/master/img/logo.png"></p>
<p align="center">
  <img src="https://img.shields.io/badge/miniSdk-15%2B-blue.svg">
  <img src="https://img.shields.io/badge/jcenter%20version-2.4.0-brightgreen.svg">
  <img src="https://img.shields.io/badge/author-azhon-%23E066FF.svg">
  <img src="https://img.shields.io/badge/license-Apache2.0-orange.svg">
</p>

## [AppUpdate正在征集框架使用者信息，希望得到大家的支持](https://github.com/azhon/AppUpdate/issues/58)

## 目录

* [效果图](#效果图)
* [功能介绍](#功能介绍)
* [Demo下载体验](#demo下载体验)
* [DownloadManager](#downloadmanager配置文档)
* [UpdateConfiguration](#updateconfiguration配置文档)
* [使用步骤](#使用步骤)
* [使用技巧](#使用技巧)
* [版本更新记录](#版本更新记录)
* [哪些App正在使用](#哪些App正在使用)
* [结语](#结语)

### 效果图

<img src="https://github.com/azhon/AppUpdate/blob/master/img/zh/zh_1.png" width="300">　<img src="https://github.com/azhon/AppUpdate/blob/master/img/zh/zh_2.png" width="300">
<img src="https://github.com/azhon/AppUpdate/blob/master/img/zh/zh_3.png" width="300">　<img src="https://github.com/azhon/AppUpdate/blob/master/img/zh/zh_4.png" width="300">
<img src="https://github.com/azhon/AppUpdate/blob/master/img/zh/zh_5.png" width="300">　<img src="https://github.com/azhon/AppUpdate/blob/master/img/zh/zh_6.png" width="300">

### 功能介绍

* [x] 支持AndroidX
* [x] 支持后台下载
* [x] 支持自定义下载过程
* [x] 支持 设备 >= Android M 动态权限的申请
* [x] 支持通知栏进度条展示(或者自定义显示进度)
* [x] 支持Android N
* [x] 支持Android O
* [x] 支持Android P
* [x] 支持中/英文双语
* [x] 支持自定内置对话框的样式
* [x] 支持取消下载(如果发送了通知栏消息，则会移除)
* [x] 支持下载完成 打开新版本后删除旧安装包文件
* [x] 使用HttpURLConnection下载，未集成其他第三方框架

### Demo下载体验

 [点击下载Demo进行体验](https://github.com/azhon/AppUpdate/tree/master/apk/appupdate.apk)

### DownloadManager：配置文档

> 初始化使用`DownloadManager.getInstance(this)`

| 属性             | 描述                                                        | 默认值                   | 是否必须设置 |
|:-------------- |:--------------------------------------------------------- |:--------------------- |:------ |
| context        | 上下文                                                       | null                  | true   |
| apkUrl         | apk的下载地址                                                  | null                  | true   |
| apkName        | apk下载好的名字                                                 | null                  | true   |
| downloadPath   | apk下载的位置                                                  | getExternalCacheDir() | false  |
| showNewerToast | 是否提示用户 "当前已是最新版本"                                         | false                 | false  |
| smallIcon      | 通知栏的图标(资源id)                                              | -1                    | true   |
| configuration  | 这个库的额外配置                                                  | null                  | false  |
| apkVersionCode | 更新apk的versionCode <br>(如果设置了那么库中将会进行版本判断<br>下面的属性也就需要设置了) | 1                     | false  |
| apkVersionName | 更新apk的versionName                                         | null                  | false  |
| apkDescription | 更新描述                                                      | null                  | false  |
| apkSize        | 新版本的安装包大小（单位M）                                            | null                  | false  |
| authorities    | 兼容Android N uri授权                                         | 应用包名                  | false  |

### UpdateConfiguration：配置文档

| 属性                    | 描述                    | 默认值    |
|:--------------------- |:--------------------- |:------ |
| notifyId              | 通知栏消息id               | 1011   |
| notificationChannel   | 适配Android O的渠道通知      | 详情查阅源码 |
| httpManager           | 设置自己的下载过程             | null   |
| enableLog             | 是否需要日志输出              | true   |
| onDownloadListener    | 下载过程的回调               | null   |
| jumpInstallPage       | 下载完成是否自动弹出安装页面        | true   |
| showNotification      | 是否显示通知栏进度（后台下载提示）     | true   |
| forcedUpgrade         | 是否强制升级                | false  |
| showBgdToast          | 是否提示 "正在后台下载新版本…"     | true   |
| onButtonClickListener | 按钮点击事件回调              | null   |
| dialogImage           | 对话框背景图片资源(图片规范参考demo) | -1     |
| dialogButtonColor     | 对话框按钮的颜色              | -1     |
| dialogButtonTextColor | 对话框按钮的文字颜色            | -1     |

### 使用步骤

#### 第一步： `app/build.gradle`进行依赖

```groovy
implementation 'com.azhon:appupdate:2.4.0'
```

- 如果你使用的是`AndroidX`，请依赖`appupdateX`

```groovy
implementation 'com.azhon:appupdateX:2.4.0'
```

#### 第二步：创建`DownloadManager`，更多用法请查看[这里示例代码](https://github.com/azhon/AppUpdate/blob/master/app/src/main/java/com/azhon/app/MainActivity.java)

```java
DownloadManager manager = DownloadManager.getInstance(this);
manager.setApkName("appupdate.apk")
        .setApkUrl("https://raw.githubusercontent.com/azhon/AppUpdate/master/apk/appupdate.apk")
        .setSmallIcon(R.mipmap.ic_launcher)
        //可设置，可不设置
        .setConfiguration(configuration)
        .download();
```

#### 第三步：兼容Android N 及以上版本，在你应用的`Manifest.xml`添加如下代码

> provider中设置的authorities值必须与DownloadManager中设置的authorities一致（不设置则为应用包名）
> 
> android:authorities="${applicationId}"

```xml
<provider
    android:name="android.support.v4.content.FileProvider"
    android:authorities="${applicationId}"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths_public" />
</provider>
```

- 如果你引入的是`appupdateX`版本

  ```xml
  <provider
      android:name="androidx.core.content.FileProvider"
      android:authorities="${applicationId}"
      android:exported="false"
      android:grantUriPermissions="true">
      <meta-data
          android:name="android.support.FILE_PROVIDER_PATHS"
          android:resource="@xml/file_paths_public" />
  </provider>
  ```

#### 第四步：资源文件`res/xml/file_paths_public.xml`内容

```xml
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <external-path
        name="app_update_external"
        path="/" />
    <external-cache-path
        name="app_update_cache"
        path="/" />
</paths>
```

#### 第五步：混淆打包，只需保持`Activity`、`Service`不混淆

```groovy
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
```

### 使用技巧

* 框架内部支持中/英文（其他语言只需要在对应的`string.xml`中取相同的名字即可）
* 查看版本库中的Log只需要过滤`AppUpdate`开头的Tag
* 如果没有设置`downloadPath`则默认为`getExternalCacheDir()`目录同时不会申请[存储]权限！
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

* v2.4.0

  * 优化下载监听回调执行在主线程中
  * 解决强制更新状态下去安装，用户取消安装Dialog无法继续安装bug

* [更多更新记录点此查看](https://github.com/azhon/AppUpdate/wiki/更新日志)

### 哪些App正在使用

<table cellspacing="8" >
  <tr>
    <td></td>
    <td><a href="http://app.mi.com/details?id=com.vice.bloodpressure"><img src="http://file.market.xiaomi.com/thumbnail/PNG/l114/AppStore/00d2e4e2668ac84d7fb7d0ef130f15aec9d4202bc" width="100"></a></td>
    <td><a href="http://app.mi.com/details?id=com.xy.xydoctor"><img src="http://file.market.xiaomi.com/thumbnail/PNG/l114/AppStore/019b1555a24c734ce9c12843ef137edc01f43ec8e" width="100"></a></td>
  </tr>
    <tr>
    <th>易果无忧</th>
    <th>慧健康</th>
    <th>慧健康医生版</th>
  </tr>
</table>

### 结语

* 欢迎大家加入《通往Android的神奇之旅》群号码：555974449
* 如果大家在使用的过程中有什么问题，欢迎提Issues告知。
* 如果大家有什么好的建议或者需求，也可以提Issues或者发送邮件至：958460248@qq.com˛
