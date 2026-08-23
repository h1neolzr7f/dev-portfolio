package com.example.project.entity;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;
import cn.hutool.core.annotation.Alias;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.project.common.LDTConfig;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
* <p>
* 预约
* </p>
*
* @author
* @since 2023-04-07
*/
@Getter
@Setter
@ApiModel(value = "Books对象", description = "预约")
public class Books implements Serializable {

private static final long serialVersionUID = 1L;

    // 预约编号
    @ApiModelProperty("预约编号")
    @Alias("预约编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


    // 预约号
    @ApiModelProperty("预约号")
    @Alias("预约号")
    private String name;


    // 预约明细
    @ApiModelProperty("预约明细")
    @Alias("预约明细")
    private String content;


    // 订单状态,已预约|签到|结束|已取消
    @ApiModelProperty("订单状态,已预约|签到|结束|已取消")
    @Alias("订单状态,已预约|签到|结束|已取消")
    private String stateRadio;


    // 预约用户
    @ApiModelProperty("预约用户")
    @Alias("预约用户")
    private Integer userId;


    // 预约时间
    @ApiModelProperty("预约时间")
    @Alias("预约时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;


    // 更新时间
    @ApiModelProperty("更新时间")
    @Alias("更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;


    // 场地编号
    @ApiModelProperty("场地编号")
    @Alias("场地编号")
    private String goodids;


    // 进场时间
    @ApiModelProperty("进场时间")
    @Alias("进场时间")
    private String intime;


    // 离场时间
    @ApiModelProperty("离场时间")
    @Alias("离场时间")
    private String outtime;


    // 离场时间
    @ApiModelProperty("离场时间")
    @Alias("离场时间")
    private Integer timeslotId;

    // 离场时间
    @ApiModelProperty("订金")
    @Alias("订金")
    private Double price;


    // 离场时间
    @ApiModelProperty("总费用")
    @Alias("总费用")
    private Double total;

    @ApiModelProperty("租金单价")
    @Alias("租金单价")
    private Double rent;



}
