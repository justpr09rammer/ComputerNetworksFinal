package com.example.computernetworksmidterm.service;

import com.example.computernetworksmidterm.UTILS;
import com.example.computernetworksmidterm.dtos.*;
import com.example.computernetworksmidterm.entity.DateDuration;
import com.example.computernetworksmidterm.entity.Gender;
import com.example.computernetworksmidterm.entity.Member;
import com.example.computernetworksmidterm.repository.MemberRepository;
import org.hibernate.sql.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class MemberService {

    private final UTILS utils;
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository, UTILS utils) {
        this.memberRepository = memberRepository;
        this.utils = utils;
    }


    public SearchResponseById getAccountInfoById(Long id) {
        if (!memberRepository.existsById(id)){
            return SearchResponseById.builder()
                    .message(utils.getUSER_WITH_THE_GIVEN_ID_DOES_NOT_EXIST())
                    .build();
        }
        Optional<Member> member = memberRepository.findById(id);
        return SearchResponseById.builder()
                .message(utils.getUSER_WITH_THE_GIVEN_ID_EXISTS())
                .accountInfo(AccountInfo.builder()
                        .phoneNumber(member.get().getPhoneNumber())
                        .endDate(member.get().getEndDate())
                        .startDate(member.get().getStartDate())
                        .id(member.get().getId())
                        .birthDate(member.get().getDateOfBirth())
                        .isActive(member.get().getIsActive())
                        .userName(member.get().getUserName())
                        .build())
                .build();

    }

    public MemberCreateResponse createMember(MemberCreateRequest memberCreateRequest) {
        if (memberRepository.existsByEmail(memberCreateRequest.getEmail())) {
            return MemberCreateResponse.builder()
                    .message("Email already exists")
                    .build();
        }

        if (memberRepository.existsByPhoneNumber(memberCreateRequest.getPhoneNumber())) {
            return MemberCreateResponse.builder()
                    .message("Phone number already exists")
                    .build();
        }
        if (memberCreateRequest.getDateDuration() == null) {
            return MemberCreateResponse.builder()
                    .message("Membership duration is required")
                    .build();
        }

        Member member = Member.builder()
                .firstName(memberCreateRequest.getFirstName())
                .lastName(memberCreateRequest.getLastName())
                .userName(memberCreateRequest.getFirstName() + " " + memberCreateRequest.getLastName())
                .email(memberCreateRequest.getEmail())
                .phoneNumber(memberCreateRequest.getPhoneNumber())
                .gender(Gender.valueOf(memberCreateRequest.getGender()))
                .dateOfBirth(LocalDate.parse(memberCreateRequest.getDateOfBirth()))
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(memberCreateRequest.getDateDuration().getMonths()))
                .isActive(true)
                .build();

        Member savedMember = memberRepository.save(member);

        return MemberCreateResponse.builder()
                .id(savedMember.getId())
                .firstName(savedMember.getFirstName())
                .lastName(savedMember.getLastName())
                .userName(savedMember.getUserName())
                .isActive(savedMember.getIsActive())
                .startDate(String.valueOf(LocalDate.now()))
                .endDate(String.valueOf(LocalDate.now().plusMonths(memberCreateRequest.getDateDuration().getMonths())))
                .message(utils.getMEMBER_CREATED_MESSAGE())
                .build();
    }

    public List<AccountInfo> getAllMembers() {
        List<Member> members = memberRepository.findAll();
        List<AccountInfo> accountInfos = new ArrayList<>();
        for (Member member : members) {
            accountInfos.add(AccountInfo.builder()
                            .id(member.getId())
                            .phoneNumber(String.valueOf(member.getPhoneNumber()))
                            .userName(member.getUserName())
                            .birthDate(member.getDateOfBirth())
                            .isActive(member.getIsActive())
                            .startDate(member.getStartDate())
                            .endDate(member.getEndDate())
                            .build());
        }
        return accountInfos;
    }

    public DeactivateAccountResponse deactivateAccount(String phoneNumber) {
        if (!memberRepository.existsByPhoneNumber(phoneNumber)) {
            return DeactivateAccountResponse.builder()
                    .message("Phone number does not exist")
                    .build();
        }
        Member member = memberRepository.findByPhoneNumber(phoneNumber);
        member.setIsActive(false);
        member.setEndDate(LocalDate.now());
        memberRepository.save(member);
        return DeactivateAccountResponse.builder()
                .message(utils.getACCOUNT_DEACTIVATED())
                .accountInfo(AccountInfo.builder()
                        .id(member.getId())
                        .phoneNumber(member.getPhoneNumber())
                        .isActive(member.getIsActive())
                        .startDate(member.getStartDate())
                        .endDate(member.getEndDate())
                        .userName(member.getUserName())
                        .birthDate(member.getDateOfBirth())
                        .build())

                .build();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void deactivateExpiredMembers() {
        List<Member> expiredMembers = memberRepository.findByIsActiveTrueAndEndDateBefore(LocalDate.now());
        expiredMembers.forEach(member -> {
            member.setIsActive(false);
            memberRepository.save(member);
        });
    }
    public UpdateDateResponse updateDate(UpdateDateRequest updateDateRequest) {
        if (!memberRepository.existsByPhoneNumber(updateDateRequest.getPhoneNumber())) {
            return UpdateDateResponse.builder()
                    .message(utils.getPHONE_NUMBER_DOES_NOT_EXIST())
                    .build();
        }

        Member member = memberRepository.findByPhoneNumber(updateDateRequest.getPhoneNumber());
        member.setEndDate(member.getEndDate().plusMonths(updateDateRequest.getDateDuration().getMonths()));
        member.setIsActive(true);
        memberRepository.save(member);

        return UpdateDateResponse.builder()
                .id(member.getId())
                .userName(member.getUserName())
                .startDate(member.getStartDate().toString())
                .endDate(member.getEndDate().toString())
                .message(utils.getMEMBER_UPDATED_MESSAGE())
                .build();
    }
    public DeleteAccountResponse deleteAccount(DeleteAccountRequest request) {
        if (request.getId() == null || request.getId() <= 0) {
            return DeleteAccountResponse.builder()
                    .message(utils.getINVALID_ID_FORMAT())
                    .build();
        }
        Optional<Member> memberOptional = memberRepository.findById(request.getId());
        if (memberOptional.isEmpty()) {
            return DeleteAccountResponse.builder()
                    .message(utils.getUSER_WITH_THE_GIVEN_ID_DOES_NOT_EXIST())
                    .build();
        }

        Member member = memberOptional.get();
        AccountInfo accountInfo = AccountInfo.builder()
                .id(member.getId())
                .userName(member.getFirstName() + " " + member.getLastName())
                .birthDate(member.getDateOfBirth())
                .startDate(member.getStartDate())
                .endDate(member.getEndDate())
                .phoneNumber(member.getPhoneNumber())
                .isActive(member.getIsActive())
                .build();

        try {
            memberRepository.delete(member);
            return DeleteAccountResponse.builder()
                    .message("User with ID " + request.getId() + " has been deleted successfully")
                    .accountInfo(accountInfo)
                    .build();
        } catch (Exception e) {
            return DeleteAccountResponse.builder()
                    .message(utils.getDELETE_ACCOUNT_FAILED() + ": " + e.getMessage())
                    .accountInfo(accountInfo)
                    .build();
        }
    }
    public List<SearchResponseByNameAndSurname> searchByFirstNameAndLastName(
            SearchRequestByNameAndSurname request) {

        List<SearchResponseByNameAndSurname> result = new ArrayList<>();

        List<Member> allMembers = memberRepository.findAll();

        for (Member member : allMembers) {
            if (member.getFirstName().toLowerCase().equalsIgnoreCase(request.getName().toLowerCase()) &&
                    member.getLastName().toLowerCase().equalsIgnoreCase(request.getSurname().toLowerCase())) {

                AccountInfo info = AccountInfo.builder()
                        .id(member.getId())
                        .userName(member.getFirstName() + " " + member.getLastName())
                        .birthDate(member.getDateOfBirth())
                        .startDate(member.getStartDate())
                        .endDate(member.getEndDate())
                        .phoneNumber(member.getPhoneNumber())
                        .isActive(member.getIsActive())
                        .build();

                result.add(
                        SearchResponseByNameAndSurname.builder()
                                .message("Member found")
                                .accountInfo(info)
                                .build()
                );
            }
        }

        if (result.isEmpty()) {
            result.add(
                    SearchResponseByNameAndSurname.builder()
                            .message("No members found with name: " + request.getName() +
                                    " and surname: " + request.getSurname())
                            .accountInfo(null)
                            .build()
            );
        }

        return result;
    }


}
