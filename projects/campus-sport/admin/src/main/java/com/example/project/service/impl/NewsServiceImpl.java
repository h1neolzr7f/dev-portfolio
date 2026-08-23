package com.example.project.service.impl;

import com.example.project.entity.News;
import com.example.project.mapper.NewsMapper;
import com.example.project.service.INewsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统公告 服务实现类
 * </p>
 *
 * @author 
 * @since 2023-04-07
 */
@Service
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News> implements INewsService {

}
