package com.prgrms.amabnb.security.handler;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.amabnb.common.exception.ErrorResponse;
import com.prgrms.amabnb.security.jwt.exception.TokenException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (TokenException e) {
            log.info("exception handler token error : {}", e.getMessage(), e);
            generateErrorResponse(response, e);
        }
    }

    private void generateErrorResponse(HttpServletResponse response, TokenException e) throws IOException {
        response.setStatus(e.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(new ErrorResponse(e.getMessage())));
    }

}
