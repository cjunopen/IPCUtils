package com.github.cjunopen.ipc_library.ksong.req;

import lombok.Data;

/**
 * @Description:
 * @Author: CJ
 * @CreateDate: 2023/10/13 16:16
 */
@Data
public class WalkLanternReq {
    public int roomMqType=0;

    public int cycleTime;//轮播次数

    public long color;//走马灯颜色

    public String mqMsg;//消息内容
}
