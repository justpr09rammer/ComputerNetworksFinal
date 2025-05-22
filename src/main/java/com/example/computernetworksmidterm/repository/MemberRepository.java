package com.example.computernetworksmidterm.repository;

import com.example.computernetworksmidterm.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Boolean existsByEmail(String email);
    boolean existsById(Long id);
    Boolean existsByPhoneNumber(String phoneNumber);
    Member findByPhoneNumber(String phoneNumber);
    List<Member> findByIsActiveTrueAndEndDateBefore(LocalDate now);
    Optional<Member> findById(Long id);

}
