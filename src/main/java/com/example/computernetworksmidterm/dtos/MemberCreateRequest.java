package com.example.computernetworksmidterm.dtos;


import com.example.computernetworksmidterm.entity.DateDuration;
import lombok.Data;


@Data
public class MemberCreateRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String gender;
    private String dateOfBirth;
    private DateDuration dateDuration;

}
