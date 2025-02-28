package com.example.shop.service;

import com.example.shop.constant.Role;
import com.example.shop.dto.MembersDTO;
import com.example.shop.entity.Members;
import com.example.shop.repository.MembersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class MembersServiceImpl implements MembersService , UserDetailsService {
    private final MembersRepository membersRepository;
    private ModelMapper modelMapper = new ModelMapper();
    private final PasswordEncoder passwordEncoder;

    @Override
    public String signUp(MembersDTO membersDTO) {
        /*이메일로 가입 여부 확인*/
        validDuplicateMember(membersDTO.getEmail());

        /*저장을 위해 dto를 entity로 변환*/
        Members members = modelMapper.map(membersDTO, Members.class);

        members.setPassword(passwordEncoder.encode(membersDTO.getPassword()));
        log.info("멤버 서비스 : 패스워드 반환 값 : "+members.getPassword());

        /*todo 현재 가입하는 모든 사람들은 ROLE = Role.ADMIN*/
//        members.setRole(Role.USER);
        members.setRole(Role.ADMIN);

        members = membersRepository.save(members);

        membersDTO = modelMapper.map(members, MembersDTO.class);
        return membersDTO.getName();
    }

    /*회원가입이 이미 되어있는지 여부 확인용*/
    private void validDuplicateMember(String email){
        Members members = membersRepository.findByEmail(email);
        if(members!=null){
            log.info("이미 가입된 정보.");
            throw new IllegalStateException("이미 등록된 회원입니다.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        /*입력받은 email을 통해서 엔티티 찾기*/
        Members members = membersRepository.findByEmail(email);
        if(members==null){
            log.info("현재 입력하신 "+email+"로는 가입된 정보가 없습니다.");
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");/*todo try-catch처리*/
        }
        log.info("로그인 시도하는 사람 : "+members);
        log.info("로그인 하려는 사람의 권한 : "+members.getRole().name());

        String role = "";
        if(members.getRole().name().equals(Role.ADMIN.name())){
            log.info("관리자 로그인 시도 중");
            role = members.getRole().name();/*현재 로그인 시도하려는 사람의 권한을 role변수에 담는다.*/
        }else{
            log.info("일반 회원 로그인 시도 중");
            role = members.getRole().name();
        }

        /*시큐리티에서 말하는 username은 인증을 하는 필드이다. (이름 아님) email 또는 pk*/
        /*db에 있는 password를 userdetails객체에 담아서 보내면
        * 호출한 컨트롤러에서 해당 객체를 받아 브라우저에서 입력한 비밀번호와 비교하여 로그인 시도*/
        UserDetails user = User.builder().username(members.getEmail())
                .password(members.getPassword())
                .roles(role).build();
        return user;
    }
}