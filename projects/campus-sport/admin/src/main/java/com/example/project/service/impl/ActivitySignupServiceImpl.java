package com.example.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.project.entity.ActivitySignup;
import com.example.project.mapper.ActivitySignupMapper;
import com.example.project.service.IActivitySignupService;
import org.springframework.stereotype.Service;

@Service
public class ActivitySignupServiceImpl extends ServiceImpl<ActivitySignupMapper, ActivitySignup> implements IActivitySignupService {
}
