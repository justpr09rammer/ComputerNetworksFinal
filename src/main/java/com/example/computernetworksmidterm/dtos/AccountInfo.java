package com.example.computernetworksmidterm.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class AccountInfo {
    private Long id;
    private String userName;
    private LocalDate birthDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String phoneNumber;
    private boolean isActive;
}
