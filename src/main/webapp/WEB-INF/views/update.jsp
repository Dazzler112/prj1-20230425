<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="my" tagdir="/WEB-INF/tags"%>
<!-- tag 쓰고 싶을때 설정 -->
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Insert title here</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" integrity="sha512-iecdLmaskl7CVkqkXNQ/ZH/XLlvWZOJyj7Yy7tcenmpD1ypASozpmT/E0iPtmFIB46ZmdtAc9eNBvH0H/ZpiBw==" crossorigin="anonymous" referrerpolicy="no-referrer" />
</head>
<body>
	<my:navBar></my:navBar>
	<my:alert></my:alert>
	<div class="container-lg">
		<div class="row justify-content-center">
			<div class="col-12 col-md-8 col-lg-6">
				<h1>${board.id }게시물수정</h1>
				<form method="post" enctype="multipart/form-data">
					<!-- 액션 어트리뷰트가 없으면 원래 경로로 날라감 -->
					<input type="hidden" name="id" value="${board.id }" />
					<div class="mb-3">
						<label for="" class="form-label">제목</label> <input type="text" name="title" class="form-control" value="${board.title }" />
					</div>
					<!-- 그림파일 출력 -->

					<div class="mb-3">
						<c:forEach items="${board.fileName}" var="fileName" varStatus="status"> <!-- varStatus = id등 고유값 1개만 필요한걸 사용할때 쓰인다 -->
							<input type="checkbox" name="removeFiles"  value="${fileName }" id=""/>
							<div>
								<!-- http://localhost:8080/image/2060/%EC%A0%9C%EB%AA%A9%20%EC%97%86%EC%9D%8C.png -->
								<!-- http://localhos:8080/image/게시물번호/fileName -->
								<img  class="img-fluid img-thumbnail" src="${bucketUrl }/${board.id }/${fileName}" alt="" />
								<%-- aws 안사용하는법 <img class=" img-fluid img-thumbnail" src="http://localhost:8080/image/${board.id }/${fileName}" alt="" /> --%>
							</div>
						</c:forEach>
					</div>

					<div class="mb-3">
						<label for="" class="form-label">본문</label>
						<textarea name="body" class="form-control">${board.body } </textarea>
					</div>
					<div class="mb-3">
						작성일시 : <label for="" class="form-label">본문</label> <input type="text" class="form-control" value="${board.inserted }" readonly />
					</div>
					
					<div class="mb-3">
						<label for="up-insert" class="form-label"></label>
						<input type="file" id="up-insert" class="form-control" name="fileList" accept="image/*" multiple>
						<div class="form-text">
							총10MB, 하나의 파일은 1MB를 초과할 수 없습니다. 
						</div>
					</div>
					
					<div class="mb-3">
						<input class="btn btn-secondary" type="submit" value="수정" />
					</div>
				</form>
			</div>
		</div>
	</div>
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js" integrity="sha384-ENjdO4Dr2bkBIFxQpeoTz1HIcje39Wm4jDKdf19U8gI4ddQ3GYNS7NTKfAdVQSZe" crossorigin="anonymous"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.4/jquery.min.js" integrity="sha512-pumBsjNRGGqkPzKHndZMaAG+bir374sORyzM3uulLV14lN5LyykqNk8eEeUlUkB3U0M4FApyaHraT65ihJhDpQ==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
</body>
</html>