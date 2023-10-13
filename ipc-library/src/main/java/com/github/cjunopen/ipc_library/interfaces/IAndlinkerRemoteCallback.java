package com.github.cjunopen.ipc_library.interfaces;

import com.codezjx.andlinker.annotation.RemoteInterface;

@RemoteInterface
public interface IAndlinkerRemoteCallback {
    void onCallBack(String str);
}
