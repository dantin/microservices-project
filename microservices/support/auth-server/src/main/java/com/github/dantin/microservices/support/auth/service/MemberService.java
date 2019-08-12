package com.github.dantin.microservices.support.auth.service;

import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class MemberService {

    private final ConsumerTokenServices consumerTokenServices;

    public MemberService(ConsumerTokenServices consumerTokenServices) {
        this.consumerTokenServices = consumerTokenServices;
    }

    @GetMapping("/member")
    public Principal member(Principal user) {
        return user;
    }

    @DeleteMapping(value = "/exit")
    public String revokeToken(String token) {
        if (consumerTokenServices.revokeToken(token)) {
            return "success";
        }
        return "fail";
    }
}
