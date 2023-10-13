package com.github.cjunopen.ipc_library.interfaces;

import com.codezjx.andlinker.annotation.Callback;
import com.codezjx.andlinker.annotation.RemoteInterface;

/**
 * @Description:
 * @Author: CJ
 * @CreateDate: 2023/10/13 9:25
 */
@RemoteInterface
public interface ILiYuHome {

    String request(String req);

    void registerLiyuHomeListener(@Callback IAndlinkerRemoteCallback callback);

    void unRegisterLiyuHomeListener();

}
