package com.example.oauthexample.Controller;

import java.util.Map;

import javax.servlet.http.HttpSession;

import com.example.oauthexample.Annotation.SocialUser;
import com.example.oauthexample.VO.SocialType;
import com.example.oauthexample.VO.User;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController{

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping(value="/loginSuccess")
    public String loginComplete( @SocialUser User user){
        return "redirect:/board/list";
    }

}
