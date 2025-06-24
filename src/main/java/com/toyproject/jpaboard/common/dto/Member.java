package com.toyproject.jpaboard.common.dto;

import com.toyproject.jpaboard.common.enums.Gender;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idx;

    @Column(nullable = false, length = 30, updatable = false)
    private String loginid;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 10)
    private String username;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate birthdate;

    @Column(nullable = false, length = 5)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false, length = 30)
    private String email;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member")
    private List<MemberRole> memberRole = new ArrayList<>();


}

