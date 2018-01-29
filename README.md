#### 功能介绍
* [x] 支持断点下载
* [x] 支持后台下载
* [x] 支持自定义下载过程
* [x] 支持 设备 >= Android M 动态权限的申请
* [x] 支持通知栏进度条展示(或者自定义显示进度)
* [x] 支持Android N
* [x] 支持Android O


#### DownloadManager：配置文档
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

#### UpdateConfiguration：配置文档
属性      | 描述		| 默认值
:-------- | :-------- | :--------
notifyId | 通知栏消息id  | 1011
notificationChannel | 适配Android O的渠道通知 | 详情查阅源码
httpManager | 设置自己的下载过程  | null
breakpointDownload | 是否需要支持断点下载  | true
enableLog | 是否需要日志输出  | true
onDownloadListener | 下载过程的回调  | null
jumpInstallPage | 下载完成是否自动弹出安装页面  | true