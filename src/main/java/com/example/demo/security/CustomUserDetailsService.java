package com.example.demo.security;

import java.util.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.security.core.authority.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.*;

import com.example.demo.domain.*;
import com.example.demo.mapper.*;
//게시판 권한등(회원아닌 사람이 글쓰기 수정등 못하고 회원은 로그인상태일때 회원가입에 접근등 못하게 할 때)
@Component
public class CustomUserDetailsService implements UserDetailsService{
	@Autowired
	private MemberMapper mapper;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Member member = mapper.selectById(username);
		
		if(member == null) {
			throw new UsernameNotFoundException(username + "회원이 없습니다.");
		}
		
		// 1-1 1-2를 풀어 쓴것
		List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
		
		for (String auth : member.getAuthority()) {
			authorityList.add(new SimpleGrantedAuthority(auth));
		}
		
		UserDetails user = User.builder()
				.username(member.getId())
				.password(member.getPassword())
				.authorities(authorityList)
//				.authorities(List.of(member.getAuthority().stream().map(SimpleGrantedAuthority::new).toList()) 1-2 이게 저 위의 코드를 줄여 쓴것이다
				.build();
		
		return user;
	}
}
