package com.example.oauthexample.Resolver;

import java.time.LocalDateTime;
import java.util.Map;

import javax.security.auth.message.callback.PrivateKeyCallback.Request;
import javax.servlet.http.HttpSession;

import com.example.oauthexample.Annotation.SocialUser;
import com.example.oauthexample.Repository.UserRepository;
import com.example.oauthexample.VO.SocialType;
import com.example.oauthexample.VO.User;

import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    private UserRepository userRepository;

    @Override
    public Object resolveArgument(MethodParameter arg0, ModelAndViewContainer arg1, NativeWebRequest arg2,
            WebDataBinderFactory arg3) throws Exception {

        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession();
        User user = (User) session.getAttribute("user");

        return getUser(user, session);
    }

    private Object getUser(User user, HttpSession session) {

        if( user == null){
            try{

                OAuth2Authentication authentication = (OAuth2Authentication) SecurityContextHolder.getContext()
                        .getAuthentication();

                Map<String, String> map = (Map<String, String>) authentication.getUserAuthentication().getDetails();

                User convertUser = convertUser(String.valueOf(authentication.getAuthorities().toArray()[0]), map);

                user = userRepository.findByEmail(convertUser.getEmail());
                if(user == null){ user = userRepository.save(convertUser); }

                setRoleIfNotSame(user, authentication, map);
                session.setAttribute("user", user);

            }catch(ClassCastException e){
                return user;
            }
        }
        return user;
    }

    private void setRoleIfNotSame(User user, OAuth2Authentication authentication, Map<String, String> map) {
            
        if(!authentication.getAuthorities().contains( new SimpleGrantedAuthority(user.getSocialType().getRoleType())) ) {
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(map, "N/A",
                    AuthorityUtils.createAuthorityList(user.getSocialType().getRoleType())));
            }
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(SocialUser.class) != null &&
               parameter.getParameterType().equals(User.class);
    }

    private User convertUser(String authority, Map<String, String> map){
        if(SocialType.GOOGLE.isEquals(authority)) return getModerUser(SocialType.GOOGLE, map);
        else return null;
    }

    private User getModerUser(SocialType socialType, Map<String, String> map) {
        return User.builder()
                    .name(map.get("name"))
                    .email(map.get("email"))
                    .principal(map.get("principal"))
                    .socialType(socialType)
                    .createdDate(LocalDateTime.now())
                    .build();
    }




    
}