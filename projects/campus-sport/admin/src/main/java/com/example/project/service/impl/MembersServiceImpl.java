package com.example.project.service.impl;

import com.example.project.entity.Members;
import com.example.project.mapper.MembersMapper;
import com.example.project.service.IMembersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 会员 服务实现类
 * </p>
 *
 * @author
 * @since 2023-04-07
 */
@Service
public class MembersServiceImpl extends ServiceImpl<MembersMapper, Members> implements IMembersService {

}
