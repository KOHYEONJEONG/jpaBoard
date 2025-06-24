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
    private String userid;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 10)
    private String username;

    @Temporal(TemporalType.DATE)
    private LocalDate birthdate;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String email;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member")
    private List<MemberRole> memberRole = new ArrayList<>();

//    insert into member(idx, userid,password, username)
//    values(1, 'admin', '{bcrypt}$2a$10$Pds9l4v7gqJTOrRKmo3pn.EBkdgVXHtNE03WHIOAR7OACGfk9NS9e', 'admin')
}

