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
* 教学视频
* </p>
*
* @author 
* @since 2023-04-07
*/
@Getter
@Setter
@ApiModel(value = "Sportvideo对象", description = "教学视频")
public class Sportvideo implements Serializable {

private static final long serialVersionUID = 1L;

    // 主键
    @ApiModelProperty("主键")
    @Alias("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


    // 标题
    @ApiModelProperty("标题")
    @Alias("标题")
    private String name;


    // 视频
    @ApiModelProperty("视频")
    @Alias("视频")
    private String vedio;


    // 内容描述
    @ApiModelProperty("内容描述")
    @Alias("内容描述")
    private String content;


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


    // 封面图片
    @ApiModelProperty("封面图片")
    @Alias("封面图片")
    private String img;



}
