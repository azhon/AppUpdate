## [中文 文档](https://github.com/azhon/AppUpdate/blob/master/README.md)

<p align="center"><img src="https://github.com/azhon/AppUpdate/blob/master/img/logo.png"></p>
<p align="center">
  <img src="https://img.shields.io/badge/miniSdk-15%2B-blue.svg">
  <img src="https://img.shields.io/badge/jcenter%20version-3.0.0-brightgreen.svg">
  <img src="https://img.shields.io/badge/author-azhon-%23E066FF.svg">
  <img src="https://img.shields.io/badge/license-Apache2.0-orange.svg">
</p>

### Since version 3.0.0, you can use [AppUpdate log query](http://azhong.tk:8088/app/) to see which apps are in use, and you can also discuss issues together!
### [Since Android Q version restricts background apps from launching Activity, a notification will be sent to the notification bar when the download is complete (ignoring the showNotification value, you need to allow notifications to be sent)](https://developer.android.google.cn/guide/components/activities/background-starts)
### [Since Android Q version restricts access to external storage，so removed setDownloadPath()](https://developer.android.google.cn/training/data-storage/files/external-scoped)

## Table of Contents

* Rendering
* Function introduction
* Demo download experience
* DownloadManager
* UpdateConfiguration
* Use reminder
* Steps for usage
* Skills
* Version update record
* End

### Rendering

<img src="https://github.com/azhon/AppUpdate/blob/master/img/en/en_1.png" width="300">　<img src="https://github.com/azhon/AppUpdate/blob/master/img/en/en_2.png" width="300">
<img src="https://github.com/azhon/AppUpdate/blob/master/img/en/en_3.png" width="300">　<img src="https://github.com/azhon/AppUpdate/blob/master/img/en/en_4.png" width="300">
<img src="https://github.com/azhon/AppUpdate/blob/master/img/en/en_5.png" width="300">　<img src="https://github.com/azhon/AppUpdate/blob/master/img/en/en_6.png" width="300">

### Function introduction

* [x] Support AndroidX
* [x] Support for custom download process
* [x] Support Device >= Android M Dynamic Access Request
* [x] Support notification progress display (or custom display progress)
* [x] Support Android N
* [x] Support Android O
* [x] Support Android P
* [x] Support Android Q
* [x] Support Chinese/English 
* [x] Support for custom built-in dialog styles
* [x] Support for canceling the download (if the notification bar message is sent, it will be removed)
* [x] Support download completion Delete old APK file after opening new version
* [x] Download using HttpURLConnection, no other third-party framework is integrated

### [Demo download experience](https://github.com/azhon/AppUpdate/releases/download/2.8.0/appupdate.apk)

### DownloadManager：Configuration Doc

> Initial use`DownloadManager.getInstance(this)`

| Attributes     | Description                                                                                                                  | Default Value         | Must be set |
|:-------------- |:---------------------------------------------------------------------------------------------------------------------------- |:--------------------- |:----------- |
| context        | Context                                                                                                                      | null                  | true        |
| apkUrl         | Apk download Url                                                                                                             | null                  | true        |
| apkName        | Apk download  name                                                                                                           | null                  | true        |
| downloadPath   | apk download path(2.7.0 or higher is deprecated)                                                                             | getExternalCacheDir() | false       |
| showNewerToast | Whether to prompt the user<br> "currently the latest version" toast                                                          | false                 | false       |
| smallIcon      | Notification icon (resource id)                                                                                              | -1                    | true        |
| configuration  | Additional configuration of this library                                                                                     | null                  | false       |
| apkVersionCode | new apk versionCode <br>(If set, the version will be judged in the library,<br>The following properties also need to be set) | Integer.MIN_VALUE     | false       |
| apkVersionName | new apk versionName                                                                                                          | null                  | false       |
| apkDescription | Update description                                                                                                           | null                  | false       |
| apkSize        | New version of the apk size (unit M)                                                                                         | null                  | false       |
| apkMD5         | Md5 (32 bit) of the new apk                                                                                                  | null                  | false       |

### UpdateConfiguration：Configuration Doc

| Attributes            | Description                                                                             | Default Value              |
|:--------------------- |:--------------------------------------------------------------------------------------- |:-------------------------- |
| notifyId              | notification id                                                                         | 1011                       |
| notificationChannel   | Adapt to Android O  notifications                                                       | See the source for details |
| httpManager           | Set up your own download process                                                        | null                       |
| enableLog             | Whether need to log output                                                              | true                       |
| onDownloadListener    | Callback of the download process                                                        | null                       |
| jumpInstallPage       | Whether the download completes automatically<br> pops up the installation page          | true                       |
| showNotification      | Whether to display the progress of the<br> notification bar (background download toast) | true                       |
| forcedUpgrade         | Whether to force an upgrade                                                             | false                      |
| showBgdToast          | Whether need to “Downloading new version in the background…”                            | true                       |
| onButtonClickListener | Button click event listener                                                             | null                       |
| dialogImage           | Dialog background image resource<br> (picture specification reference demo)             | -1                         |
| dialogButtonColor     | The color of the dialog button                                                          | -1                         |
| dialogButtonTextColor | The text color of the dialog button                                                     | -1                         |
| dialogProgressBarColor | Dialog progress bar and text color                                                     | -1                         |

### Usage reminder

Because it will cooperate with the [AppUpdate log query](http://azhong.tk:8088/app/) platform to generate some network data, this information collection will never be used for any malicious purposes.

* Count how many Apps used AppUpdate</br>
HttpUtil#postUsage
* Report the download error log</br>
HttpUtil#postException

### Steps for usage

#### Step1： `app/build.gradle` Dependent

```groovy
implementation 'com.azhon:appupdate:3.0.0'
```

- If you using `AndroidX`, please implementation `appupdateX`

```groovy
implementation 'com.azhon:appupdateX:3.0.0'
```

#### Step2：Create `DownloadManager`，For more usage, please see [sample code here](https://github.com/azhon/AppUpdate/blob/master/app/src/main/java/com/azhon/app/MainActivity.java)

```java
DownloadManager manager = DownloadManager.getInstance(this);
manager.setApkName("appupdate.apk")
        .setApkUrl("https://raw.githubusercontent.com/azhon/AppUpdate/master/apk/appupdate.apk")
        .setSmallIcon(R.mipmap.ic_launcher)
        //Can be set, can not be set
        .setConfiguration(configuration)
        .download();
```

#### Step3：ProGuard Rules

```groovy
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
```

### Skills

* Internal support Chinese / English (other languages only need to take the same name in the corresponding `string.xml`
* To view the Log, you only need to filter the Tag at the beginning of `AppUpdate`
* Download completed Delete old APK file after opening new version

```java
//Old version apk file save path
boolean b = ApkUtil.deleteOldApk(this, getExternalCacheDir().getPath() + "/appupdate.apk");
```

* Tips: The contents of the upgrade dialog can be swiped up and down！
* If you need to implement your own set of download process, you only need to `extends` `BaseHttpDownloadManager` and update the progress with listener.

```java
public class MyDownload extends BaseHttpDownloadManager {}
```

### Version update record

* v3.0.0 (2020/06/05)

   * [Fix] Solve the problem that the high version cannot use http clear text network request
   * [New] Report error information to the server

#### [More update records click here to view](https://github.com/azhon/AppUpdate/wiki/更新日志)

### End

* If you encounter problems during use, please feel free to ask Issues.
* If you have any good suggestions, you can also mention Issues or send  email to: azhon.cn@gmail.com.
