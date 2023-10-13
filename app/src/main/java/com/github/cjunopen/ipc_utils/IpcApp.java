package com.github.cjunopen.ipc_utils;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.github.cjunopen.ipc_library.interfaces.IAndlinkerRemoteCallback;
import com.github.cjunopen.ipc_library.manager.BaseIPCManager;
import com.github.cjunopen.ipc_library.manager.LiyuHomeIPCManager;
import com.github.cjunopen.ipc_library.resp.IpcBaseResponse;
import com.github.cjunopen.ipc_library.util.GsonUtil;

/**
 * @Description:
 * @Author: CJ
 * @CreateDate: 2023/10/13 9:54
 */
public class IpcApp extends Application {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = this;

        LiyuHomeIPCManager.getInstance().bind();

        LiyuHomeIPCManager.getInstance().setOnBindListener(new BaseIPCManager.OnBindListener() {
            @Override
            public void onBind() {
                LiyuHomeIPCManager.getInstance().registerLiyuHomeListener(new IAndlinkerRemoteCallback() {
                    @Override
                    public void onCallBack(String str) {
                        showToast("onCallBack: " + str);
                    }
                });
            }
        });


    }

    public static void showToast(String msg){
        Toast.makeText(sContext, msg, Toast.LENGTH_LONG).show();
    }

}
