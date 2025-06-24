package com.toyproject.jpaboard.common.repository;

import com.toyproject.jpaboard.common.dto.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUserid(String userid);   // userid 컬럼이 실제 존재할 경우

}

