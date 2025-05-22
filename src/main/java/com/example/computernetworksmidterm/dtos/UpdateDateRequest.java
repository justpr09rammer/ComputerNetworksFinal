package com.example.computernetworksmidterm.dtos;

import com.example.computernetworksmidterm.entity.DateDuration;
import lombok.Data;

@Data
public class UpdateDateRequest {
    private String phoneNumber;
    private DateDuration dateDuration;
}