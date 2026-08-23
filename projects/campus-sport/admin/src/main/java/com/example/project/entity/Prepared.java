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
* 预选场地
* </p>
*
* @author 
* @since 2023-04-07
*/
@Getter
@Setter
@ApiModel(value = "Prepared对象", description = "预选场地")
public class Prepared implements Serializable {

private static final long serialVersionUID = 1L;

    // 体育场编号
    @ApiModelProperty("体育场编号")
    @Alias("体育场编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


    // 预选场地
    @ApiModelProperty("预选场地")
    @Alias("预选场地")
    private Integer userId;


    // 订金
    @ApiModelProperty("订金")
    @Alias("订金")
    private Double price;


    // 租金(小时)
    @ApiModelProperty("租金(小时)")
    @Alias("租金(小时)")
    private Double rent;


    // 场地名称
    @ApiModelProperty("场地名称")
    @Alias("场地名称")
    private String name;


    // 场地图片
    @ApiModelProperty("场地图片")
    @Alias("场地图片")
    private String img;


    // 场地编号
    @ApiModelProperty("场地编号")
    @Alias("场地编号")
    private Integer goodid;



}
