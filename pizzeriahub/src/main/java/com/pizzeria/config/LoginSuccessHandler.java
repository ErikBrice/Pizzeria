package com.pizzeria.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        if (roles.contains("ROLE_ADMIN")) {
            response.sendRedirect("/admin");
        } else if (roles.contains("ROLE_COCINA")) {
            response.sendRedirect("/cocina");
        } else if (roles.contains("ROLE_DELIVERY")) {
            response.sendRedirect("/delivery");
        } else if (roles.contains("ROLE_MOTORIZADO")) {
            response.sendRedirect("/delivery");
        } else if (roles.contains("ROLE_CLIENTE")) {
            response.sendRedirect("/menu");
        } else {
            response.sendRedirect("/");
        }
    }
}