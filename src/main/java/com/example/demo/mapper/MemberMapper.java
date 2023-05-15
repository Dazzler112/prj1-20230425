package com.example.demo.mapper;

import java.util.*;

import org.apache.ibatis.annotations.*;

import com.example.demo.domain.*;

@Mapper
public interface MemberMapper {
	//-------------가입----------------------------
	
	@Insert("""
			INSERT INTO Member (id, password, nickName, email)
			VALUES (#{id}, #{password}, #{nickName}, #{email})
			""")
	int insert(Member member);

	//--------------회원 리스트 불러오기------------------------
	
	@Select("""
			SELECT 
				*
			FROM
				Member 
			ORDER BY inserted DESC
			""")
	List<Member> searchMember();

//--------------리스트에서 회원 눌러서 info 불러오기 -------------------

	// 계정삭제 ------------------------------------	
	
	@Select("""
			<script>
			SELECT * FROM 
			Member m LEFT JOIN MemberAuthority ma ON m.id = ma.memberId
			WHERE id = #{id}
			</script>
			""")
	@ResultMap("memberMap")
	Member selectById(String id);


	@Delete("""
			DELETE FROM Member
			WHERE id = #{id}
			""")
	Integer deleteById(String id);

	//------------------정보 수정-------------------------

	@Update("""
			<script>
			UPDATE Member
			SET 
			<if test="password neq null and password neq ''">
			password = #{password},
			</if>
				nickName = #{nickName},
				email = #{email}
			WHERE
				id = #{id}
			
			</script>
			""")
	Integer update(Member member);

}
