package com.github.cjunopen.ipc_library.interfaces;

/**
 * @Description: 利瑜桌面提供给K歌的接口
 * @Author: CJ
 * @CreateDate: 2023/10/13 16:06
 */
public interface ILiyuHomeForKSong {

    //启动大屏报钟模块
    boolean launchAlarmBusiness();

    //启动利瑜桌面
    boolean launchLiyuHome();

    //发送请求到终端，用于同步房态、呼叫态
    boolean sendReqToTerminalForSyncState(String bodyJson);
}
