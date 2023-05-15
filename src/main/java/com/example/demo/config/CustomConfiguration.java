package com.example.demo.config;

import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.*;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.*;
import org.springframework.security.web.*;

import jakarta.annotation.*;
import jakarta.servlet.*;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.*;
import software.amazon.awssdk.services.s3.*;

@Configuration
@EnableMethodSecurity //아래처럼 코드 길게 적어도 되고 이거처럼 어노테이션 달고 컨트롤에서 @PreAuthorize 등 써서 접근제한 걸수도 있다
public class CustomConfiguration {
	@Value("${aws.accessKeyId}")
	private String accessKeyId;
	@Value("${aws.secretAccessKey}")
	private String secretAcessKey;
	
	@Value("${aws.bucketUrl}")
	private String bucketUrl;
	
	@Autowired
	private ServletContext application;
	
	@PostConstruct
	public void init() {
		application.setAttribute("bucketUrl" ,bucketUrl);
	}
	
	@Bean
	public S3Client s3client() {
		
		AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAcessKey); 
		AwsCredentialsProvider provider = StaticCredentialsProvider.create(credentials);
		
		S3Client s3client = S3Client.builder()
				.credentialsProvider(provider)
				.region(Region.AP_NORTHEAST_2)
				.build();
		
		return s3client;
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf().disable();
		
//		http.formLogin(Customizer.withDefaults());//이건 기본
		http.formLogin().loginPage("/member/login");//우리가 만들 로그인 페이지
		http.logout().logoutUrl("/member/logout");
		
//		http.authorizeHttpRequests().requestMatchers("/add").authenticated(); //접근가능한 사람만(로그인한 사람만)
//		http.authorizeHttpRequests().requestMatchers("/member/signup").anonymous();  //로그인 안한사람만 접근하게 하겠다
//		http.authorizeHttpRequests().requestMatchers("/**").permitAll();	//아무나 올수 있게 해 주겠다
		
//		http.authorizeHttpRequests().requestMatchers("/add").access(new WebExpressionAuthorizationManager("isAuthenticated")); //3번째중 맨위에것과 똑같음
//		http.authorizeHttpRequests()
//					.requestMatchers("/member/signup")
//					.access(new WebExpressionAuthorizationManager("isAnonymous()"));
//		http.authorizeHttpRequests().requestMatchers("/**")
//									.access(new WebExpressionAuthorizationManager("permitAll"));
		//https://docs.spring.io/spring-security/reference/servlet/authorization/expression-based.html 가서 참조해가면서 사용하던지 
		//spring security expression 검색후 공식홈 가서 찾아가면서 하던지 해라
		
		
		return http.build(); //로그인창 안나오게 하기
	}
}
