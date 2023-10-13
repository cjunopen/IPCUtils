package com.github.cjunopen.ipc_library.resp;

import lombok.Data;

@Data
public class IpcBaseResponse<T> {

    //装载数据
    private T data;

    //0 正常
    private int code;

    //通常用于错误信息
    private String msg;

    private String cmdId;
}
