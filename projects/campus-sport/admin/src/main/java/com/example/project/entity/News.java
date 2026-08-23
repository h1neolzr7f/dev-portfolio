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
* 系统公告
* </p>
*
* @author 
* @since 2023-04-07
*/
@Getter
@Setter
@ApiModel(value = "News对象", description = "系统公告")
public class News implements Serializable {

private static final long serialVersionUID = 1L;

    // 编号
    @ApiModelProperty("编号")
    @Alias("编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


    // 名称
    @ApiModelProperty("名称")
    @Alias("名称")
    private String name;


    // 内容
    @ApiModelProperty("内容")
    @Alias("内容")
    private String content;


    // 创建时间
    @ApiModelProperty("创建时间")
    @Alias("创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;


    // 创建人
    @ApiModelProperty("创建人")
    @Alias("创建人")
    private Integer userId;


    // 图片
    @ApiModelProperty("图片")
    @Alias("图片")
    private String img;



}
