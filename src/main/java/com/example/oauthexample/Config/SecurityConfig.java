package com.example.oauthexample.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.Filter;

import com.example.oauthexample.VO.SocialType;

import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.CompositeFilter;

import ch.qos.logback.core.net.server.Client;

@Configuration
@EnableWebSecurity
// @EnableOAuth2Client
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // @Autowired
    // private OAuth2ClientContext oAuth2ClientContext;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        CharacterEncodingFilter filter = new CharacterEncodingFilter();

        http.authorizeRequests()
                .antMatchers("/", "/oauth2/**", "/login/**", "/css/**", "/images/**", "/js/**","/console/***")
                .permitAll()
                .antMatchers("/google").hasAuthority(SocialType.GOOGLE.getRoleType())
                .anyRequest().authenticated()
            .and()
                .oauth2Login()
                .defaultSuccessUrl("/loginSuccess")
                .failureUrl("/loginFailure")
            .and()
                .headers().frameOptions().disable()
            .and()
                .exceptionHandling()
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint(
                        "/login"))
            .and()
                .formLogin()
                .successForwardUrl("/board/list")
            .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
            .and()
                .addFilterBefore(filter, CsrfFilter.class)
                .csrf().disable();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(OAuth2ClientProperties oAuth2ClientProperties){
        
        List<ClientRegistration> registrations = oAuth2ClientProperties.getRegistration().keySet().stream()
                                                        .map(client -> getRegistration(oAuth2ClientProperties, client))
                                                        .filter(Objects::nonNull)
                                                        .collect(Collectors.toList());
        
        return new InMemoryClientRegistrationRepository(registrations);
    }

    private ClientRegistration getRegistration(OAuth2ClientProperties clientProperties, String client){

        if("google".equals(client)){
            OAuth2ClientProperties.Registration registration = clientProperties.getRegistration().get("google");

            return CommonOAuth2Provider.GOOGLE.getBuilder(client)
                        .clientId(registration.getClientId())
                        .clientSecret(registration.getClientSecret())
                        .scope("email","profile","https://www.googleapis.com/auth/youtube")
                        .build();
        }

        return null;
    }

    // @Bean
    // public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter){

    //     FilterRegistrationBean registration = new FilterRegistrationBean();

    //     registration.setFilter(filter);
    //     registration.setOrder(-100);

    //     return registration;        
    // }

    // private Filter oauth2Filter(){

    //     CompositeFilter filter = new CompositeFilter();
    //     List<Filter> filters = new ArrayList<>();

    //     filters.add(oauth2Filter(google(), "/login/google", SocialType.GOOGLE));
    //     filter.setFilters(filters);

    //     return filter;
    // }

    // private Filter oauth2Filter(ClientResources client, String path, SocialType socialType){

    //     OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);

    //     OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oAuth2ClientContext);

    //     filter.setRestTemplate(template);
    //     filter.setTokenServices(new UserTokenService(client, socialType));
    //     filter.setAuthenticationSuccessHandler((request, response, authentication)
    //                                          -> response.sendRedirect("/" + socialType.getValue()+"/complete"));
    //     filter.setAuthenticationFailureHandler((request, response, exception) -> response.sendRedirect("/error"));

    //     return filter;
    // }

    // @Bean
    // @ConfigurationProperties("google")
    // public ClientResources google() {
        
    //     return new ClientResources();
    // }

}