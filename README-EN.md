## [中文 文档](https://github.com/azhon/AppUpdate/blob/main/README.md)

<p align="center"><img src="https://github.com/azhon/AppUpdate/blob/main/img/logo.png"></p>
<p align="center">
  <img src="https://img.shields.io/badge/miniSdk-16%2B-blue.svg">
  <img src="https://img.shields.io/badge/mavenCentral-4.2.3-brightgreen.svg">
  <img src="https://img.shields.io/badge/author-azhon-%23E066FF.svg">
  <img src="https://img.shields.io/badge/license-Apache2.0-orange.svg">
</p>

### [Since Android Q version restricts background apps from launching Activity, a notification will be sent to the notification bar when the download is complete (ignoring the showNotification value, you need to allow notifications to be sent)](https://developer.android.google.cn/guide/components/activities/background-starts)

## Table of Contents

* Rendering
* Function introduction
* Demo download experience
* Steps for usage
* Skills
* Version update record
* End

### Rendering

<img src="https://github.com/azhon/AppUpdate/blob/main/img/en/en_1.png" width="300">　<img src="https://github.com/azhon/AppUpdate/blob/main/img/en/en_2.png" width="300">
<img src="https://github.com/azhon/AppUpdate/blob/main/img/en/en_3.png" width="300">　<img src="https://github.com/azhon/AppUpdate/blob/main/img/en/en_4.png" width="300">
<img src="https://github.com/azhon/AppUpdate/blob/main/img/en/en_5.png" width="300">　<img src="https://github.com/azhon/AppUpdate/blob/main/img/en/en_6.png" width="300">

### Function introduction

* [x] Support Kotlin
* [x] Support AndroidX
* [x] Support for custom download process
* [x] Support Android 4.1 and above
* [x] Support notification progress display (or custom display progress)
* [x] Support Chinese/Traditional/English 
* [x] Support for custom built-in dialog styles
* [x] Support for canceling the download (if the notification bar message is sent, it will be removed)
* [x] Support download completion Delete old APK file after opening new version
* [x] Download using HttpURLConnection, no other third-party framework is integrated

### [Demo download experience](https://github.com/azhon/AppUpdate/releases/tag/4.2.0)

### Steps for usage

#### Step1：`app/build.gradle`

```groovy
implementation 'io.github.azhon:appupdate:4.2.3'
```
<details>
<summary>Since in-app updates are prohibited by GooglePlay policy, it can be handled in productFlavors</summary>

- [GooglePlay policy](https://support.google.com/googleplay/android-developer/answer/9888379?hl=en&ref_topic=9877467)
- Library provides a version without no operation[Click see more](https://github.com/azhon/AppUpdate/blob/main/app/build.gradle)
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

#### Step2：Create `DownloadManager`，For more usage, please see [sample code here](https://github.com/azhon/AppUpdate/blob/main/app/src/main/java/com/azhon/app/MainActivity.kt)

```java
val manager = DownloadManager.Builder(this).run {
    apkUrl("your apk url")
    apkName("appupdate.apk")
    smallIcon(R.mipmap.ic_launcher)
    //If this parameter is set, it will automatically determine whether to show tip dialog
    apkVersionCode(2)
    apkVersionName('v4.2.2')
    apkSize("7.7MB")
    apkDescription("description...")
    //Optional parameters...
    build()
}
manager?.download()
```

#### Step3：ProGuard Rules

```groovy
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
```

### Skills

* Internal support (other languages only need to take the same name in the corresponding `string.xml`
* To view the Log, you only need to filter the Tag at the beginning of `AppUpdate`
* Download completed Delete old APK file after opening new version

```java
//Old version apk file save path
val result = ApkUtil.deleteOldApk(this, "${externalCacheDir?.path}/appupdate.apk")
```

* Tips: The contents of the upgrade dialog can be swiped up and down！
* If you need to implement your own set of download process, you only need to `extends` `BaseHttpDownloadManager`.

```java
class MyDownload : BaseHttpDownloadManager() {}
```

### Version update record

* v4.2.3（2022/09/13）

  * [opt] Migrating from jitpack to mavenCentral
  * [opt] Provides a version with no operations for use in specific situations

#### [More update records click here to view](https://github.com/azhon/AppUpdate/wiki/Home)

### End

* If you encounter problems during use, please feel free to ask Issues.
* If you have any good suggestions, you can also mention Issues or send  email to: azhon.cn@gmail.com.
