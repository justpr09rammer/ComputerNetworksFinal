package com.example.computernetworksmidterm.dtos;

import lombok.*;

@Data
@Builder
public class MemberCreateResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String userName;
    private Boolean isActive;
    private String message;
    private String startDate;
    private String endDate;
}