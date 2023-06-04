package com.atguigu.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 审批类型
 * </p>
 *
 * @author atguigu
 * @since 2023-05-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OaProcess implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 审批code
     */
    private String processCode;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 审批模板id
     */
    private Long processTemplateId;

    /**
     * 审批类型id
     */
    private Long processTypeId;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 表单值
     */
    private String formValues;

    /**
     * 流程实例id
     */
    private String processInstanceId;

    /**
     * 当前审批人
     */
    private String currentAuditor;

    /**
     * 状态（0：默认 1：审批中 2：审批通过 -1：驳回）
     */
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
