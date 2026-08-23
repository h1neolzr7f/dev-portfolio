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
@ApiModel(value = "ActivitySignup对象", description = "活动报名")
public class ActivitySignup implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("编号")
    @Alias("编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("活动")
    @Alias("活动")
    private Integer activityId;

    @ApiModelProperty("报名用户")
    @Alias("报名用户")
    private Integer userId;

    @ApiModelProperty("报名时间")
    @Alias("报名时间")
    private String applyTime;

    @ApiModelProperty("审核状态")
    @Alias("审核状态")
    private String stateRadio;

    @ApiModelProperty("审核人")
    @Alias("审核人")
    private Integer reviewerId;

    @ApiModelProperty("审核时间")
    @Alias("审核时间")
    private String reviewTime;

    @ApiModelProperty("申请备注")
    @Alias("申请备注")
    private String remark;

    @ApiModelProperty("审核意见")
    @Alias("审核意见")
    private String reviewRemark;

    @TableField(exist = false)
    private String activityTitle;

    @TableField(exist = false)
    private String activityTime;

    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private String reviewerName;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
