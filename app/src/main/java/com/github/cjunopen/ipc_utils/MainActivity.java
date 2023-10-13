package com.github.cjunopen.ipc_utils;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.blankj.utilcode.util.GsonUtils;
import com.github.cjunopen.ipc_library.constant.CmdIdConstant;
import com.github.cjunopen.ipc_library.manager.LiyuHomeIPCManager;
import com.github.cjunopen.ipc_library.req.IpcBaseRequest;
import com.github.cjunopen.ipc_library.req.IpcTestReq;
import com.github.cjunopen.ipc_library.resp.IpcBaseResponse;
import com.github.cjunopen.ipc_library.resp.IpcTestResp;
import com.github.cjunopen.ipc_library.util.GsonUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IpcBaseRequest<IpcTestReq> request = new IpcBaseRequest<>();
                request.setCmdId(CmdIdConstant.CMD_TEST_REQ);
                request.setData(new IpcTestReq());
                String json = LiyuHomeIPCManager.getInstance().request(GsonUtil.toJson(request));
                IpcApp.showToast("onClick1: " + json);

                IpcBaseResponse<IpcTestResp> resp =
                        GsonUtil.fromJson(json, GsonUtils.getType(IpcBaseResponse.class, IpcBaseResponse.class));
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiyuHomeIPCManager.getInstance().testReq(new IpcTestReq())
                        .subscribe(new Observer<IpcTestResp>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {

                            }

                            @Override
                            public void onNext(@NonNull IpcTestResp ipcTestResp) {
                                IpcApp.showToast("onNext: " + GsonUtil.toJson(ipcTestResp));
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                IpcApp.showToast("onError: " + e.getMessage());
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        });
    }
}