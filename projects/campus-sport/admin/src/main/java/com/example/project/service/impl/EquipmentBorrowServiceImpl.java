package com.example.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.project.entity.EquipmentBorrow;
import com.example.project.mapper.EquipmentBorrowMapper;
import com.example.project.service.IEquipmentBorrowService;
import org.springframework.stereotype.Service;

@Service
public class EquipmentBorrowServiceImpl extends ServiceImpl<EquipmentBorrowMapper, EquipmentBorrow> implements IEquipmentBorrowService {
}
