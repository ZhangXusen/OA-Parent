package com.atguigu.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 审批模板
 * </p>
 *
 * @author atguigu
 * @since 2023-05-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OaProcessTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色id
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 图标路径
     */
    private String iconUrl;

    private String processTypeId;

    /**
     * 表单属性
     */
    private String formProps;

    /**
     * 表单选项
     */
    private String formOptions;

    /**
     * 流程定义key
     */
    private String processDefinitionKey;

    /**
     * 流程定义上传路径
     */
    private String processDefinitionPath;

    /**
     * 流程定义模型id
     */
    private String processModelId;

    /**
     * 描述
     */
    private String description;

    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 删除标记（0:不可用 1:可用）
     */
    private Integer isDeleted;


}
