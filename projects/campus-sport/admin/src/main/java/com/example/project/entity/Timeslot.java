package com.example.project.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
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
* 场地时间安排表
* </p>
*
* @author 
* @since 2023-04-07
*/
@Getter
@Setter
@ApiModel(value = "Timeslot对象", description = "场地时间安排表")
public class Timeslot implements Serializable {

private static final long serialVersionUID = 1L;

    // 编号
    @ApiModelProperty("编号")
    @Alias("编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


    // 体育场地
    @ApiModelProperty("体育场地")
    @Alias("体育场地")
    private Integer areaId;


    // 预约日期
    @ApiModelProperty("预约日期")
    @Alias("预约日期")
    private String bookDate;


    // 预约时刻
    @ApiModelProperty("预约时刻")
    @Alias("预约时刻")
    private Integer timetableId;


    // 过期时间
    @ApiModelProperty("过期时间")
    @Alias("过期时间")
    private String expireTime;


    // 状态,可预约|已预约|已过期
    @ApiModelProperty("状态,可预约|已预约|已过期")
    @Alias("状态,可预约|已预约|已过期")
    private String stateRadio;



}
