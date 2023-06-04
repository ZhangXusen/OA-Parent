package com.atguigu.process.service.impl;

import com.atguigu.model.process.ProcessTemplate;
import com.atguigu.model.process.ProcessType;
import com.atguigu.process.entity.OaProcessTemplate;
import com.atguigu.process.mapper.OaProcessTemplateMapper;
import com.atguigu.process.service.OaProcessService;
import com.atguigu.process.service.OaProcessTemplateService;
import com.atguigu.process.service.OaProcessTypeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.activiti.engine.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * <p>
 * 审批模板 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-05-06
 */
@Service
public class OaProcessTemplateServiceImpl extends ServiceImpl<OaProcessTemplateMapper, ProcessTemplate> implements OaProcessTemplateService {
    @Autowired
    private OaProcessTypeService processTypeService;
    @Autowired
    private OaProcessService processService;

    @Override
    public IPage<ProcessTemplate> selectPageProcessTemp(Page<ProcessTemplate> page) {
        LambdaQueryWrapper<ProcessTemplate> queryWrapper = new LambdaQueryWrapper<ProcessTemplate>();
        queryWrapper.orderByDesc(ProcessTemplate::getId);
        Page<ProcessTemplate> TempPage = baseMapper.selectPage(page, queryWrapper);
        List<ProcessTemplate> processTemplates = TempPage.getRecords();
        List<Long> list = new ArrayList<>();
        for (ProcessTemplate template : processTemplates) {
            //获得每个对象的审批类型的id
            Long typeId = template.getProcessTypeId();
            //根据审批类型id查询类型名称
            ProcessType type = processTypeService.getById(typeId);
            if (type == null) continue;
            template.setProcessTypeName(type.getName());
        }
        return TempPage;
    }

    @Override
    public void publish(Long id) {
        ProcessTemplate template = baseMapper.selectById(id);
        template.setStatus(1);
        baseMapper.updateById(template);
        //部署流程
        if (!StringUtils.isEmpty(template.getProcessDefinitionPath())) {
            processService.deployByZip(template.getProcessDefinitionPath());
        }

    }


}
