package com.atguigu.process.service;

import com.atguigu.model.process.ProcessRecord;
import com.atguigu.process.entity.OaProcessRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 审批记录 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-05-09
 */
public interface OaProcessRecordService extends IService<ProcessRecord> {
    void record(Long processId,Integer status,String desc);
}
