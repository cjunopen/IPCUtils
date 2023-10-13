package com.github.cjunopen.ipc_library.req;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description:
 * @Author: CJ
 * @CreateDate: 2023/10/13 9:27
 */
@Data
@Accessors(chain = true)
public class IpcBaseRequest<T> {

    private String cmdId;

    private T data;

}
