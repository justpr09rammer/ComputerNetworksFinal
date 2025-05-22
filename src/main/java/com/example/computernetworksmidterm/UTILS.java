package com.example.computernetworksmidterm;


import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class UTILS {

    private String MEMBER_CREATED_MESSAGE = "Member created successfully";
    private String MEMBER_UPDATED_MESSAGE = "Member updated successfully";
    private String PHONE_NUMBER_DOES_NOT_EXIST = "Member with the given phone number does not exist";
    private String USER_WITH_THE_GIVEN_ID_DOES_NOT_EXIST = "User with the given id does not exist";
    private String USER_WITH_THE_GIVEN_ID_EXISTS = "User with the given id found!";
    private String ACCOUNT_DEACTIVATED = "Account deactivated";
    private String INVALID_ID_FORMAT = "Invalid ID format";
    private String ACCOUNT_DELETED_SUCCESS = "Account deleted successfully";
    private String DELETE_ACCOUNT_FAILED = "Failed to delete account";
}
