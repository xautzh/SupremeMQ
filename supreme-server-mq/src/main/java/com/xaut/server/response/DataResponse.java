package com.xaut.server.response;

import com.xaut.server.vo.QueueVo;

import java.util.List;

public class DataResponse {
    List<QueueVo> queueResult;

    public List<QueueVo> getQueueResult() {
        return queueResult;
    }

    public void setQueueResult(List<QueueVo> queueResult) {
        this.queueResult = queueResult;
    }
}
