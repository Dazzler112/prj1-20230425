package com.example.demo.service;

import java.util.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import org.springframework.web.multipart.*;

import com.example.demo.Board.*;
import com.example.demo.mapper.*;

import software.amazon.awssdk.core.sync.*;
import software.amazon.awssdk.services.s3.*;
import software.amazon.awssdk.services.s3.model.*;

@Service  //components라고 써도 되지만 Service라고 써주는게 더 좋겠다 (어차피 service안에 components도 담겨있음)
public class BoardService {
	
	@Autowired
	private S3Client s3;
	
	@Value("${aws.s3.bucketName}")
	private String bucketName;
	
	@Autowired
	private BoardMapper mapper;
	
	public List<Board> listBoard() {
		List<Board> list = mapper.selectAll();
		return list; //일을 다시 컨트롤러에게 돌려줌
	}

	public Board getBoard(Integer id) {
		return mapper.selectById(id);
	}

	@Transactional()
	public boolean update(Board board, List<String> removeFileNames,
						MultipartFile[] addFiles) throws Exception{
		
		///FileName 테이블 삭제
		if(removeFileNames != null && !removeFileNames.isEmpty()) {
			for(String fileName : removeFileNames) {
				//s3에세 파일(객체) 삭제
				String objectKey = "board/" + board.getId() + "/" + fileName;
				DeleteObjectRequest dor = DeleteObjectRequest.builder()
						.bucket(bucketName)
						.key(objectKey)
						.build();
				s3.deleteObject(dor);
				
				//테이블 삭제
				mapper.deleteFileNameByBoardIdAndFileName(board.getId(),fileName);
			}
		}
		
		//새파일 추가
		for (MultipartFile newFile : addFiles) {
			if (newFile.getSize() > 0) {
				// 테이블에 파일명 추가
				mapper.insertFileName(board.getId(), newFile.getOriginalFilename());

				// s3에 파일(객체) 업로드
				String objectKey = "board/" + board.getId() + "/" + newFile.getOriginalFilename();
				PutObjectRequest por = PutObjectRequest.builder()
						.acl(ObjectCannedACL.PUBLIC_READ)
						.bucket(bucketName)
						.key(objectKey)
						.build();
				RequestBody rb = RequestBody.fromInputStream(newFile.getInputStream(), newFile.getSize());
				s3.putObject(por, rb);
			}
		}
		
		
		
		/*aws 안쓰는 방법
		 * 
		 * ///FileName 테이블 삭제 if(removeFileNames != null && !removeFileNames.isEmpty())
		 * { for(String fileName : removeFileNames) { //하드디스크에서 삭제 String path =
		 * "C:\\study\\upload\\" + board.getId() + "\\" + fileName; File file = new
		 * File(path); if(file.exists()) { file.delete(); }
		 * 
		 * //테이블 삭제 mapper.deleteFileNameByBoardIdAndFileName(board.getId(),fileName); }
		 * }
		 */
			
			
		
			/* //삭제한 다음 추가해야 추가한 파일 삭제가 안 되겠지?
			 * aws 사용 안하고
			 * 
			 * //새파일 추가 for(MultipartFile newFile : addFiles) { if(newFile.getSize() > 0){
			 * //테이블에 파일명 추가 mapper.insertFileName(board.getId(),
			 * newFile.getOriginalFilename());
			 * 
			 * String fileName = newFile.getOriginalFilename(); String folder =
			 * "C:\\study\\upload\\" + board.getId(); String path = folder + "\\" +
			 * fileName;
			 * 
			 * //디렉토리 없으면 만들기 File dir = new File(folder); if(!dir.exists()) { dir.mkdirs();
			 * } //파일을 하드디스크에 저장 File file = new File(path); newFile.transferTo(file); } }
			 */
		
		//게시물(board)테이블 수정
		int cnt = mapper.update(board);
		
		return cnt == 1;
	}

	public boolean remove(Integer id) {
		//파일명 조회
		List<String> fileNames =  mapper.selectFileNamesByBoardId(id);
		
		//FileName 테이블의 데이터 지우기
		mapper.deleteFilenameByBoardId(id);
		
		//s3 bucket의 파일(객체) 지우기
		for(String fileName : fileNames) {
			String objectKey = "board/" + id + "/" + fileName;
			DeleteObjectRequest dor = DeleteObjectRequest.builder()
					.bucket(bucketName)
					.key(objectKey)
					.build();
			s3.deleteObject(dor);
		}
		
		
		/*AWS 사용 안하고 하기
		 * 
		 * //하드디스크 파일 지우기 for(String fileName : fileNames) { String path
		 * ="C:\\Study\\upload\\" + id + "\\" + fileName; File file = new File(path);
		 * if(file.exists()) { file.delete(); } }
		 */
		
		
		
		
		// 게시물 테이블의 데이터 지우기
		int cnt = mapper.deleteById(id);
		return cnt ==1;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public boolean addProcess(Board board, MultipartFile[] file) throws Exception{

		//게시물 insert
		int cnt = mapper.addProcess(board);
		
		for(MultipartFile f : file) {
			if(f.getSize() > 0) {
				mapper.insertFileName(board.getId(), f.getOriginalFilename());
				String ObjectKey = "board/" +  board.getId() + "/" + f.getOriginalFilename();
				
				PutObjectRequest por = PutObjectRequest.builder()
						.bucket(bucketName)
						.key(ObjectKey)
						.acl(ObjectCannedACL.PUBLIC_READ)
						.build();
				RequestBody rb = RequestBody.fromInputStream(f.getInputStream(), f.getSize());
				
				s3.putObject(por, rb);
			}
		}
		
		/* aws 사용 안하는 법
		  
		  for(MultipartFile f : file) { 
		  if(f.getSize() > 0) {
		  System.out.println(f.getOriginalFilename()); 
		  System.out.println(f.getSize());
		  //파일 저장(파일 시스템에) 
		  //(1)게시물 번호로 폴더 만들기 
		  //(2)트랜잭션 처리하기 
		  //폴더 만들기 
		  String folder = "C:\\study\\upload\\" + board.getId(); 
		  File targetFolder = new File(folder);
		  if (!targetFolder.exists()) 
		  { targetFolder.mkdirs(); 
		  }
		  String path = folder + "\\" + f.getOriginalFilename(); 
		  File target = new File(path); 
		  f.transferTo(target); // db에 관련 정보 저장(insert)
		  mapper.insertFileName(board.getId(), f.getOriginalFilename()); } }
		*/ 
		
		
//		int cnt = 0; //실패
		return cnt ==1;
	}

	public Map<String,Object> listBoard(Integer page, String search, String type) {
		// 페이지당 행의 수
		Integer rowPerPage = 5;
		// 쿼리 LIMIT 절에 사용할 시작 인덱스
		Integer startIndex = (page - 1) * rowPerPage;
		
		//페이지네이션 필요한 정보
		//전체 레코드 수
		Integer numOfRecords = mapper.countAll(search, type);

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
		Integer endPageNum = (numOfRecords / 5)+1;
		
		Map<String, Object> pageInfo = new HashMap<>();
		pageInfo.put("rightPageNum", rightPageNum);
		pageInfo.put("leftPageNum", leftPageNum);
		pageInfo.put("currentPageNum", page); //<-- 현재페이지 생성법
		pageInfo.put("lastPageNum", lastPageNumber);
		pageInfo.put("firstPageNum", firstPageNum);
		pageInfo.put("endPageNum", endPageNum);
		//게시물 목록
		List<Board> list = mapper.selectAllPaging(startIndex,rowPerPage,search,type);
		return Map.of("pageInfo",pageInfo,
					  "boardList", list);
	}

	public void removeByWriter(String writer) {
		List<Integer> idList = mapper.selectIdByWriter(writer);
		
		for(Integer id : idList) {
			remove(id);
		}
	}

	
}
