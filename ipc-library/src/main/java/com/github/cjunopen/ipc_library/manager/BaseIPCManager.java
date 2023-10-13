package com.github.cjunopen.ipc_library.manager;

import android.content.Context;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.StringUtils;
import com.codezjx.andlinker.AndLinker;
import com.codezjx.andlinker.adapter.OriginalCallAdapterFactory;
import com.github.cjunopen.ipc_library.resp.IpcBaseResponse;
import com.github.cjunopen.ipc_library.util.GsonUtil;
import com.whitesky.common.base.RxScheduler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import timber.log.Timber;

//IPC通信
public abstract class BaseIPCManager<T> implements LifecycleObserver {
    private T mIRemoteService;

    private AndLinker mLinker;

    private Context mContext;

    private Disposable mDisposable;

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
                    mIRemoteService = null;
                    setOnBindListener(null);
                }
            });
        }

        mLinker.bind();
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
        if (mDisposable != null && !mDisposable.isDisposed()) {
            return;
        }
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) throws Throwable {
                while (true) {
                    Thread.sleep(60 * 1000);
                    if (mIRemoteService == null) {
                        Timber.e("检查到[%s]未存活", getTargetAction());
                        bind();
                    }
                }
            }
        })
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
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

    private interface IIpcWork<E> {
        String request();
    }

    /**
     * 需要获取type，只能在内部调
     *
     * @param <E>
     */
    public abstract static class IpcWorkAble<E> implements IIpcWork<E> {
        public Type getType() {
            return GsonUtil.getGenericityType(getClass());
        }
    }

    /**
     * rxjava方式通信
     *
     * @param ipcWorkAble
     * @param <E>
     * @return
     */
    protected <E> Observable<E> IpcConnectByRx(IpcWorkAble<E> ipcWorkAble) {
        return Observable.create(new ObservableOnSubscribe<E>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<E> emitter) throws Throwable {
                if (getIRemoteService() == null) {
                    throw new Exception(StringUtils.format("%s getIRemoteService() 为空", getTargetAction()));
                }

                String json = ipcWorkAble.request();
                Type type = GsonUtils.getType(IpcBaseResponse.class, ipcWorkAble.getType());
                IpcBaseResponse<E> response = GsonUtil.fromJson(json, type);
                if (!checkIpcBaseResponse(response)) {
                    throw new Exception(response.getMsg());
                }

                emitter.onNext(response.getData());
                emitter.onComplete();
            }
        })
                .compose(RxScheduler.Obs_io_main());
    }

    /**
     * @return 是否需要检查目标服务存活
     */
    protected boolean isNeedCheckExist() {
        return true;
    }

    public void registerLifecycleObserver(Lifecycle lifecycle) {
        lifecycle.addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private void onCreate() {
        bind();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void onDestroy() {
        unBind();
    }
}
