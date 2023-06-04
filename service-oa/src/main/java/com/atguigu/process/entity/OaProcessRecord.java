package com.atguigu.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 审批记录
 * </p>
 *
 * @author atguigu
 * @since 2023-05-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OaProcessRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 审批流程id
     */
    private Long processId;

    /**
     * 审批描述
     */
    private String description;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 操作用户id
     */
    private Long operateUserId;

    /**
     * 操作用户
     */
    private String operateUser;

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
