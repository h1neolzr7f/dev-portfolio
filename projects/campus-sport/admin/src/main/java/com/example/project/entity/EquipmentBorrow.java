package com.example.project.entity;

import cn.hutool.core.annotation.Alias;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ApiModel(value = "EquipmentBorrow对象", description = "器材借用")
public class EquipmentBorrow implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("编号")
    @Alias("编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("器材")
    @Alias("器材")
    private Integer equipmentId;

    @ApiModelProperty("用户")
    @Alias("用户")
    private Integer userId;

    @ApiModelProperty("借用数量")
    @Alias("借用数量")
    private Integer borrowQuantity;

    @ApiModelProperty("借用时间")
    @Alias("借用时间")
    private String borrowTime;

    @ApiModelProperty("归还时间")
    @Alias("归还时间")
    private String returnTime;

    @ApiModelProperty("状态")
    @Alias("状态")
    private String stateRadio;

    @ApiModelProperty("备注")
    @Alias("备注")
    private String remark;

    @TableField(exist = false)
    private String equipmentName;

    @TableField(exist = false)
    private String userName;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
