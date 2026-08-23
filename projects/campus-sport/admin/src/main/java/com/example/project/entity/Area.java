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
* 体育场地
* </p>
*
* @author 
* @since 2023-04-07
*/
@Getter
@Setter
@ApiModel(value = "Area对象", description = "体育场地")
public class Area implements Serializable {

private static final long serialVersionUID = 1L;

    // 主键
    @ApiModelProperty("主键")
    @Alias("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


    // 名称
    @ApiModelProperty("名称")
    @Alias("名称")
    private String name;


    // 描述
    @ApiModelProperty("描述")
    @Alias("描述")
    private String content;


    // 图片
    @ApiModelProperty("图片")
    @Alias("图片")
    private String img;


    // 订金
    @ApiModelProperty("订金")
    @Alias("订金")
    private Double price;


    // 租金(小时)
    @ApiModelProperty("租金(小时)")
    @Alias("租金(小时)")
    private Double rent;


    // 创建时间
    @ApiModelProperty("创建时间")
    @Alias("创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;


    // 修改时间
    @ApiModelProperty("修改时间")
    @Alias("修改时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;


    // 场地类型
    @ApiModelProperty("场地类型")
    @Alias("场地类型")
    private Integer categoryId;


    // 浏览次数
    @ApiModelProperty("浏览次数")
    @Alias("浏览次数")
    private Integer views;



}
