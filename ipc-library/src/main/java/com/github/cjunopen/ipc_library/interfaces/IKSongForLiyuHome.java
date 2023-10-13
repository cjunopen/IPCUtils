package com.github.cjunopen.ipc_library.interfaces;

import com.github.cjunopen.ipc_library.ksong.req.WalkLanternReq;

/**
 * @Description: K歌给利瑜桌面提供的接口
 * @Author: CJ
 * @CreateDate: 2023/10/13 16:25
 */
public interface IKSongForLiyuHome {

    //发送走马灯消息
    void sendWalkLantern(WalkLanternReq req);

}
