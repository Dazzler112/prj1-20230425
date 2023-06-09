package com.example.demo.Board;

import java.time.*;
import java.util.*;

import lombok.*;
@Data
public class Board {
	private Integer id;
	private String title;
	private String body;
	private LocalDateTime inserted;
	private String writer;
	private List<String> fileName;
	
	private Integer fileCount;
}
