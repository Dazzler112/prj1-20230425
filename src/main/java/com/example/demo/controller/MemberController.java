package com.example.demo.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.security.access.prepost.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.*;

import com.example.demo.domain.*;
import com.example.demo.service.*;

import jakarta.servlet.http.*;

@Controller
@RequestMapping("member")
public class MemberController {
	
	@Autowired
	private MemberService service;
	
	// --------------회원 가입 ---------------------------------
	@GetMapping("signup")
	@PreAuthorize("isAnonymous()")
	public void signupForm() {
	
	}
	
	@PostMapping("signup")
	@PreAuthorize("isAnonymous()")
	public String signupProcess(Member member ,RedirectAttributes rttr) {

		
		try {
			service.signup(member);
			rttr.addFlashAttribute("message","회원 가입되었습니다.");
			return"redirect:/list";
		}catch(Exception e) {
			e.printStackTrace();
			rttr.addFlashAttribute("member",member); //<- 문제 발생시 기입했던거 남아있게 하는 코드 그리고 view의 value에 el식으로 써넣기
			rttr.addFlashAttribute("message","회원 가입중 문제가 발생했습니다.");
			return"redirect:/member/signup";
		}
	}
	
	//회원 리스트 불러오기----------------------
	@GetMapping("list")
	@PreAuthorize("hasAuthority('admin')")
	public void list(Model model) {
		List<Member> members = service.membertList();
		
		model.addAttribute("memberList",members);
		
	}
	
	//------------리스트에서 회원정보 눌러서 불러오기-----------------
	
	//경로 : /member/info?id=asdf
	@GetMapping("info")
	@PreAuthorize("hasAuthority('admin') or (isAuthenticated() and (authentication.name eq #id))")
	public void info(String id, Model model) {
		Member member = service.get(id);
		model.addAttribute("member",member);
	}
	
	// ------------------- 계정 삭제--------------------------------
	@PostMapping("remove")
	@PreAuthorize("isAuthenticated() and authentication.name eq #member.id")//아이디 같아야지만 삭제 가능하게
	public String remove(Member member, RedirectAttributes rttr,HttpServletRequest request) throws Exception{
		boolean ok =  service.remove(member);
		
		if (ok) {
			rttr.addFlashAttribute("message", "회원 탈퇴하였습니다.");
			
			//로그아웃
			request.logout(); //탈퇴시 로그아웃 하게 하려면 HttpServletRequest파라미터에 받아서 써야한다
			
			return "redirect:/list";
		} else {
			rttr.addFlashAttribute("message", "회원 탈퇴시 문제가 발생하였습니다.");
			return "redirect:/member/info?id=" + member.getId(); 
		}
	}
	
	
	// 계정 수정------------------------------------
	
	//1.
	@GetMapping("modify")
	@PreAuthorize("isAuthenticated() and (authentication.name eq #id)")
	public void modifyForm(String id, Model model) {
		Member member = service.get(id);
		model.addAttribute("member",member);

//이걸 더 줄이면 이런식으로 표현
//		model.addAttribute(service.get(id));
	}
	
	//2.
	@PostMapping("modify")
	@PreAuthorize("isAuthenticated() and (authentication.name eq #member.id)")
	public String modifyProcess(Member member,String oldPassword ,RedirectAttributes rttr) {
		boolean ok = service.modify(member,oldPassword);
		
		if(ok) {
			rttr.addFlashAttribute("message", "회원정보가 수정되었습니다.");
			return "redirect:/member/info?id=" + member.getId();
		}else {
			rttr.addFlashAttribute("message", "회원 정보 수정시 문제가 발생하였습니다");
			return "redirect:/member/modify?id=" + member.getId();		
		}
		
	}
	
	//로그인 창
	@GetMapping("login")
	@PreAuthorize("isAnonymous()")
	public void loginForm() {
		
	}
	
	//ID 중복 확인
	@GetMapping("checkId/{id}")
	@ResponseBody
	public Map<String, Object> checkId(@PathVariable("id") String id){
		
		return service.checkId(id);
	}
}
