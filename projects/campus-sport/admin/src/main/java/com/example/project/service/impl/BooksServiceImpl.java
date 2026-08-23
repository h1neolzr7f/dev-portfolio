package com.example.project.service.impl;

import com.example.project.entity.Books;
import com.example.project.mapper.BooksMapper;
import com.example.project.service.IBooksService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 预约 服务实现类
 * </p>
 *
 * @author 
 * @since 2023-04-07
 */
@Service
public class BooksServiceImpl extends ServiceImpl<BooksMapper, Books> implements IBooksService {

}
