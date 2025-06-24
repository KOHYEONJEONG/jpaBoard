package com.toyproject.jpaboard.common.enums;

public enum Gender {
    MAN("남"),
    WOMAN("여");

    private final String desc;

    Gender(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

}
