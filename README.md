## 目录

* [编译问题](#依赖库版本问题)
* [功能介绍](#功能介绍)
* [DownloadManager](#downloadmanager配置文档)
* [UpdateConfiguration](#updateconfiguration配置文档)
* [使用步骤](#使用步骤)
* [结语](#结语)

### 编译问题

> 因为适配了Android O的通知栏，所以依赖的v7包版本比较高`appcompat-v7:26.1.0`

> 使用的gradle版本为`gradle-4.1-all`，所以建议使用`Android Studio 3.0`以上的版本打开此项目


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
smallIcon | 通知栏的图标(资源id)  | -1 |  true
configuration | 这个库的额外配置 | null |  false
apkVersionCode | 更新apk的versionCode <br>(如果设置了那么库中将会进行版本判断)  | 1 | false

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
* 简单用法：创建`DownloadManager`

```
DownloadManager manager = DownloadManager.getInstance(this);
manager.setApkName("QQ.apk")
        .setApkUrl("http://gdown.baidu.com/data/wisegame/74dadae1bde205b0/QQ_776.apk")
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

### 结语
* 如果大家在使用的过程中有什么问题，欢迎提Issuse告知。
* 如果大家有什么好的建议或者需求，也可以提Issuse或者发送邮件至：958460248@qq.com
