package com.toyproject.jpaboard.common.dto;


import com.toyproject.jpaboard.common.enums.ROLE;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class MemberRole {
     @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        @Column(nullable = false, length = 30)
        @Enumerated(EnumType.STRING)
        private ROLE role;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "fk_member_role_member_id"))
        private Member member;

        // 기본 생성자
        public MemberRole() {}

}