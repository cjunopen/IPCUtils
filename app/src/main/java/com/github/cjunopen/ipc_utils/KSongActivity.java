package com.github.cjunopen.ipc_utils;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.github.cjunopen.ipc_library.interfaces.ILiyuHomeForKSong;
import com.github.cjunopen.ipc_library.manager.LiyuHomeIPCManager;

public class KSongActivity extends AppCompatActivity {

    private ILiyuHomeForKSong mILiyuHomeForKSong = LiyuHomeIPCManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ksong);

        initView();
    }

    private void initView() {
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mILiyuHomeForKSong.launchAlarmBusiness();
            }
        });
    }
}