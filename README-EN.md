## [中文 文档](https://github.com/azhon/AppUpdate/blob/main/README.md)

<p align="center"><img src="https://github.com/azhon/AppUpdate/blob/main/img/logo.png"></p>
<p align="center">
  <img src="https://img.shields.io/badge/miniSdk-15%2B-blue.svg">
  <img src="https://img.shields.io/badge/jitpack%20version-3.0.5-brightgreen.svg">
  <img src="https://img.shields.io/badge/author-azhon-%23E066FF.svg">
  <img src="https://img.shields.io/badge/license-Apache2.0-orange.svg">
</p>

### [Since Android Q version restricts background apps from launching Activity, a notification will be sent to the notification bar when the download is complete (ignoring the showNotification value, you need to allow notifications to be sent)](https://developer.android.google.cn/guide/components/activities/background-starts)

## Table of Contents

* Rendering
* Function introduction
* Demo download experience
* DownloadManager
* UpdateConfiguration
* Steps for usage
* Skills
* Version update record
* End

### Rendering

<img src="https://github.com/azhon/AppUpdate/blob/main/img/en/en_1.png" width="300">　<img src="https://github.com/azhon/AppUpdate/blob/main/img/en/en_2.png" width="300">
<img src="https://github.com/azhon/AppUpdate/blob/main/img/en/en_3.png" width="300">　<img src="https://github.com/azhon/AppUpdate/blob/main/img/en/en_4.png" width="300">
<img src="https://github.com/azhon/AppUpdate/blob/main/img/en/en_5.png" width="300">　<img src="https://github.com/azhon/AppUpdate/blob/main/img/en/en_6.png" width="300">

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

### [Demo download experience](https://github.com/azhon/AppUpdate/releases/download/3.0.1/appupdate.apk)

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
| usePlatform           | Whether to use AppUpdate website                                                        | true                       |
| onButtonClickListener | Button click event listener                                                             | null                       |
| dialogImage           | Dialog background image resource<br> (picture specification reference demo)             | -1                         |
| dialogButtonColor     | The color of the dialog button                                                          | -1                         |
| dialogButtonTextColor | The text color of the dialog button                                                     | -1                         |
| dialogProgressBarColor | Dialog progress bar and text color                                                     | -1                         |

### Steps for usage

#### Step1：

- `root/build.gradle`

```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

- `app/build.gradle`

```groovy
implementation 'com.github.azhon:AppUpdate:3.0.5'
```

#### Step2：Create `DownloadManager`，For more usage, please see [sample code here](https://github.com/azhon/AppUpdate/blob/main/app/src/main/java/com/azhon/app/MainActivity.java)

```java
UpdateConfiguration configuration = new UpdateConfiguration()

DownloadManager manager = DownloadManager.getInstance(this);
manager.setApkName("appupdate.apk")
        .setApkUrl("https://raw.githubusercontent.com/azhon/AppUpdate/main/apk/appupdate.apk")
        .setSmallIcon(R.mipmap.ic_launcher)
        //Optional parameters
        .setConfiguration(configuration)
        //If this parameter is set, it will automatically determine whether to show tip dialog
        .setApkVersionCode(2)
        .setApkDescription("description...")
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

* v3.0.5 (2021/08/15)

  *  [New] Optimize download thread pool

#### [More update records click here to view](https://github.com/azhon/AppUpdate/wiki/更新日志)

### End

* If you encounter problems during use, please feel free to ask Issues.
* If you have any good suggestions, you can also mention Issues or send  email to: azhon.cn@gmail.com.
