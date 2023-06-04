package com.atguigu.wechat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 菜单
 * </p>
 *
 * @author atguigu
 * @since 2023-06-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WechatMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 上级id
     */
    private Long parentId;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 类型
     */
    private String type;

    /**
     * 网页 链接，用户点击菜单可打开链接
     */
    private String url;

    /**
     * 菜单KEY值，用于消息接口推送
     */
    private String meunKey;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer isDeleted;


}
