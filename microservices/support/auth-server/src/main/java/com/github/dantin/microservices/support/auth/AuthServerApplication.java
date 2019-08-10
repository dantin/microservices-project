package com.github.dantin.microservices.support.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@SpringBootApplication
@EnableAuthorizationServer
public class AuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServerApplication.class, args);
    }

    @EnableWebSecurity
    protected static class SecurityConfig extends WebSecurityConfigurerAdapter {

        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        @Bean
        @Override
        protected UserDetailsService userDetailsService() {
            InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
            manager.createUser(
                    User.withUsername("admin")
                            .password(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("admin"))
                            .authorities("USER").build());
            return manager;
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService());
        }
    }

    @Configuration
    protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

        @Autowired
        private AuthenticationManager authenticationManager;

        @Autowired
        private UserDetailsService userDetailsService;

        @Bean
        public TokenStore memoryTokenStore() {
            return new InMemoryTokenStore();
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            // NOTE: secret密码配置从 Spring Security 5.0开始必须以 {bcrypt}+加密后的密码 这种格式填写;
            clients.inMemory()
                    .withClient("acme")
                    .authorizedGrantTypes("authorization_code", "refresh_token")
                    .scopes("webshop")
                    .secret(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("acmesecret"));
        }

        @Override
        public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
            security.tokenKeyAccess("permitAll()")
                    .checkTokenAccess("isAuthenticated()");
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            // NOTE: we need userDetailsService here, or `refresh_token` will be wrong.
            endpoints.authenticationManager(authenticationManager)
                    .tokenStore(memoryTokenStore())
                    .userDetailsService(userDetailsService);
        }
    }

}
