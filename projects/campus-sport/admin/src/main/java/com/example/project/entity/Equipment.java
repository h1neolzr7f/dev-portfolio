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
@ApiModel(value = "Equipment对象", description = "体育器材")
public class Equipment implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("编号")
    @Alias("编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("器材名称")
    @Alias("器材名称")
    private String name;

    @ApiModelProperty("器材类型")
    @Alias("器材类型")
    private String type;

    @ApiModelProperty("总库存")
    @Alias("总库存")
    private Integer totalStock;

    @ApiModelProperty("可借库存")
    @Alias("可借库存")
    private Integer availableStock;

    @ApiModelProperty("存放位置")
    @Alias("存放位置")
    private String location;

    @ApiModelProperty("状态")
    @Alias("状态")
    private String stateRadio;

    @ApiModelProperty("备注")
    @Alias("备注")
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
