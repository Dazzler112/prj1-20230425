package com.example.demo.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.security.access.prepost.*;
import org.springframework.security.core.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;
import org.springframework.web.servlet.mvc.support.*;

import com.example.demo.Board.*;
import com.example.demo.service.*;

@Controller
@RequestMapping("/")
public class BorardController {
	
	@Autowired
	private BoardService service;
	
	// 경로: http://localhost:8080
	// 경로: http://localhost:8080/list
	@GetMapping({"/" , "list"})
	public String list (Model model,
			@RequestParam(value="page", defaultValue="1") Integer page,
			@RequestParam(value = "search",defaultValue = "")String search,
			@RequestParam(value="type" ,required = false)String type) {//<- 값이 필요 없이 검색할때 (null로 남아있음) required
		// 1. request param
		
		// 2. business logic
//		List<Board> list = service.listBoard();
//		Map<String, Object> result = service.listBoard(page); //페이지 처리건
		Map<String, Object> result = service.listBoard(page,search,type);
		// 3. add attribute
//		model.addAttribute("boardList",result.get("boardList"))/*;
//		model.addAttribute("pageInfo", result.get("pageInfo"));*/
		model.addAllAttributes(result);
		// 4. forward / redirect
		return"list";
	}
	
	@GetMapping("/id/{id}")
	@PreAuthorize("isAuthenticated()")
	public String board(@PathVariable("id")Integer id, Model model) {
		//1. request param
		//2. business logic
		Board board =  service.getBoard(id);
		//3. add attribute
		model.addAttribute("board",board);
		//4. forward / redirect
		return "get";
	}
	
	
	@GetMapping("/update/{id}")
	@PreAuthorize("isAuthenticated() and @customSecurityChecker.checkBoardWriter(authentication,#id)")
	public String updateForm(@PathVariable("id")Integer id , Model model) {
		model.addAttribute("board", service.getBoard(id));
		return "update";
	}
	
	
//	@RequestMapping(value="/update/{id}", method=RequestMethod.POST) //이걸 간결하게 한게 아래코드
	@PostMapping("/update/{id}")
	@PreAuthorize("isAuthenticated() and @customSecurityChecker.checkBoardWriter(authentication,#board.id)")
	//수정하려는 게시물 id : board.getId
	
	public String updateProcess(Board board,
				@RequestParam(value="removeFiles", required = false) List<String> removeFileNames ,
				@RequestParam(value="fileList", required = false) MultipartFile[] addFiles,
				RedirectAttributes rttr) throws Exception{
		
		boolean ok = service.update(board,removeFileNames,addFiles);
		
		if(ok) {
			// 해당 게시물 보기로 리 리디렉션
//			rttr.addAttribute("success","sucess");
			
			rttr.addFlashAttribute("message", board.getId() + "번 게시물이 수정되었습니다.");
			return "redirect:/id/" + board.getId();
		}else {
			//수정 form으로 리디렉션
			rttr.addFlashAttribute("message", board.getId() + "번 게시물이 수정되지 않았습니다.");
			return "redirect:/update/" + board.getId();
		}
		
	}
	
	
	
	@PostMapping("remove")
	@PreAuthorize("isAuthenticated() and @customSecurityChecker.checkBoardWriter(authentication,#id) ")
	public String remove(Integer id, RedirectAttributes rttr) {
		boolean ok = service.remove(id);
	
		if(ok) {
			//query string에 추가
//			rttr.addAttribute("success","remove");
			
			//모델에 추가
			rttr.addFlashAttribute("message",id + "번 게시물이 삭제되었습니다.");
			return "redirect:/list";
		}else {
			return "redirect:/id/" + id;
		}
	}
	
	
	
	
	@GetMapping("add")
	@PreAuthorize("isAuthenticated()")
	public void addForm() {
		// 게시물 작성 form (view)로 포워드
	}
	
	@PostMapping("add")
	public String addProcess(@RequestParam("fileList") MultipartFile[] file,
						Board board, RedirectAttributes rttr,
						Authentication authentication) //로그인 한사람 받을때는 Authentication
								throws Exception{
		// 새 게시물 db에 추가
		board.setWriter(authentication.getName());
		boolean ok = service.addProcess(board,file);
		if(ok) {
			rttr.addFlashAttribute("message",board.getId() +"번 게시물이 등록되었습니다.");
			return "redirect:/id/" + board.getId();
		}else {
			rttr.addFlashAttribute("message",board.getId() +"게시물 등록 중 문제가 발생하였습니다.");
			rttr.addFlashAttribute("board",board);
			return "redirect:/add"; //글쓰는게 잘 안됐으면 글쓰는곳으로 오는게 더 낫겠지?
		}
	}
	
}
