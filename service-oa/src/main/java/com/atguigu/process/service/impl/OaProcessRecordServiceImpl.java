package com.atguigu.process.service.impl;

import com.atguigu.auth.service.SysUserService;
import com.atguigu.custom.LoginUserInfoHelper;
import com.atguigu.model.process.ProcessRecord;
import com.atguigu.model.system.SysUser;
import com.atguigu.process.entity.OaProcessRecord;
import com.atguigu.process.mapper.OaProcessRecordMapper;
import com.atguigu.process.service.OaProcessRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 审批记录 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-05-09
 */
@Service
public class OaProcessRecordServiceImpl extends ServiceImpl<OaProcessRecordMapper, ProcessRecord> implements OaProcessRecordService {
    @Autowired
    private SysUserService userService;
    //记录流程
    @Override
    public void record(Long processId, Integer status, String desc) {
        Long userId = LoginUserInfoHelper.getUserId();
        SysUser user = userService.getById(userId);
        ProcessRecord record = new ProcessRecord();
        record.setProcessId(processId);
        record.setDescription(desc);
        record.setStatus(status);

        record.setOperateUser(user.getName());
        record.setOperateUserId(userId);
        baseMapper.insert(record);
    }
}
