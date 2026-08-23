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
* 轮播图
* </p>
*
* @author 
* @since 2023-04-07
*/
@Getter
@Setter
@ApiModel(value = "Rotation对象", description = "轮播图")
public class Rotation implements Serializable {

private static final long serialVersionUID = 1L;

    // 轮播图编号
    @ApiModelProperty("轮播图编号")
    @Alias("轮播图编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


    // 图片
    @ApiModelProperty("图片")
    @Alias("图片")
    private String img;


    // 是否首页
    @ApiModelProperty("是否首页")
    @Alias("是否首页")
    private String indexRadio;



}
