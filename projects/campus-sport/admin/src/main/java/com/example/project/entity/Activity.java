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
@ApiModel(value = "Activity对象", description = "场馆活动")
public class Activity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("编号")
    @Alias("编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("活动名称")
    @Alias("活动名称")
    private String title;

    @ApiModelProperty("活动内容")
    @Alias("活动内容")
    private String content;

    @ApiModelProperty("活动地点")
    @Alias("活动地点")
    private String location;

    @ApiModelProperty("活动时间")
    @Alias("活动时间")
    private String activityTime;

    @ApiModelProperty("报名截止时间")
    @Alias("报名截止时间")
    private String deadline;

    @ApiModelProperty("人数上限")
    @Alias("人数上限")
    private Integer capacity;

    @ApiModelProperty("活动状态")
    @Alias("活动状态")
    private String stateRadio;

    @ApiModelProperty("发布人")
    @Alias("发布人")
    private Integer publisherId;

    @ApiModelProperty("备注")
    @Alias("备注")
    private String remark;

    @TableField(exist = false)
    private String publisherName;

    @TableField(exist = false)
    private Long signupCount;

    @TableField(exist = false)
    private Long approvedCount;

    @TableField(exist = false)
    private Long pendingCount;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
