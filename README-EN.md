## [中文 文档](https://github.com/azhon/AppUpdate/blob/master/README.md)

## Written in front

[![Travis](https://img.shields.io/badge/miniSdk-15%2B-blue.svg)]()　[![Travis](https://img.shields.io/badge/jcenter-2.0.0-brightgreen.svg)]()　[![Travis](https://img.shields.io/badge/star-300+-%23EE2C2C.svg)]()　[![Travis](https://img.shields.io/badge/author-azhon-%23E066FF.svg)]()　[![Travis](https://img.shields.io/badge/license-Apache2.0-orange.svg)]()

![](https://github.com/azhon/AppUpdate/blob/master/img/logo.png)

#### The framework supports Chinese/English internally (other languages only need to take the same name in the corresponding `string.xml`)

#### Internal dialog background image, button support customization

#### To view the Log in the library, you only need to filter the `tag` at the beginning of `AppUpdate`

#### **Focus：** If `downloadPath` is empty, the default is `getExternalCacheDir()` directory, and the [storage] permission will not be applied!

## Table of Contents

* [Rendering](#rendering)
* [Function introduction](#function introduction)
* [DownloadManager](#downloadmanager configuration doc)
* [UpdateConfiguration](#updateconfiguration configuration Doc)
* [Steps for usage](#steps for usage)
* [Demo download experience](#demo download experience)
* [Version update record](#version update record)
* [Conclusion](#conclusion)

### Rendering

<img src="https://github.com/azhon/AppUpdate/blob/master/img/01.png" width="300">　<img src="https://github.com/azhon/AppUpdate/blob/master/img/02.png" width="300">
<img src="https://github.com/azhon/AppUpdate/blob/master/img/03.png" width="300">　<img src="https://github.com/azhon/AppUpdate/blob/master/img/04.png" width="300">
<img src="https://github.com/azhon/AppUpdate/blob/master/img/05.png" width="300">　<img src="https://github.com/azhon/AppUpdate/blob/master/img/06.png" width="300">

### Function introduction

* [x] Support breakpoint download
* [x] Support background download
* [x] Support for custom download process
* [x] Support Device >= Android M Dynamic Access Request
* [x] Support notification progress bar display (or custom display progress)
* [x] Support Android N
* [x] Support Android O
* [x] Support Chinese/English 
* [x] Support for custom built-in dialog styles
* [x] Support for canceling the download (if the notification bar message is sent, it will be removed)
* [x] Support download completion Delete old APK file after opening new version
* [x] Download using HttpURLConnection, no other third-party framework is integrated

### More detailed documentation see here[《AppUpdate API Doc》](http://azhon.github.io/AppUpdate/index.html)

### DownloadManager：Configuration Doc

> Initial use`DownloadManager.getInstance(this)`

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

### UpdateConfiguration：Configuration Doc

| 属性                    | 描述                    | 默认值    |
|:--------------------- |:--------------------- |:------ |
| notifyId              | 通知栏消息id               | 1011   |
| notificationChannel   | 适配Android O的渠道通知      | 详情查阅源码 |
| httpManager           | 设置自己的下载过程             | null   |
| breakpointDownload    | 是否需要支持断点下载            | true   |
| enableLog             | 是否需要日志输出              | true   |
| onDownloadListener    | 下载过程的回调               | null   |
| jumpInstallPage       | 下载完成是否自动弹出安装页面        | true   |
| showNotification      | 是否显示通知栏进度（后台下载提示）     | true   |
| forcedUpgrade         | 是否强制升级                | false  |
| onButtonClickListener | 按钮点击事件回调              | null   |
| dialogImage           | 对话框背景图片资源(图片规范参考demo) | -1     |
| dialogButtonColor     | 对话框按钮的颜色              | -1     |
| dialogButtonTextColor | 对话框按钮的文字颜色            | -1     |

#### All versions：[Click to view](https://dl.bintray.com/azhon/azhon/com/azhon/appupdate/)

### Steps for usage

#### Step1： `app/build.gradle` Dependent

```groovy
implementation 'com.azhon:appupdate:2.0.0'
```

#### Step2：Create `DownloadManager`，For more usage, please see [sample code here](https://github.com/azhon/AppUpdate/blob/master/app/src/main/java/com/azhon/app/MainActivity.java)

```java
DownloadManager manager = DownloadManager.getInstance(this);
manager.setApkName("appupdate.apk")
        .setApkUrl("https://raw.githubusercontent.com/azhon/AppUpdate/master/apk/appupdate.apk")
        .setSmallIcon(R.mipmap.ic_launcher)
        //可设置，可不设置
        .setConfiguration(configuration)
        .download();
```

#### Step3：Compatible with Android N and above，Add the following code to your app's `Manifest.xml`

```java
<--! android:authorities="${applicationId}" 
这个值必须与DownloadManager中的authorities一致（不设置则为应用包名）-->

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

#### Step4：Resource file `res/xml/file_paths_public.xml` content

```java
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

#### Step5：Cancel the download, cancel the download and continue downloading. Please refer to the `Step2` of the document

```java
private DownloadManager manager;
//取消下载
manager.cancel();
```

#### Download completed Delete old APK file after opening new version

```
//旧版本apk的文件保存地址
boolean b = ApkUtil.deleteOldApk(this, getExternalCacheDir().getPath() + "/appupdate.apk");
```

* Compatible with Android O and above, you need to set `NotificationChannel`; the library has been written to go to view[NotificationUtil.java](https://github.com/azhon/AppUpdate/blob/master/appupdate/src/main/java/com/azhon/appupdate/utils/NotificationUtil.java)
* Tips: The contents of the upgrade dialog can be swiped up and down！
* If you need to implement your own set of download process, you only need to `extends` `BaseHttpDownloadManager` and update the progress with listener.

```
public class MyDownload extends BaseHttpDownloadManager {}
```

### Demo download experience

 [Click to download Demo to experience](https://github.com/azhon/AppUpdate/tree/master/apk/appupdate.apk)

### Version update record

* v2.0.0
  * New installation completes the startup of the new version to delete the old APK file
  * Added cancel download function

#### [More update records click here to view](https://github.com/azhon/AppUpdate/wiki/更新日志)

### Conclusion

* If you encounter problems during use, please feel free to ask Issues.
