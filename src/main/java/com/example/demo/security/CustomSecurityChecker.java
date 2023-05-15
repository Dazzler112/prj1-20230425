package com.example.demo.security;

import org.springframework.beans.factory.annotation.*;
import org.springframework.security.core.*;
import org.springframework.stereotype.*;

import com.example.demo.Board.*;
import com.example.demo.mapper.*;
// 해당유저가 게시물 수정할때 필요(자기것만 수정되고 남에것은 안되게 하기)
@Component
public class CustomSecurityChecker {
	
	@Autowired
	private BoardMapper mapper;
	
	public boolean checkBoardWriter(Authentication authentication, Integer boardId) {
		Board board =  mapper.selectById(boardId);
		
		String username = authentication.getName();
		String writer = board.getWriter();
		
		return username.equals(writer);
	}
	
}
