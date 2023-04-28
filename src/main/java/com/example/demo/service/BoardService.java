package com.example.demo.service;

import java.util.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import com.example.demo.Board.*;
import com.example.demo.BoardMapper.*;

@Service  //components라고 써도 되지만 Service라고 써주는게 더 좋겠다 (어차피 service안에 components도 담겨있음)
public class BoardService {
	
	@Autowired
	private BoardMapper mapper;
	
	public List<Board> listBoard() {
		List<Board> list = mapper.selectAll();
		return list; //일을 다시 컨트롤러에게 돌려줌
	}

	public Board getBoard(Integer id) {
		return mapper.selectById(id);
	}

	public boolean update(Board board) {
		int cnt = mapper.update(board);
		
		return cnt == 1;
	}

	public boolean remove(Integer id) {
		int cnt = mapper.deleteById(id);
		return cnt ==1;
	}

	public boolean addProcess(Board board) {
		int cnt = mapper.addProcess(board);
		return cnt ==1;
	}

	public Map<String,Object> listBoard(Integer page) {
		// 페이지당 행의 수
		Integer rowPerPage = 5;
		// 쿼리 LIMIT 절에 사용할 시작 인덱스
		Integer startIndex = (page - 1) * rowPerPage;
		
		//페이지네이션 필요한 정보
		//전체 레코드 수
		Integer numOfRecords = mapper.countAll();

//		구글식으로 할꺼임 현재 5번페이지면 1/2/3/4 보이고 6/7/8/9/10 이런식으로 -4 +5 식으로 보이게

		//마지막 페이지 번호
		Integer lastPageNumber = (numOfRecords-1) / rowPerPage +1;
		
		//페이지 네이션 왼쪽번호
		Integer leftPageNum = page - 5;
		//1보다 작을 수 없음
		leftPageNum = Math.max(leftPageNum, 1);
		
		//페이지 네이션 오른쪽번호
		Integer rightPageNum = leftPageNum + 9;
		//마지막 페이지 넘버보다 클수 없음
		rightPageNum = Math.min(rightPageNum,lastPageNumber);
		
		//처음페이지
		Integer firstPageNum = (numOfRecords - numOfRecords) +1;
		
		//마지막페이지
		Integer lastPageNum = (numOfRecords / 5);
		
		Map<String, Object> pageInfo = new HashMap<>();
		pageInfo.put("rightPageNum", rightPageNum);
		pageInfo.put("leftPageNum", leftPageNum);
		pageInfo.put("currentPageNum", page); //<-- 현재페이지 생성법
		pageInfo.put("lastPageNum", lastPageNumber);
		pageInfo.put("firstPageNum", firstPageNum);
		pageInfo.put("lastPageNum", lastPageNum);
		//게시물 목록
		List<Board> list = mapper.selectAllPaging(startIndex,rowPerPage);
		return Map.of("pageInfo",pageInfo,
					  "boardList", list);
	}

	
}
