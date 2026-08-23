package com.example.project.service;

import com.example.project.entity.TimeSlotDto;
import com.example.project.entity.Timeslot;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 场地时间安排表 服务类
 * </p>
 *
 * @author
 * @since 2023-04-07
 */
public interface ITimeslotService extends IService<Timeslot> {

    List<TimeSlotDto> getTimeSlotList(Integer areaId);
}
