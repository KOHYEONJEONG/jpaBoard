package com.toyproject.jpaboard.common.dto;

import com.toyproject.jpaboard.common.enums.Gender;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {

    public Member() {
    }


    public Member(String userid, String password, String userName, Gender gender) {
        this.userid = userid;
        this.password = password;
        this.userName = userName;
        this.gender = gender;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idx;

    @Column(nullable = false, length = 30, updatable = false)
    private String userid;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 10)
    private String userName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 5)
    private Gender gender;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate regDate = LocalDate.of(2000, 1, 1);  // ✅ 기본값 설정됨


    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = true)
    private LocalDate birthDate;

    @Column(nullable = true)
    private String email;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member")
    private List<MemberRole> memberRole = new ArrayList<>();


//    insert into member(idx, userid,password, username)
//    values(1, 'admin', '{bcrypt}$2a$10$Pds9l4v7gqJTOrRKmo3pn.EBkdgVXHtNE03WHIOAR7OACGfk9NS9e', 'admin')
}

