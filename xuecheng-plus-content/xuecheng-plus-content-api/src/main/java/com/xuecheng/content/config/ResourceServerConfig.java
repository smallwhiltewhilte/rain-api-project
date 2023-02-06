package com.xuecheng.content.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * @author wangzan
 * @version 1.0
 * @description 资源服务配置
 * @date 2023/1/30
 */
 @Configuration
 @EnableResourceServer
 @EnableGlobalMethodSecurity(securedEnabled = true,prePostEnabled = true)
 public class ResourceServerConfig extends ResourceServerConfigurerAdapter {


  //资源服务标识
  public static final String RESOURCE_ID = "xuecheng-plus";

  @Autowired
  TokenStore tokenStore;

  @Override
  public void configure(ResourceServerSecurityConfigurer resources) {
   resources.resourceId(RESOURCE_ID)//资源 id
           .tokenStore(tokenStore)
           .stateless(true);
  }

 @Override
 public void configure(HttpSecurity http) throws Exception {
  http.csrf().disable()
          .authorizeRequests()
          // 所有/r/**的请求必须认证通过
//          .antMatchers("/r/**","/course/**").authenticated()
          .anyRequest().permitAll()
  ;
 }

 }
