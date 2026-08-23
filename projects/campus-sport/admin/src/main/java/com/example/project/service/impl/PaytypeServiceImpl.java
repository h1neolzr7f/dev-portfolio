package com.example.project.service.impl;

import com.example.project.entity.Paytype;
import com.example.project.mapper.PaytypeMapper;
import com.example.project.service.IPaytypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 支付方式 服务实现类
 * </p>
 *
 * @author 
 * @since 2023-04-07
 */
@Service
public class PaytypeServiceImpl extends ServiceImpl<PaytypeMapper, Paytype> implements IPaytypeService {

}
