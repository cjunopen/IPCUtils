package com.github.cjunopen.ipc_utils;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.blankj.utilcode.util.ThreadUtils;
import com.github.cjunopen.ipc_library.interfaces.IKSongForLiyuHome;
import com.github.cjunopen.ipc_library.interfaces.ILiyuHomeForKSong;
import com.github.cjunopen.ipc_library.ksong.req.WalkLanternReq;
import com.github.cjunopen.ipc_library.manager.LiyuHomeIPCManager;
import com.github.cjunopen.ipc_library.util.GsonUtil;
import com.google.gson.Gson;

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

//        LiyuHomeIPCManager.getInstance().bind();
//
//        LiyuHomeIPCManager.getInstance().setOnBindListener(new BaseIPCManager.OnBindListener() {
//            @Override
//            public void onBind() {
//                LiyuHomeIPCManager.getInstance().registerLiyuHomeListener(new IAndlinkerRemoteCallback() {
//                    @Override
//                    public void onCallBack(String str) {
//                        ThreadUtils.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                showToast("onCallBack: " + str);
//                            }
//                        });
//                    }
//                });
//            }
//        });

        LiyuHomeIPCManager.getInstance().setIKSongForLiyuHome(new IKSongForLiyuHome() {
            @Override
            public void sendWalkLantern(WalkLanternReq req) {
                //K歌app处理 WalkLanternReq 展示走马灯
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast(GsonUtil.toJson(req));
                    }
                });
            }
        });
    }

    public static void showToast(String msg){
        Toast.makeText(sContext, msg, Toast.LENGTH_LONG).show();
    }

}
