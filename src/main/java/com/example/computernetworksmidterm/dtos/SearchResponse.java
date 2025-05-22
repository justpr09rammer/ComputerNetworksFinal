package com.example.computernetworksmidterm.dtos;

import lombok.*;

@Data
@Builder
public class SearchResponse {
    private String message;
    private AccountInfo accountInfo;
}
