package com.toyproject.common.dto;


import com.toyproject.common.enums.ROLE;
import jakarta.persistence.*;

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

        // Getter/Setter
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public ROLE getRole() {
            return role;
        }

        public void setRole(ROLE role) {
            this.role = role;
        }

        public Member getMember() {
            return member;
        }

        public void setMember(Member member) {
            this.member = member;
        }
}