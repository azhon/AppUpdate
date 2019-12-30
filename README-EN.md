## [中文 文档](https://github.com/azhon/AppUpdate/blob/master/README.md)

<p align="center"><img src="https://github.com/azhon/AppUpdate/blob/master/img/logo.png"></p>
<p align="center">
  <img src="https://img.shields.io/badge/miniSdk-15%2B-blue.svg">
  <img src="https://img.shields.io/badge/jcenter%20version-2.7.0-brightgreen.svg">
  <img src="https://img.shields.io/badge/author-azhon-%23E066FF.svg">
  <img src="https://img.shields.io/badge/license-Apache2.0-orange.svg">
</p>

### [AppUpdate is collecting frame user information and hopes to get everyone's support](https://github.com/azhon/AppUpdate/issues/58)
### [Since Android Q version restricts background apps from launching Activity, a notification will be sent to the notification bar when the download is complete (ignoring the showNotification value, you need to allow notifications to be sent)](https://developer.android.google.cn/guide/components/activities/background-starts)
### [Since Android Q version restricts access to external storage，so removed setDownloadPath()](https://developer.android.google.cn/training/data-storage/files/external-scoped)

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

### [Demo download experience](https://github.com/azhon/AppUpdate/tree/master/apk/appupdate.apk)

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
| apkVersionCode | new apk versionCode <br>(If set, the version will be judged in the library,<br>The following properties also need to be set) | 1                     | false       |
| apkVersionName | new apk versionName                                                                                                          | null                  | false       |
| apkDescription | Update description                                                                                                           | null                  | false       |
| apkSize        | New version of the apk size (unit M)                                                                                         | null                  | false       |
| authorities    | Support Android N uri license                                                                                                | package Name          | false       |
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

### Steps for usage

#### Step1： `app/build.gradle` Dependent

```groovy
implementation 'com.azhon:appupdate:2.7.0'
```

- If you using `AndroidX`, please implementation `appupdateX`

```groovy
implementation 'com.azhon:appupdateX:2.7.0'
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

#### Step3：Compatible with Android N and above，Add the following code to your app's `Manifest.xml`

> The authorities value set in the provider must be the same as the authorities set in the DownloadManager (the application package name is not set)
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

- If you implementation the `appupdateX` version

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

#### Step4：Resource file `res/xml/file_paths_public.xml` content

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

#### Step5：ProGuard Rules

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

* v2.7.0（2019/12/30）

  * Upgrade dialog hides progress bar when download progress Max equals -1
  * When the download progress Max is equal to -1, the progress of the notification bar does not show the percentage
  * Remove custom download directory and `[storage permission]` 
  * Optimized network connection timeout time to 30 seconds

#### [More update records click here to view](https://github.com/azhon/AppUpdate/wiki/更新日志)

### End

* If you encounter problems during use, please feel free to ask Issues.
* If you have any good suggestions, you can also mention Issues or send  email to: azhon.cn@gmail.com.
