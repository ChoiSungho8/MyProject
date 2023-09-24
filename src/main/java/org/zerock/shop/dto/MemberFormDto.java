package org.zerock.shop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberFormDto {
    
    private String name; // 회원 이름
    
    private String email; // 회원 이메일
    
    private String password; // 회원 비밀번호
    
    private String address; // 회원 주소
    
}
