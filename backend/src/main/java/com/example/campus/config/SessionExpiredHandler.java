package com.example.campus.config;

import com.example.campus.common.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SessionExpiredHandler implements InvalidSessionStrategy, SessionInformationExpiredStrategy {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        sendSessionExpiredResponse(response);
    }

    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) 
            throws IOException, ServletException {
        sendSessionExpiredResponse(event.getResponse());
    }

    private void sendSessionExpiredResponse(HttpServletResponse response) throws IOException {
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        ApiResponse<Object> apiResponse = ApiResponse.error(408, "会话已过期，请重新登录");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
