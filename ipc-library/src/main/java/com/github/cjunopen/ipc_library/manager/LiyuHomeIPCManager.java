package com.github.cjunopen.ipc_library.manager;

import android.content.Context;
import android.text.TextUtils;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.Utils;
import com.github.cjunopen.ipc_library.constant.CmdIdConstant;
import com.github.cjunopen.ipc_library.interfaces.IAndlinkerRemoteCallback;
import com.github.cjunopen.ipc_library.interfaces.IKSongForLiyuHome;
import com.github.cjunopen.ipc_library.interfaces.ILiYuHomeIpcConnect;
import com.github.cjunopen.ipc_library.interfaces.ILiyuHomeForKSong;
import com.github.cjunopen.ipc_library.ksong.req.WalkLanternReq;
import com.github.cjunopen.ipc_library.req.IpcBaseRequest;
import com.github.cjunopen.ipc_library.resp.IpcBaseResponse;
import com.github.cjunopen.ipc_library.util.GsonUtil;

import timber.log.Timber;

/**
 * @Description:
 * @Author: CJ
 * @CreateDate: 2023/10/13 9:25
 */
public class LiyuHomeIPCManager extends BaseIPCManager<ILiYuHomeIpcConnect> implements ILiYuHomeIpcConnect, ILiyuHomeForKSong {

    private IKSongForLiyuHome mIKSongForLiyuHome;

    private IAndlinkerRemoteCallback mIAndlinkerRemoteCallback;

    public LiyuHomeIPCManager(Context context) {
        super(context);
        Timber.plant(new Timber.DebugTree());
    }

    public static LiyuHomeIPCManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static LiyuHomeIPCManager INSTANCE = new LiyuHomeIPCManager(Utils.getApp());
    }

    @Override
    protected String getTargetPkg() {
        return "com.evideo.projector.home";
    }

    @Override
    protected String getTargetAction() {
        return "com.evideo.projector.home.liyu.ANDLINKER_SERVICE";
    }

    @Override
    public String request(String req) {
        if (getIRemoteService() == null) {
            return null;
        }
        return getIRemoteService().request(req);
    }

    @Override
    public void registerLiyuHomeListener(IAndlinkerRemoteCallback callback) {
        if (getIRemoteService() == null) {
            return;
        }
        getLinker().registerObject(callback);
        getIRemoteService().registerLiyuHomeListener(callback);
        mIAndlinkerRemoteCallback = callback;
    }

    @Override
    public void unRegisterLiyuHomeListener() {
        if (getIRemoteService() == null) {
            return;
        }
        if (mIAndlinkerRemoteCallback != null) {
            getLinker().unRegisterObject(mIAndlinkerRemoteCallback);
        }
        getIRemoteService().unRegisterLiyuHomeListener();
    }

    @Override
    public void release() {
        unRegisterLiyuHomeListener();
        super.release();
    }

    /**
     * 设置 K歌 提供给利瑜桌面的接口
     * @param IKSongForLiyuHome
     */
    public void setIKSongForLiyuHome(IKSongForLiyuHome IKSongForLiyuHome) {
        mIKSongForLiyuHome = IKSongForLiyuHome;
    }

    @Override
    public void bind() {
        super.bind();
        if (mIKSongForLiyuHome != null) {
            setOnBindListener(new OnBindListener() {
                @Override
                public void onBind() {
                    registerLiyuHomeListener(new IAndlinkerRemoteCallback() {
                        @Override
                        public void onCallBack(String str) {
                            handleCallBack(str, mIKSongForLiyuHome);
                        }
                    });
                }
            });
        }
    }

    /**
     * 处理利瑜桌面发来的数据
     */
    private void handleCallBack(String json, IKSongForLiyuHome IKSongForLiyuHome){
        if (IKSongForLiyuHome == null) {
            return;
        }
        IpcBaseRequest base = GsonUtil.fromJson(json, IpcBaseRequest.class);
        switch (base.getCmdId()){
            case CmdIdConstant.CMD_SEND_WALK_LANTERN:
                WalkLanternReq walkLanternReq = GsonUtil.fromJson(base.getData().toString(), WalkLanternReq.class);
                IKSongForLiyuHome.sendWalkLantern(walkLanternReq);
                break;
        }
    }

    /**
     * 启动大屏报钟业务
     */
    @Override
    public boolean launchAlarmBusiness(){
        IpcBaseRequest baseRequest = new IpcBaseRequest()
                .setCmdId(CmdIdConstant.CMD_LAUNCH_ALARM_BUSINESS);

        String json = request(GsonUtil.toJson(baseRequest));
        if (TextUtils.isEmpty(json)) {
            return false;
        }

        IpcBaseResponse response = getIpcBaseResponse(json);
        return response.getCode() == 0;
    }

    @Override
    public boolean launchLiyuHome() {
        AppUtils.launchApp("com.evideo.projector.home");
        return true;
    }

    @Override
    public boolean syncFYState(String bodyJson) {
        IpcBaseRequest<String> baseRequest = new IpcBaseRequest<String>()
                .setCmdId(CmdIdConstant.CMD_SYNC_FY_STATE)
                .setData(bodyJson);

        String json = request(GsonUtil.toJson(baseRequest));
        if (TextUtils.isEmpty(json)) {
            return false;
        }

        IpcBaseResponse response = getIpcBaseResponse(json);
        return response.getCode() == 0;
    }

    private IpcBaseResponse getIpcBaseResponse(String json){
        return GsonUtil.fromJson(json, IpcBaseResponse.class);
    }

}
