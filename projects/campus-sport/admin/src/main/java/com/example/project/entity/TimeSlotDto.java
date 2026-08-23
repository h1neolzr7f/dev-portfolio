package com.example.project.entity;

import lombok.Data;

@Data
public class TimeSlotDto {
    private String time;
    private Boolean selected;
    private Integer id;
    private String stateRadio;
}
