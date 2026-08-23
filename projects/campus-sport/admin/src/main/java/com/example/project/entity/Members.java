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
* 会员
* </p>
*
* @author 
* @since 2023-04-07
*/
@Getter
@Setter
@ApiModel(value = "Members对象", description = "会员")
public class Members implements Serializable {

private static final long serialVersionUID = 1L;

    // 主键
    @ApiModelProperty("主键")
    @Alias("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


    // 姓名
    @ApiModelProperty("姓名")
    @Alias("姓名")
    private String name;


    // 年龄
    @ApiModelProperty("年龄")
    @Alias("年龄")
    private Integer age;


    // 性别,男|女
    @ApiModelProperty("性别,男|女")
    @Alias("性别,男|女")
    private String sexRadio;


    // 所属用户
    @ApiModelProperty("所属用户")
    @Alias("所属用户")
    private Integer userId;



}
