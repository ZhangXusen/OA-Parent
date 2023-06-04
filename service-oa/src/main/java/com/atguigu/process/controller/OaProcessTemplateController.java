package com.atguigu.process.controller;


import com.atguigu.model.process.ProcessTemplate;
import com.atguigu.process.service.OaProcessTemplateService;
import com.atguigu.process.service.OaProcessTypeService;
import com.atguigu.result.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.netty.util.internal.ResourcesUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 审批模板 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2023-05-06
 */
@Api(tags = "审批模板")
@RestController
@RequestMapping("/admin/process/processTemp")
public class OaProcessTemplateController {
    @Autowired
    private OaProcessTemplateService processTemplateService;

    @ApiOperation("获取审批模板数据")
    @GetMapping("{pageNum}/{pageSize}")
    public Result getPage(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        Page<ProcessTemplate> page = new Page<>(pageNum, pageSize);
        IPage<ProcessTemplate> templateIPage = processTemplateService.selectPageProcessTemp(page);
        return Result.ok(templateIPage);
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.list')")
    @ApiOperation(value = "获取")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        ProcessTemplate processTemplate = processTemplateService.getById(id);
        return Result.ok(processTemplate);
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.templateSet')")
    @ApiOperation(value = "新增")
    @PostMapping("save")
    public Result save(@RequestBody ProcessTemplate processTemplate) {
        processTemplateService.save(processTemplate);
        return Result.ok();
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.templateSet')")
    @ApiOperation(value = "修改")
    @PutMapping("update")
    public Result updateById(@RequestBody ProcessTemplate processTemplate) {
        processTemplateService.updateById(processTemplate);
        return Result.ok();
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.remove')")
    @ApiOperation(value = "删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        processTemplateService.removeById(id);
        return Result.ok();
    }

    @ApiOperation("上传流程图文件")
    @PostMapping("/uploadProcess")
    public Result Upload(@RequestPart @RequestParam("file") MultipartFile file) throws FileNotFoundException {
        String fileName = file.getOriginalFilename();
        //获取绝对路径
        String path = new File(ResourceUtils.getURL("classpath:").getPath()).getAbsolutePath();
        System.out.println("11111111111111111111" + path);
        //生成目录
        File tempFile = new File(path + "/processes/");
        if (!tempFile.exists()) {
            tempFile.mkdirs();
        }
        File imageFile = new File(path + "/processes/" + fileName);
        try {
            file.transferTo(imageFile);

        } catch (IOException e) {
            return Result.fail().code(206).message("上传失败");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("processDefinitionPath", "processes/" + fileName);
        map.put("processDefinitionKey", fileName.substring(0, fileName.lastIndexOf(",")));
        return Result.ok(map);
    }

    @ApiOperation("发布审批模板")
    @PostMapping("/publish/{id}")
    public Result Publish(@PathVariable("id") Long id) {
        //修改模板表中状态值
        processTemplateService.publish(id);
        return Result.ok();
    }

}

