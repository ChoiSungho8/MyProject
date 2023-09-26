package org.zerock.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.zerock.shop.constant.Role;
import org.zerock.shop.dto.MemberFormDto;

@Entity
@Table(name = "member")
@Getter
@Setter
@ToString
public class Member extends BaseEntity {

    @Id
    @Column(name="member_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(unique = true) // 동일한 값이 들어올 수 없다.
    private String email;

    private String password;

    private String address;

    // enum 타입을 엔티티 속성으로 지정
    // Enum 사용 시 순서가 저장되는데, 순서가 바뀔 경우 문제가 발생할 수 있으므로 String 저장 권장
    @Enumerated(EnumType.STRING)
    private Role role;

    // Member 엔티티를 생성하는 메소드
    // Member 엔티티에 회원을 생성하는 메소드를 만들어서 관리를 한다면 코드가 변경되더라도
    // 한 군데만 수정하면 되는 이점이 있다.
    public static Member createMember(MemberFormDto memberFormDto,
                                      PasswordEncoder passwordEncoder) {

        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        member.setAddress(memberFormDto.getAddress());
        
        // BCryptPasswordEncoder Bean을 파라미터로 넘겨서 비밀번호를 암호화
        String password = passwordEncoder.encode(memberFormDto.getPassword());
        member.setPassword(password);
        member.setRole(Role.ADMIN);
        return member;

    }

}
