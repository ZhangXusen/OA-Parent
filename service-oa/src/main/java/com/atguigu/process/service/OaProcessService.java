package com.atguigu.process.service;

import com.atguigu.model.process.Process;
import com.atguigu.process.entity.OaProcess;
import com.atguigu.vo.process.ApprovalVo;
import com.atguigu.vo.process.ProcessFormVo;
import com.atguigu.vo.process.ProcessQueryVo;
import com.atguigu.vo.process.ProcessVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import me.chanjar.weixin.common.error.WxErrorException;

import java.util.Map;

/**
 * <p>
 * 审批类型 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-05-07
 */
public interface OaProcessService extends IService<Process> {

    IPage<ProcessVo> selectPage(Page<ProcessVo> page, ProcessQueryVo query);

    void deployByZip(String path);

    void startUp(ProcessFormVo query) throws WxErrorException;

    IPage<ProcessVo> findPending(Page<Process> page);

    Map<String, Object> show(Long id);

    void approve(ApprovalVo approvalVo) throws WxErrorException;

    IPage<ProcessVo> findProcessed(Page<Process> pageParam);

    IPage<ProcessVo> findStarted(Page<ProcessVo> pageParam);
}
