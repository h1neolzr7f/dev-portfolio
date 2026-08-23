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
* 支付方式
* </p>
*
* @author 
* @since 2023-04-07
*/
@Getter
@Setter
@ApiModel(value = "Paytype对象", description = "支付方式")
public class Paytype implements Serializable {

private static final long serialVersionUID = 1L;

    // 方式编号
    @ApiModelProperty("方式编号")
    @Alias("方式编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


    // 支付名称
    @ApiModelProperty("支付名称")
    @Alias("支付名称")
    private String name;


    // 二维码
    @ApiModelProperty("二维码")
    @Alias("二维码")
    private String img;



}
