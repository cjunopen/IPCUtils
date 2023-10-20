package com.github.cjunopen.ipc_library.manager;

import android.content.Context;

import com.codezjx.andlinker.AndLinker;
import com.codezjx.andlinker.adapter.OriginalCallAdapterFactory;
import com.github.cjunopen.ipc_library.resp.IpcBaseResponse;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import timber.log.Timber;

//IPC通信
public abstract class BaseIPCManager<T> {
    private T mIRemoteService;

    private AndLinker mLinker;

    private Context mContext;

    private boolean isChecking;

    protected abstract String getTargetPkg();

    protected abstract String getTargetAction();

    public BaseIPCManager(Context context) {
        mContext = context;
        AndLinker.enableLogger(true);
    }

    public interface OnBindListener {
        void onBind();
    }

    private OnBindListener mOnBindListener;

    public void bind() {
        if (mIRemoteService != null) {
            return;
        }
        if (isNeedCheckExist()) {
            checkIPCExist();
        }

        if (mLinker == null) {
            mLinker = new AndLinker.Builder(mContext)
                    .packageName(getTargetPkg())
                    .action(getTargetAction())
                    // Specify the callback executor by yourself
                    //.addCallAdapterFactory(OriginalCallAdapterFactory.create(callbackExecutor))
                    .addCallAdapterFactory(OriginalCallAdapterFactory.create()) // Basic
                    //.addCallAdapterFactory(RxJava2CallAdapterFactory.create())  // RxJava2
                    .build();

            mLinker.setBindCallback(new AndLinker.BindCallback() {
                @Override
                public void onBind() {
                    Timber.tag(getTargetAction()).e("onBind");
                    mIRemoteService = mLinker.create(getTClass());

                    if (mOnBindListener != null) {
                        mOnBindListener.onBind();
                    }
                }

                @Override
                public void onUnBind() {
                    Timber.tag(getTargetAction()).e("onUnBind");
                    release();
                }
            });
        }

        mLinker.bind();
    }

    public void release(){
        mIRemoteService = null;
        setOnBindListener(null);
    }

    public void unBind() {
        mLinker.unbind();
        mLinker.setBindCallback(null);
        mLinker = null;
    }

    public T getIRemoteService() {
        if (mIRemoteService == null) {
            bind();
        }
        return mIRemoteService;
    }

    private Class<T> getTClass() {
        Type[] types = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
        Class<T> tClass = (Class<T>) types[0];
        return tClass;
    }

    public OnBindListener getOnBindListener() {
        return mOnBindListener;
    }

    public void setOnBindListener(OnBindListener onBindListener) {
        mOnBindListener = onBindListener;
    }

    private void checkIPCExist() {
        if (isChecking){
            return;
        }
        isChecking = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(60 * 1000);
                        if (mIRemoteService == null) {
                            Timber.e("检查到[%s]未存活", getTargetAction());
                            bind();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public AndLinker getLinker() {
        return mLinker;
    }

    /**
     * 检查返回体
     *
     * @return
     */
    protected boolean checkIpcBaseResponse(IpcBaseResponse response) {
        return response.getCode() == 0;
    }

    /**
     * @return 是否需要检查目标服务存活
     */
    protected boolean isNeedCheckExist() {
        return true;
    }
}
