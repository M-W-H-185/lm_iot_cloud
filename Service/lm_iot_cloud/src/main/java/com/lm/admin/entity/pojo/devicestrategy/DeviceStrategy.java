package com.lm.admin.entity.pojo.devicestrategy;

import com.lm.admin.config.mybatis.annotation.FieldFill;
import com.lm.admin.config.mybatis.annotation.IdType;
import com.lm.admin.config.mybatis.annotation.TableField;
import com.lm.admin.config.mybatis.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 设备策略 - mysql
 * @author Lm
 * @date 2022/11/6 15:02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceStrategy implements Serializable {
    // 策略id
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    // 策略名称
    private String name;
    // 策略描述
    private String describe;
    // 触发条件表达式字符串
    private String triggerStr;
    // 执行动作字符串
    private String actionStr;
    // 是否启用 0 不启用 1 启用
    private Integer status;
    // 创建时间
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    // 更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
