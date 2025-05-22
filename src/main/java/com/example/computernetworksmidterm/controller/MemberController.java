package com.example.computernetworksmidterm.controller;

import com.example.computernetworksmidterm.UTILS;
import com.example.computernetworksmidterm.dtos.*;
import com.example.computernetworksmidterm.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Validated
public class MemberController {

    private final MemberService memberService;
    private final UTILS utils;

    @GetMapping("/all")
    @Operation(summary = "Get all members", description = "Retrieves all registered members")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved members")
    public ResponseEntity<List<AccountInfo>> getAllMembers() {
        return ResponseEntity.ok(memberService.getAllMembers());
    }

    @PostMapping("/create")
    @Operation(summary = "Create member", description = "Register a new member account")
    @ApiResponse(responseCode = "200", description = "Member created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content(schema = @Schema(implementation = MemberCreateResponse.class)))
    @ApiResponse(responseCode = "409", description = "Conflict - duplicate email/phone",
            content = @Content(schema = @Schema(implementation = MemberCreateResponse.class)))
    public ResponseEntity<MemberCreateResponse> createMember(
            @Valid @RequestBody MemberCreateRequest request) {
        MemberCreateResponse response = memberService.createMember(request);

        return switch (response.getMessage()) {
            case "Email already exists", "Phone number already exists" ->
                    ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            case "Membership duration is required" ->
                    ResponseEntity.badRequest().body(response);
            default -> ResponseEntity.ok(response);
        };
    }

    @PatchMapping("/updateDate")
    @Operation(summary = "Update membership dates", description = "Extends membership duration")
    @ApiResponse(responseCode = "200", description = "Dates updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid phone number",
            content = @Content(schema = @Schema(implementation = UpdateDateResponse.class)))
    public ResponseEntity<UpdateDateResponse> updateDate(
            @Valid @RequestBody UpdateDateRequest request) {
        UpdateDateResponse response = memberService.updateDate(request);
        return response.getMessage().equals(utils.getPHONE_NUMBER_DOES_NOT_EXIST())
                ? ResponseEntity.badRequest().body(response)
                : ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get member by ID", description = "Retrieves member details by ID")
    @Parameter(name = "id", description = "Member ID", example = "1")
    @ApiResponse(responseCode = "200", description = "Member found")
    @ApiResponse(responseCode = "404", description = "Member not found",
            content = @Content(schema = @Schema(implementation = SearchResponseById.class)))
    public ResponseEntity<SearchResponseById> getMemberById(
            @PathVariable Long id) {
        SearchResponseById response = memberService.getAccountInfoById(id);
        return response.getMessage().equals(utils.getUSER_WITH_THE_GIVEN_ID_DOES_NOT_EXIST())
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
                : ResponseEntity.ok(response);
    }

    @PatchMapping("/deactivate/{phoneNumber}")
    @Operation(summary = "Deactivate account", description = "Deactivates member account by phone")
    @Parameter(name = "phoneNumber", description = "Phone number", example = "+1234567890")
    @ApiResponse(responseCode = "200", description = "Account deactivated")
    @ApiResponse(responseCode = "400", description = "Phone not found",
            content = @Content(schema = @Schema(implementation = DeactivateAccountResponse.class)))
    public ResponseEntity<DeactivateAccountResponse> deactivateAccount(
            @PathVariable @Pattern(regexp = "^\\+?[0-9]{10,15}$") String phoneNumber) {
        DeactivateAccountResponse response = memberService.deactivateAccount(phoneNumber);
        return response.getMessage().equals(utils.getACCOUNT_DEACTIVATED())
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Delete account", description = "Permanently deletes a member account by ID and returns deleted account info")
    @ApiResponse(responseCode = "200", description = "Account deleted successfully",
            content = @Content(schema = @Schema(implementation = DeleteAccountResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid ID format",
            content = @Content(schema = @Schema(implementation = DeleteAccountResponse.class)))
    @ApiResponse(responseCode = "404", description = "Account not found",
            content = @Content(schema = @Schema(implementation = DeleteAccountResponse.class)))
    public ResponseEntity<DeleteAccountResponse> deleteAccount(
            @Valid @RequestBody DeleteAccountRequest request) {

        DeleteAccountResponse response = memberService.deleteAccount(request);

        if (response.getMessage().equals(utils.getUSER_WITH_THE_GIVEN_ID_DOES_NOT_EXIST())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else if (response.getMessage().equals(utils.getINVALID_ID_FORMAT())) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }



    @PostMapping("/searchByNameAndSurname")
    @Operation(summary = "Search members by name and surname",
            description = "Find members by exact name match")
    @ApiResponse(responseCode = "200", description = "Search completed",
            content = @Content(schema = @Schema(implementation = SearchResponseByNameAndSurname.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content(schema = @Schema(implementation = SearchResponseByNameAndSurname.class)))
    public ResponseEntity<List<SearchResponseByNameAndSurname>> searchByNameAndSurname(
            @Valid @RequestBody SearchRequestByNameAndSurname request) {

        List<SearchResponseByNameAndSurname> response = memberService.searchByFirstNameAndLastName(request);

        if (response.size() == 1 && response.get(0).getAccountInfo() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(response);
    }

}