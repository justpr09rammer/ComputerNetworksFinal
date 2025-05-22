package com.example.computernetworksmidterm.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateDateResponse {
    private Long id;
    private String userName;
    private String message;
    private String startDate;
    private String endDate;
}
