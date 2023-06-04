package com.atguigu.process.service.impl;

import com.atguigu.model.process.ProcessTemplate;
import com.atguigu.model.process.ProcessType;
import com.atguigu.process.entity.OaProcessType;
import com.atguigu.process.mapper.OaProcessTypeMapper;
import com.atguigu.process.service.OaProcessTemplateService;
import com.atguigu.process.service.OaProcessTypeService;
import com.atguigu.vo.process.ProcessFormVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-05-06
 */
@Service
public class OaProcessTypeServiceImpl extends ServiceImpl<OaProcessTypeMapper, ProcessType> implements OaProcessTypeService {
    @Autowired
    private OaProcessTemplateService processTemplateService;

    @Override
    public List<ProcessType> findProcessTypeAndTemp() {
        List<ProcessType> processTypeList = baseMapper.selectList(null);
        List<Long> typeIds = new ArrayList<>();
        for (ProcessType processType : processTypeList) {
            Long typeId = processType.getId();
            LambdaQueryWrapper<ProcessTemplate> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ProcessTemplate::getProcessTypeId, typeId);
            List<ProcessTemplate> processTemplateList = processTemplateService.list(queryWrapper);
            processType.setProcessTemplateList(processTemplateList);
        }
        return processTypeList;
    }




}
