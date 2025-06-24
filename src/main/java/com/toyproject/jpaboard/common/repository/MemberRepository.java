package com.toyproject.jpaboard.common.repository;

import com.toyproject.jpaboard.common.dto.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByLoginid(String loginId);

}

