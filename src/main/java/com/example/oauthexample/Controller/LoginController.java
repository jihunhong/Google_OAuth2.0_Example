package com.example.oauthexample.Controller;

import java.util.Map;

import javax.servlet.http.HttpSession;

import com.example.oauthexample.Annotation.SocialUser;
import com.example.oauthexample.VO.SocialType;
import com.example.oauthexample.VO.User;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController{

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping(value="/{facebook|google|kakao}/complete")
    public String loginComplete( @SocialUser User user){
        return "redirect:/board/list";
    }

}
