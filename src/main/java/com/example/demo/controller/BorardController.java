package com.example.demo.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;
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
			@RequestParam(value="page", defaultValue="1") Integer page) {
		// 1. request param
		
		// 2. business logic
//		List<Board> list = service.listBoard();
		Map<String, Object> result = service.listBoard(page);
		// 3. add attribute
//		model.addAttribute("boardList",result.get("boardList"))/*;
//		model.addAttribute("pageInfo", result.get("pageInfo"));*/
		model.addAllAttributes(result);
		// 4. forward / redirect
		return"list";
	}
	
	@GetMapping("/id/{id}")
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
	public String updateForm(@PathVariable("id")Integer id , Model model) {
		model.addAttribute("board", service.getBoard(id));
		return "update";
	}
//	@RequestMapping(value="/update/{id}", method=RequestMethod.POST) //이걸 간결하게 한게 아래코드
	@PostMapping("/update/{id}")
	public String updateProcess(Board board, RedirectAttributes rttr) {
		boolean ok = service.update(board);
		
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
	public String addForm(Board board) {
		// 게시물 작성 form (view)로 포워드
		return "add";
	}
	@PostMapping("add")
	public String addProcess(Board board, RedirectAttributes rttr) {
		// 새 게시물 db에 추가
		boolean ok = service.addProcess(board);
		if(ok) {
			rttr.addFlashAttribute("message",board.getId() +"번 게시물이 등록되었습니다.");
			return "redirect:/id/" + board.getId();
		}else {
			rttr.addFlashAttribute("message",board.getId() +"게시물 등록 중 문제가 발생하였습니다.");
			return "redirect:/add"; //글쓰는게 잘 안됐으면 글쓰는곳으로 오는게 더 낫겠지?
		}
	}
}
