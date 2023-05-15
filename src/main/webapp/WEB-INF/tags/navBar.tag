<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ attribute name="current" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<style>
	a{
	text-decoration: none;
	}
</style>
	<div style="margin-bottom: 80px;">
	<nav class="navbar navbar-expand-lg bg-body-tertiary mb-3 fixed-top">
  <div class="container-lg">
    <a class="navbar-brand" href="/list"><img src="/img/boot.jpg" alt="" height="40px"  width="60px"/> </a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarSupportedContent">
      <ul class="navbar-nav me-auto mb-2 mb-lg-0">
        <li class="nav-item">
          <a class="nav-link ${current eq 'list' ? 'active' : '' }" href="/list">목록</a>
        </li>
        <sec:authorize access="isAuthenticated()"> <!-- 이렇게 감싸면 접근제한 및 (백/프론트엔드 둘다 안보이게 제한 할수 있다) -->
        <li class="nav-item">
          <a class="nav-link ${current eq 'add' ? 'active' : '' }" href="/add">글작성</a>
        </li>
        </sec:authorize>
        <sec:authorize access="isAnonymous()">
        <li class="nav-item">
          <a class="nav-link ${current eq 'signup' ? 'active' : '' }" href="/member/signup">회원가입</a>
        </li>
        </sec:authorize>
        <sec:authorize access="hasAuthority('admin')">
        <li class="nav-item">
          <a class="nav-link ${current eq 'memberList' ? 'active' : '' }" href="/member/list">회원목록</a>
        </li>
        </sec:authorize>
        <sec:authorize access="isAuthenticated()">
        <li class="nav-item">
          <a class="nav-link ${current eq 'memberInfo' ? 'active' : '' }" href="/member/info?id=<sec:authentication property="name"/>">회원정보</a>
        </li>
        </sec:authorize>
        <sec:authorize access="isAnonymous()">
        <li class="nav-item">
          <a class="nav-link ${current eq 'login' ? 'active' : '' }" href="/member/login">로그인</a>
        </li>
        </sec:authorize>
        <sec:authorize access="isAuthenticated()">
        <li class="nav-item">
          <a class="nav-link" href="/member/logout">로그아웃</a>
        </li>
        </sec:authorize>
      </ul>
      <form action="/list" class="d-flex" role="search">
      <div class="input-group">
      <select name="type" class="form-select flex-grow-0" style=" width: 150px;">
      	<option value="all" >전체</option> 
      	<option value="title" ${param.type eq 'title' ? 'selected' : '' }>제목</option>
      	<option value="body" ${param.type eq 'body' ? 'selected' : '' }>본문</option>
		<option value="writer" ${param.type eq 'writer' ? 'selected' : '' }>작성자</option>
      </select>
      </div>
        <input class="form-control" type="search" name="search" placeholder="Search" aria-label="Search" value="${param.search }" style="width: 450px">
        <button class="btn btn-outline-success" type="submit">
        <i class="fa-solid fa-magnifying-glass"></i>
        </button>
      </form>
    </div>
  </div>
</nav>
</div>

<%--  로그인이 누구로 돼있는지 확인하는 코드
<div>
	<sec:authentication property="principal"/>
</div> --%>
