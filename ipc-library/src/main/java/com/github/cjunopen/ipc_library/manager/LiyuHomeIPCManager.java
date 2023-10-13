package com.github.cjunopen.ipc_library.manager;

import android.content.Context;

import com.blankj.utilcode.util.Utils;
import com.github.cjunopen.ipc_library.interfaces.IAndlinkerRemoteCallback;
import com.github.cjunopen.ipc_library.interfaces.ILiYuHome;
import com.github.cjunopen.ipc_library.req.IpcTestReq;
import com.github.cjunopen.ipc_library.resp.IpcTestResp;
import com.github.cjunopen.ipc_library.util.GsonUtil;

import java.lang.reflect.Type;

import io.reactivex.rxjava3.core.Observable;
import timber.log.Timber;

/**
 * @Description:
 * @Author: CJ
 * @CreateDate: 2023/10/13 9:25
 */
public class LiyuHomeIPCManager extends BaseIPCManager<ILiYuHome> implements ILiYuHome{

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
        getLinker().unRegisterObject(mIAndlinkerRemoteCallback);
        getIRemoteService().unRegisterLiyuHomeListener();
    }

    public Observable<IpcTestResp> testReq(IpcTestReq req){
        return IpcConnectByRx(new IpcWorkAble<IpcTestResp>() {
            @Override
            public String request() {
                return LiyuHomeIPCManager.this.request(GsonUtil.toJson(req));
            }
        });
    }
}
