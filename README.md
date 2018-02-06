## 目录

* [编译问题](#编译问题)
* [效果图](#效果图)
* [功能介绍](#功能介绍)
* [DownloadManager](#downloadmanager配置文档)
* [UpdateConfiguration](#updateconfiguration配置文档)
* [使用步骤](#使用步骤)
* [Demo下载体验](#demo下载体验)
* [版本更新记录](#版本更新记录)
* [结语](#结语)

### 编译问题

* 因为适配了Android O的通知栏，所以依赖的v7包版本比较高`appcompat-v7:26.1.0`
* 使用的gradle版本为`gradle-4.1-all`，所以建议使用`Android Studio 3.0`及以上的版本打开此项目

### 效果图
<img src="http://img.blog.csdn.net/20180130212521314" width="300">　<img src="http://img.blog.csdn.net/20180130212630909" width="300">
<img src="http://img.blog.csdn.net/20180130212731751" width="300">　<img src="http://img.blog.csdn.net/20180130212801442" width="300">

### 功能介绍
* [x] 支持断点下载
* [x] 支持后台下载
* [x] 支持自定义下载过程
* [x] 支持 设备 >= Android M 动态权限的申请
* [x] 支持通知栏进度条展示(或者自定义显示进度)
* [x] 支持Android N
* [x] 支持Android O


### DownloadManager：配置文档
> 初始化使用`DownloadManager.getInstance(this)`

属性      | 描述		| 默认值  | 是否必须设置
:-------- | :-------- | :--------  | :--------
context | 上下文  | null | true
apkUrl | apk的下载地址 | null | true
apkName | apk下载好的名字 | null | true
downloadPath | apk下载的位置 | null | true
showNewerToast | 是否提示用户 "当前已是最新版本" | false | false
smallIcon | 通知栏的图标(资源id)  | -1 |  true
configuration | 这个库的额外配置 | null |  false
apkVersionCode | 更新apk的versionCode <br>(如果设置了那么库中将会进行版本判断<br>下面的属性也就需要设置了)  | 1 | false
apkVersionName | 更新apk的versionName  | null | false
apkDescription | 更新描述  | null | false
apkSize | 新版本的安装包大小（单位M）  | null | false

### UpdateConfiguration：配置文档
属性      | 描述		| 默认值
:-------- | :-------- | :--------
notifyId | 通知栏消息id  | 1011
notificationChannel | 适配Android O的渠道通知 | 详情查阅源码
httpManager | 设置自己的下载过程  | null
breakpointDownload | 是否需要支持断点下载  | true
enableLog | 是否需要日志输出  | true
onDownloadListener | 下载过程的回调  | null
jumpInstallPage | 下载完成是否自动弹出安装页面  | true
showNotification | 是否显示通知栏进度（后台下载提示）  | true

### 使用步骤
* `build.gradle`进行依赖

	```
	implementation 'com.azhon:appupdate:1.2.0'
	```

* 简单用法：创建`DownloadManager`

```
DownloadManager manager = DownloadManager.getInstance(this);
manager.setApkName("appupdate.apk")
        .setApkUrl("https://raw.githubusercontent.com/azhon/AppUpdate/master/apk/appupdate.apk")
        .setDownloadPath(Environment.getExternalStorageDirectory() + "/AppUpdate")
        .setSmallIcon(R.mipmap.ic_launcher)
        //可设置，可不设置
        .setConfiguration(configuration)
        .download();
```
* 兼容Android N 及以上版本，在你应用的`Manifest.xml`添加如下代码

```
<provider
    android:name="android.support.v4.content.FileProvider"
    <!--这个不用改-->
    android:authorities="${applicationId}"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths_public" />
</provider>
```
* 资源文件`res/xml/file_paths_public.xml`内容

```
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <external-path
        name="app_update"
        <!--这里的 AppUpdate 要与你设置的下载目录一致-->
        path="AppUpdate" />
</paths>
```
* 兼容Android O及以上版本，需要设置`NotificationChannel(通知渠道)`；库中已经写好可以前往查阅[NotificationUtil.java](https://github.com/azhon/AppUpdate/blob/master/appupdate/src/main/java/com/azhon/appupdate/utils/NotificationUtil.java)
* 温馨提示：升级对话框中的内容是可以上下滑动的哦😄！
* 如果需要实现自己一套下载过程，只需要继承`BaseHttpDownloadManager` 并使用listener更新进度

```
public class MyDownload extends BaseHttpDownloadManager {}
```

### Demo下载体验
 [点击下载Demo进行体验](https://github.com/azhon/AppUpdate/tree/master/apk/appupdate.apk)

### 版本更新记录

* v1.2.0
	* 添加是否提示用户 "当前已是最新版本" 的布尔值 
	* 修复部分手机请求权限时抛出：`Only fullscreen opaque activities can request orientation`
	
* v1.3.0
	* 修复大于Android O设备更新进度手机一直震动问题
	
	```
	NotificationChannel channel = new NotificationChannel("id",importance)
	//在Android O 上更新进度 不震动
	channel.enableVibration(false);
	```	
### 结语
* 如果大家在使用的过程中有什么问题，欢迎提Issuse告知。
* 如果大家有什么好的建议或者需求，也可以提Issuse或者发送邮件至：958460248@qq.com
