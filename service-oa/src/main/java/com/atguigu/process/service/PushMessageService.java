package com.atguigu.process.service;


import me.chanjar.weixin.common.error.WxErrorException;

public interface PushMessageService {
    void pushPendingMsg(Long processId, Long userId, String taskId) throws WxErrorException;
    void pushProcessedMessage(Long processId, Long userId, Integer status);
}
