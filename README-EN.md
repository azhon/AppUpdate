## [中文 文档](https://github.com/azhon/AppUpdate/blob/master/README.md)

## Written in front

[![Travis](https://img.shields.io/badge/miniSdk-15%2B-blue.svg)]()　[![Travis](https://img.shields.io/badge/jcenter-2.0.0-brightgreen.svg)]()　[![Travis](https://img.shields.io/badge/star-300+-%23EE2C2C.svg)]()　[![Travis](https://img.shields.io/badge/author-azhon-%23E066FF.svg)]()　[![Travis](https://img.shields.io/badge/license-Apache2.0-orange.svg)]()

![](https://github.com/azhon/AppUpdate/blob/master/img/logo.png)

#### The framework supports Chinese/English internally (other languages only need to take the same name in the corresponding `string.xml`)

#### Internal dialog background image, button support customization

#### To view the Log in the library, you only need to filter the `tag` at the beginning of `AppUpdate`

#### **Focus：** If `downloadPath` is empty, the default is `getExternalCacheDir()` directory, and the [storage] permission will not be applied!

## Table of Contents

* Rendering
* Function introduction]
* DownloadManager
* UpdateConfiguration
* Steps for usage
* Demo download experience
* Version update record
* Conclusion

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

| Attributes     | Description                                                                                                                  | Default Value         | Must be set |
|:-------------- |:---------------------------------------------------------------------------------------------------------------------------- |:--------------------- |:----------- |
| context        | Context                                                                                                                      | null                  | true        |
| apkUrl         | Apk download Url                                                                                                             | null                  | true        |
| apkName        | Apk download  name                                                                                                           | null                  | true        |
| downloadPath   | apk download path                                                                                                            | getExternalCacheDir() | false       |
| showNewerToast | Whether to prompt the user<br> "currently the latest version" toast                                                          | false                 | false       |
| smallIcon      | Notification icon (resource id)                                                                                              | -1                    | true        |
| configuration  | Additional configuration of this library                                                                                     | null                  | false       |
| apkVersionCode | new apk versionCode <br>(If set, the version will be judged in the library,<br>The following properties also need to be set) | 1                     | false       |
| apkVersionName | new apk versionName                                                                                                          | null                  | false       |
| apkDescription | Update description                                                                                                           | null                  | false       |
| apkSize        | New version of the apk size (unit M)                                                                                         | null                  | false       |
| authorities    | Support Android N uri license                                                                                                | package Name          | false       |

### UpdateConfiguration：Configuration Doc

| Attributes            | Description                                                                         | Default Value |
|:--------------------- |:----------------------------------------------------------------------------------- |:------------- |
| notifyId              | notification id                                                                     | 1011          |
| notificationChannel   | Adapt to Android O  notifications                                                   | 详情查阅源码        |
| httpManager           | Set up your own download process                                                    | null          |
| breakpointDownload    | Whether need to support breakpoint downloads                                        | true          |
| enableLog             | Whether need to log output                                                          | true          |
| onDownloadListener    | Callback of the download process                                                    | null          |
| jumpInstallPage       | Whether the download completes automatically pops up the installation page          | true          |
| showNotification      | Whether to display the progress of the notification bar (background download toast) | true          |
| forcedUpgrade         | Whether to force an upgrade                                                         | false         |
| onButtonClickListener | Button click event listener                                                         | null          |
| dialogImage           | Dialog background image resource (picture specification reference demo)             | -1            |
| dialogButtonColor     | The color of the dialog button                                                      | -1            |
| dialogButtonTextColor | The text color of the dialog button                                                 | -1            |

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
