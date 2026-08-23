package com.example.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.project.entity.Timeslot;
import com.example.project.service.ITimeslotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 定时更新过期时间段
 */
@Component
@Slf4j
public class UpdateTimeslotCron {
    @Autowired
    private ITimeslotService timeslotService;

    /**
     * 每分钟扫描一次数据库，更新已经过期的预约时间段
     */
    @Scheduled(fixedRate = 60000)
    public void updateOrder(){
        UpdateWrapper<Timeslot> queryWrapper = new UpdateWrapper<Timeslot>();
        queryWrapper.eq("state_radio","可预约");
        queryWrapper.le("expire_time",new Date());
        queryWrapper.set("state_radio","已过期");
        timeslotService.update(queryWrapper);
        log.info("更新过期时间段完成");
    }
}
