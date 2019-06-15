package com.example.oauthexample;

import java.time.LocalDateTime;
import java.util.List;

import com.example.oauthexample.Repository.UserRepository;
import com.example.oauthexample.Resolver.UserArgumentResolver;
import com.example.oauthexample.VO.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
public class DemoApplication extends WebMvcConfigurerAdapter {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Autowired
	private UserArgumentResolver userArgumentResolver;

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(userArgumentResolver);
	}

	@Bean
	public CommandLineRunner runner(UserRepository userRepository) throws Exception{
		return (args) -> {
			User user = userRepository.save(User.builder()
													.name("havi")
													.password("test")
													.email("havi@gmail.com")
													.createdDate(LocalDateTime.now())
													.build());
		};
	}

	

}
