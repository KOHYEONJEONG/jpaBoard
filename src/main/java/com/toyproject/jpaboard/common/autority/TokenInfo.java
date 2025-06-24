package com.toyproject.jpaboard.common.autority;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenInfo {

    private String grantType = "";//JWT 권한 인증 타입(ex. Bearer)
    private String accessToken = ""; //실제 검증할 토큰
    //일반적으로는 리프레쉬토큰 까지 예제로 사용하지만 여기서는 사용하지 않겠다.


    public TokenInfo(String grantType, String accessToken){
        this.grantType = grantType;
        this.accessToken = accessToken;
    }

}
