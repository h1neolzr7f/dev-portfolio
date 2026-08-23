package com.example.project.mapper;

import com.example.project.entity.TimeSlotDto;
import com.example.project.entity.Timeslot;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 场地时间安排表 Mapper 接口
 * </p>
 *
 * @author
 * @since 2023-04-07
 */
public interface TimeslotMapper extends BaseMapper<Timeslot> {


    List<TimeSlotDto> selectTimeSlotList(Integer areaId);
}
