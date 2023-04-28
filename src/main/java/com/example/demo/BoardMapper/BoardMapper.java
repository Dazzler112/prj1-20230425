package com.example.demo.BoardMapper;

import java.util.*;

import org.apache.ibatis.annotations.*;

import com.example.demo.Board.*;

@Mapper
public interface BoardMapper {


	@Select("""
			SELECT
				id,
				title,
				writer,
				inserted
			FROM Board
			ORDER BY id DESC
			""")
	List<Board> selectAll();

	@Select("""
			SELECT *
			FROM Board
			WHERE id = #{id}
			""")
	Board selectById(Integer id);

	
	@Update("""
			UPDATE Board
			SET 
				title=#{title},
				body = #{body},
				writer = #{writer}
				WHERE
				id = #{id}
			""")
	int update(Board board);

	@Delete("""
			DELETE FROM Board
			WHERE id = #{id}
			""")
	int deleteById(Integer id);

	
	@Insert("""
			INSERT INTO Board(title, body,writer)
				VALUES(#{title},#{body},#{writer})
			""")
	@Options(useGeneratedKeys = true, keyProperty = "id") // id의 자동증가 하는걸 알고싶을때
	int addProcess(Board board);

//	--------------------------pageNation 필드----------------------------
	
	@Select("""
			SELECT
				id,
				title,
				writer,
				inserted
			FROM Board
			ORDER BY id DESC
			LIMIT #{startIndex}, #{rowPerPage}
			""")
	List<Board> selectAllPaging(Integer startIndex, Integer rowPerPage);

	@Select("""
			SELECT COUNT(*)
			FROM Board
			""")
	Integer countAll();

}
