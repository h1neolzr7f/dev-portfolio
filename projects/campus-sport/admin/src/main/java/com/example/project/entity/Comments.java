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
import com.example.project.entity.User;
import lombok.Getter;
import lombok.Setter;

/**
* <p>
* 场地评论
* </p>
*
* @author 
* @since 2023-04-07
*/
@Getter
@Setter
@ApiModel(value = "Comments对象", description = "场地评论")
public class Comments implements Serializable {

private static final long serialVersionUID = 1L;

    // 评论编号
    @ApiModelProperty("评论编号")
    @Alias("评论编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


    // 评论内容
    @ApiModelProperty("评论内容")
    @Alias("评论内容")
    private String content;


    // 添加时间
    @ApiModelProperty("添加时间")
    @Alias("添加时间")
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


    // 用户
    @ApiModelProperty("用户")
    @Alias("用户")
    private Integer userId;


    // 场地编号
    @ApiModelProperty("场地编号")
    @Alias("场地编号")
    private Integer areaId;


    // 父评论ID
    @ApiModelProperty("父评论ID")
    @Alias("父评论ID")
    private Integer pid;


    // 父级用户ID
    @ApiModelProperty("父级用户ID")
    @Alias("父级用户ID")
    private Integer puserId;


    // 评论星级
    @ApiModelProperty("评论星级")
    @Alias("评论星级")
    private Integer score;


    // 订单编号
    @ApiModelProperty("订单编号")
    @Alias("订单编号")
    private Integer ordersId;



    @TableField(exist = false)
    //子回复列表
    private List<Comments> children;

    @TableField(exist = false)
    //当前评论用户
    private User user;

    // 回复的用户
    @TableField(exist = false)
    private User pUser;
}
