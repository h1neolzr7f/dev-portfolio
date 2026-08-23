package com.example.project.service.impl;

import com.example.project.entity.TimeSlotDto;
import com.example.project.entity.Timeslot;
import com.example.project.mapper.TimeslotMapper;
import com.example.project.service.ITimeslotService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 场地时间安排表 服务实现类
 * </p>
 *
 * @author
 * @since 2023-04-07
 */
@Service
public class TimeslotServiceImpl extends ServiceImpl<TimeslotMapper, Timeslot> implements ITimeslotService {

    @Autowired(required = false)
    private TimeslotMapper timeslotMapper;

    @Override
    public List<TimeSlotDto> getTimeSlotList(Integer areaId) {
        return timeslotMapper.selectTimeSlotList(areaId);
    }
}
