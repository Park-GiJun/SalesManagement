package com.gijun.salesmanagement.interceptor;

import com.gijun.salesmanagement.domain.ApiLog;
import com.gijun.salesmanagement.repository.ApiLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ApiLogInterceptor implements HandlerInterceptor {

    private final ApiLogRepository apiLogRepository;
    private static final int MAX_CONTENT_LENGTH = 3900; // 여유를 둔 최대 길이

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (request instanceof ContentCachingRequestWrapper requestWrapper && response instanceof ContentCachingResponseWrapper responseWrapper) {

            String requestBody = new String(requestWrapper.getContentAsByteArray());
            String responseBody = new String(responseWrapper.getContentAsByteArray());

            // 요청 파라미터 수집
            String requestParams = request.getParameterMap().entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + Arrays.toString(entry.getValue()))
                    .collect(Collectors.joining(", "));

            // 현재 인증된 사용자 ID 가져오기
            Long userId = null;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                // Principal에서 userId를 가져오는 로직 구현
                // userId = ((YourUserPrincipal) authentication.getPrincipal()).getId();
            }

            ApiLog apiLog = ApiLog.builder()
                    .requestUri(request.getRequestURI())
                    .method(request.getMethod())
                    .clientIp(getClientIp(request))
                    .userAgent(request.getHeader("User-Agent"))
                    .requestParams(truncateContent(requestParams))
                    .requestBody(truncateContent(requestBody))
                    .responseBody(truncateContent(responseBody))
                    .statusCode(response.getStatus())
                    .processingTime(System.currentTimeMillis() - (Long) request.getAttribute("startTime"))
                    .userId(userId)
                    .errorMessage(ex != null ? truncateContent(ex.getMessage()) : null)
                    .status(response.getStatus() < 400 ? ApiLog.LogStatus.SUCCESS : ApiLog.LogStatus.FAIL)
                    .build();

            apiLogRepository.save(apiLog);
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }

    private String getClientIp(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("WL-Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
        }
        return clientIp;
    }

    private String truncateContent(String content) {
        if (content == null) return null;
        return content.length() > MAX_CONTENT_LENGTH ?
                content.substring(0, MAX_CONTENT_LENGTH) : content;
    }
}