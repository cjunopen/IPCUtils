package com.github.cjunopen.ipc_library.manager;

import android.content.Context;

import androidx.annotation.WorkerThread;

import com.blankj.utilcode.util.Utils;
import com.github.cjunopen.ipc_library.constant.CmdIdConstant;
import com.github.cjunopen.ipc_library.interfaces.IAndlinkerRemoteCallback;
import com.github.cjunopen.ipc_library.interfaces.IKSongForLiyuHome;
import com.github.cjunopen.ipc_library.interfaces.ILiyuHomeForKSong;
import com.github.cjunopen.ipc_library.interfaces.ILiYuHomeIpcConnect;
import com.github.cjunopen.ipc_library.ksong.req.WalkLanternReq;
import com.github.cjunopen.ipc_library.req.IpcBaseRequest;
import com.github.cjunopen.ipc_library.req.IpcTestReq;
import com.github.cjunopen.ipc_library.resp.IpcBaseResponse;
import com.github.cjunopen.ipc_library.resp.IpcTestResp;
import com.github.cjunopen.ipc_library.util.GsonUtil;

import io.reactivex.rxjava3.core.Observable;
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
    @WorkerThread
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
        getLinker().unRegisterObject(mIAndlinkerRemoteCallback);
        getIRemoteService().unRegisterLiyuHomeListener();
    }

    /**
     * rxjava方式调用
     * @param req
     * @return
     */
    public Observable<IpcTestResp> testReq(IpcTestReq req){
        return IpcConnectByRx(new IpcWorkAble<IpcTestResp>() {
            @Override
            public String request() {
                IpcBaseRequest<IpcTestReq> baseRequest = new IpcBaseRequest<>();
                baseRequest.setCmdId(CmdIdConstant.CMD_TEST_REQ);
                baseRequest.setData(req);
                return LiyuHomeIPCManager.this.request(GsonUtil.toJson(baseRequest));
            }
        });
    }

    /**
     * 设置 K歌 提供给利瑜桌面的接口
     * @param IKSongForLiyuHome
     */
    public void setIKSongForLiyuHome(IKSongForLiyuHome IKSongForLiyuHome) {
        mIKSongForLiyuHome = IKSongForLiyuHome;
        bind();
        setOnBindListener(new OnBindListener() {
            @Override
            public void onBind() {
                registerLiyuHomeListener(new IAndlinkerRemoteCallback() {
                    @Override
                    public void onCallBack(String str) {
                        handleCallBack(str, IKSongForLiyuHome);
                    }
                });
            }
        });
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

        IpcBaseResponse response = getIpcBaseResponse(json);
        return response.getCode() == 0;
    }

    private IpcBaseResponse getIpcBaseResponse(String json){
        return GsonUtil.fromJson(json, IpcBaseResponse.class);
    }

}
