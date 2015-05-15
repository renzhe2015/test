package com.tsb.settings.broadcast;


public class RtkBroadcast
{
    public static final String RTK_BCT_SYSTEM_ENV_CHANGED = "com.skyworth.broadcast.system.env";
    public static final String RTK_BCT_DIRECTIVE = "com.skyworth.directive";
    public static final String RTK_BCT_MEDIAPLAYER_EXIT = "com.skyworth.mediaplayer.exit";// 退出播放器
    public static final String RTK_BCT_MEDIAPLAYER_BEGIN = "com.skyworth.mediaplayer.begin";// 每曲开始
    public static final String RTK_BCT_MEDIAPLAYER_OUTEROPEN = "com.skyworth.mediaplayer.outeropen";// 外部调播放器
    public static final String RTK_BCT_HOTKEYS_SOURCESWITCH = "com.skyworth.hotkeys.sourceswitch";// 信号源
    public static final String RTK_BCT_HOTKEYS_MUTE = "com.skyworth.hotkeys.mute";// 静音
    public static final String RTK_BCT_HOTKEYS_VOLUMEADJUST = "com.skyworth.hotkeys.volumeadjust";// 音量调节
    public static final String RTK_BCT_HOTKEYS_SIDEKEYS = "com.skyworth.hotkeys.sidekeys";// 键控板
    public static final String RTK_BCT_HOTKEYS_DISPLAYMODE = "com.skyworth.hotkeys.displaymode";// 显示模式
    public static final String RTK_BCT_HOTKEYS_PICTUREMODE = "com.skyworth.hotkeys.picturemode";// 图像模式
    public static final String RTK_BCT_HOTKEYS_SOUNDMODE = "com.skyworth.hotkeys.soundmode";// 声音模式
    public static final String RTK_BCT_HOTKEYS_3DMODE = "com.skyworth.hotkeys.3dmode";// 3D模式
    public static final String RTK_BCT_LANG_SETTING_CHANGED = "com.skyworth.broadcast.lang.setting";
    public static final String RTK_BCT_SLEEP_TIME_SETTING = "com.skyworth.broadcast.sleeptime.setting";// 设置睡眠时间
    public static final String RTK_BCT_SYSTEM_STATUS_ENV_CHANGED = "com.skyworth.broadcast.status.env";// 状态栏
    public static final String RTK_BCT_SEND_HOTKEYS = "com.android.sky.SendHotKey";// 键值广播
    public static final String RTK_BCT_VOICE_KEYS = "com.skyworth.voice.key";// 语音键广播
    public static final String RTK_BCT_VOICE_VIEW = "com.skyworth.voice.view";// 语音View发广播到SFloatUIService
    public static final String RTK_BCT_VOICE_FROM_PHONE = "com.skyworth.controlservice.voicetext";// 接收手机端语音广播
    public static final String RTK_BCT_VOICE_SYNTHESIS = "com.skyworth.voice.synthesis";// 接收手机端语音广播
    public static final String RTK_BCT_VOICE_CLOSE = "com.skyworth.voice.close";// 关闭语音
    public static final String RTK_BCT_WEATHER_VIEW = "com.skyworth.weather.view";// 打开天气广播
    public static final String RTK_BCT_FAC_EXIT = "com.skyworth.broadcast.factory.eixt";// 退出工厂模式
    public static final String RTK_BCT_FAC_OUTSETOK = "com.skyworth.broadcast.factory.outsetok";// 出厂键要求显示OutsetOK
    public static final String RTK_BCT_FAC_REPROCESS_INIT_OK = "com.skyworth.broadcast.factory.reprocessinitok";// 复位初始化结束（用于返工）
    public static final String RTK_BCT_FAC_MMODE = "com.skyworth.broadcast.factory.mmode";// 老化键要求显示“M”
    public static final String RTK_BCT_FAC_RECEIVE_STANDBY = "tv.intent.action.standby";// 收到待机消息
    public static final String RTK_BCT_STOCK_VIEW = "com.skyworth.stock.view";// 打开股票页面
    public static final String RTK_BCT_COMMENT_VIEW = "com.skyworth.broadcast.toast";// 调用全局的Toast
    public static final String RTK_BCT_DMT_ITV_VOICEINPUT = "com.skyworth.dmt.itv.voiceplatform.voicedetect.intputstatus";// 讯飞输入焦点状态
    public static final String RTK_BCT_LOGACTIVITY = "com.skyworth.broadcast.logactivity";// 第三方应用调logActivity
    public static final String RTK_BCT_HOTKEYS_DISABLE = "com.skyworth.broadcast.hotkeys.disable";// standardservices不处理广播按键的广播
    public static final String RTK_BCT_ENCYCLOPEDIAS_VIEW = "com.skyworth.encyclopedias.view";// 打开百科页面
    public static final String RTK_BCT_PAUSE_AUDIO_PLAY = "com.skyworth.broadcast.resource.audioPlay.pause";// 暂停atv的音频
    public static final String RTK_BCT_PLAYER_RESOURCE_AUDIO_PAUSING = "com.skyworth.broadcast.resource.audioPlay.pause.doing";// atv音频暂停中
    public static final String RTK_BCT_PLAYER_RESOURCE_AUDIO_PAUSED = "com.skyworth.broadcast.resource.audioPlay.pause.done";// atv音频已暂停
    public static final String RTK_BCT_RESUME_AUDIO_PLAY = "com.skyworth.broadcast.resource.audioPlay.resume";// 恢复原来的音频
    public static final String RTK_BCT_PLAYER_RESOURCE_AUDIO_RESUMING = "com.skyworth.broadcast.resource.audioPlay.resume.doing";// atv音频恢复中
    public static final String RTK_BCT_PLAYER_RESOURCE_AUDIO_RESUMED = "com.skyworth.broadcast.resource.audioPlay.resume.done";// atv音频已恢复
    public static final String RTK_BCT_DIG_ON_HOME = "com.skyworth.mstar.home.window";// 通知MStar主页是否挖一角TV
    public static final String RTK_BCT_SYSTEM_SET_ENV = "com.skyworth.broadcast.set.env";// 第三方应用设置环境变量
    public static final String RTK_BCT_SNS_SHARE = "com.skyworth.broadcast.sns.share";// 第三方调分享
    public static final String RTK_BCT_SOURCE_SELECT = "com.skyworth.broadcast.source.select";// 信号源切换
    public static final String RTK_BCT_STANDARDSERVICES_RESTART = "com.skyworth.broadcast.standardservices.restart";// StandardService重启
    public static final String RTK_BCT_SHOWRECOMMENT = "com.skyworth.controlservice.showrecomment";// 显示实时评论的UI
    //public static final String RTK_BCT_SKYCLOUD_PUSH_NOTIFY = "com.skyworth.skycloud.push.notify"; // skycloud推送通知
    public static final String RTK_BCT_CUSTOMER_TOKEN_CREATED = "com.skyworth.customer_token.created"; // 金山快盘token产生
    public static final String RTK_BCT_TV_ASSISTANT = "com.skyworth.tv.assistant"; // 电视助手
    public static final String RTK_BCT_TV_ASSISTANT_WEBVIEW = "com.skyworth.tv.assistant.web"; // 电视助手webView
    public static final String RTK_BCT_CLOUD_SHARE = "com.skyworth.cloud.share"; // 电视助手webView
    public static final String RTK_BCT_TIME_SET = "com.skyworth.time.set";// 外部设置时间
    public static final String RTK_BCT_CHILD_LOCK = "com.skyworth.child.lock";// 童锁
    public static final String RTK_BCT_INPUT_STATUS_GET = "com.skyworth.dmt.itv.voiceplatform.voicedetect.inputstatus"; // 焦点在输入框中(此广播由MSTAR系统发出)
    public static final String RTK_BCT_INPUT_STATUS_CLOSE = "com.skyworth.dmt.itv.voiceplatform.voicedetect.inputstatus";// 焦点从输入框中消失(此广播由MSTAR系统发出)

    // 网络类型接口切换之后发出的广播名称
    public static final String RTK_BCT_NETWORK_INTERFACE_CHANGE = "com.skyworth.broadcast.network.interfaceChange";
    // 有线网络配置之后发出的广播名称
    public static final String RTK_BCT_NETWORK_ETHERNET_CONFIGURATION = "com.skyworth.broadcast.network.ethernetConfiguration";
    // WIFI扫描AP之后发出的广播名称
    public static final String RTK_BCT_NETWORK_WIFI_SCAN = "com.skyworth.broadcast.network.wifiScan";
    // 无线网络配置之后发出的广播名称
    public static final String RTK_BCT_NETWORK_WIFI_CONFIGURATION = "com.skyworth.broadcast.network.wifiConfiguration";
    // WIFI进行WPS配置之后发出的广播名称
    public static final String RTK_BCT_NETWORK_WIFI_WPS_AUTO_CONNECT = "com.skyworth.broadcast.network.wifiWpsConfiguration";

    // 网络状态变化需要立即发出的广播名称，这几个广播可以不带参数
    // 有线网络：网线拔出
    public static final String RTK_BCT_NETWORK_ETHERNET_DEVICE_REMOVE = "com.skyworth.broadcast.network.ethernetDeviceRemove";
    // 有线网络：网线插上
    public static final String RTK_BCT_NETWORK_ETHERNET_DEVICE_ADD = "com.skyworth.broadcast.network.ethernetDeviceAdd";
    // 有线网络：网络断开
    public static final String RTK_BCT_NETWORK_ETHERNET_NETWORK_DISCONNECT = "com.skyworth.broadcast.network.ethernetNetworkDisconnect";
    // 有线网络： 网络连通
    public static final String RTK_BCT_NETWORK_ETHERNET_NETWORK_CONNECT = "com.skyworth.broadcast.network.ethernetNetworkConnect";

    public static final String RTK_BCT_CHANGE_USER = "com.skyworth.broadcast.change_user";

    // 首页添加图标广播
    public static final String RTK_BCT_HOME_ADD_APP_SHORTCUT = "com.skyworth.broadcast.home.addShortcut";
    // 首页删除图标广播
    public static final String RTK_BCT_HOME_REMOVE_APP_SHORTCUT = "com.skyworth.broadcast.home.removeShortcut";
    // 停止文件扫描广播
    public static final String RTK_BCT_PAUSE_FILESCAN = "com.skyworth.broadcast.media_pause_filescan";
    // 恢复文件扫描广播
    public static final String RTK_BCT_RESUME_FILESCAN = "com.skyworth.broadcast.media_resume_filescan";
    // 重启文件扫描广播
    public static final String RTK_BCT_RESTART_FILESCAN = "com.skyworth.broadcast.media_restart_filescan";
    // 语音输入文本
    public static final String RTK_BCT_UPDATE_TEXT = "com.skyworth.controlservice.updatetext";
    // 纹理加载结束广播
    public static final String RTK_BCT_UI_TEXTURESLOADED = "com.skyworth.broadcast.ui.texturesloaded";
    // HOME主页启动完毕的广播
    public static final String RTK_BCT_HOME_STARTED = "com.skyworth.broadcast.homestarted";

    // 播放资源状态请求
    public static final String RTK_BCT_PLAYER_RESOURCE_REQUEST_STATE = "com.skyworth.broadcast.resource.player.requestState";
    // 播放资源释放请求
    public static final String RTK_BCT_PLAYER_RESOURCE_REQUEST_RELEASE = "com.skyworth.broadcast.resource.player.requestRelease";
    // 资源释放回复
    public static final String RTK_BCT_PLAYER_RESOURCE_ACK_RELEASING = "com.skyworth.broadcast.resource.player.ackReleasing";
    // 播放资源已释放
    public static final String RTK_BCT_PLAYER_RESOURCE_FREE = "com.skyworth.broadcast.resource.player.free";
    // 播放资源使用中
    public static final String RTK_BCT_PLAYER_RESOURCE_INUSE = "com.skyworth.broadcast.resource.player.inuse";

    // 恢复音频播放
    public static final String RTK_BCT_AUDIO_RESOURCE_RESUME_INFO = "com.skyworth.broadcast.resource.audio.resumeInfo";
    // 暂停原来的音频播放
    public static final String RTK_BCT_AUDIO_RESOURCE_REQUEST_RELEASE = "com.skyworth.broadcast.resource.audioPlay.resume";

    // 设置->高级设置->恢复出厂设置
    public static final String RTK_BCT_RECOVERY_AML = "com.skyworth.recovery_aml";

    // DTV->退出单独听
    public static final String RTK_BCT_EXIT_AUDIO_ALONE = "com.skyworth.exit.audioalone";
    // DTV -> 搜台完成
    public static final String RTK_BCT_DTV_CHANNEL_SEARCHED = "com.skyworth.dtv.channelsearched";
    // DTV -> 换台发的广播
    public static final String RTK_BCT_DTV_CHANGE_CHANNEL = "com.skyworth.dtv.changechannel";
    // DTV -> 退出dtv广播
    public static final String RTK_BCT_DTV_EXIT = "com.skyworth.broadcast.dtv.release.ok";

    public static final String RTK_MOVIE_CHANGED_NOTIFY = "com.skyworth.play.moivechanged";
    public static final String RTK_VOICE_RECOGNITION = "com.skyworth.voicerecognition";// 语音登陆
    public static final String RTK_BCT_SHOW_WEBVIEW = "com.skyworth.webview";// webview
    public static final String RTK_BCT_LOGIN_MEMBER = "com.skyworth.login_member";// 切换用户
    public static final String RTK_BCT_IFLYTEK_RAIN_SYSTEM = "com.skyworth.iflytekrain";// 科大讯飞雨点系统合作

    public static final String RTK_BCT_FACTORY_SDCARD_INITOK = "com.skyworth.sdcard.initok";

    public static final String RTK_BCT_FACTORY_SDCARD_INITNG = "com.skyworth.sdcard.initng";

    // 儿童账号时间管理广播
    public static final String RTK_BCT_CHILD_TIME_MANAGER_SET = "com.skyworth.child_time_manager.set";
    public static final String RTK_BCT_CHILD_TIME_MANAGER_COUNTDOWN = "com.skyworth.child_time_manager.countdown";
    public static final String RTK_BCT_CHILD_TIME_MANAGER_SHOW = "com.skyworth.child_time_manager.show";
    public static final String RTK_BCT_CHILD_TIME_MANAGER_STOP = "com.skyworth.child_time_manager.stop";
    public static final String RTK_BCT_CHILD_TIME_MANAGER_REBOOT = "com.skyworth.child_time_manager.reboot";

	/*
	public static void registerBroadcast(String broadCast, BroadcastReceiver receiver)
	{
		IntentFilter filter = new IntentFilter(broadCast);
		Tv_strategy.getContext().registerReceiver(receiver, filter);
	}
	*/
}
