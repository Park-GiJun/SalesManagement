package com.gijun.salesmanagement.domain;

import com.gijun.salesmanagement.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "api_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String requestUri;

    @Column(nullable = false)
    private String method;

    @Column(name = "client_ip")
    private String clientIp;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "request_params", length = 1000)
    private String requestParams;

    @Column(name = "request_body", length = 4000)
    private String requestBody;

    @Column(name = "response_body", length = 4000)
    private String responseBody;

    @Column(name = "status_code")
    private Integer statusCode;

    @Column(name = "processing_time")
    private Long processingTime;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LogStatus status;

    public enum LogStatus {
        SUCCESS, FAIL
    }

    @Builder
    public ApiLog(String requestUri, String method, String clientIp, String userAgent,
                  String requestParams, String requestBody, String responseBody,
                  Integer statusCode, Long processingTime, Long userId,
                  String errorMessage, LogStatus status) {
        this.requestUri = requestUri;
        this.method = method;
        this.clientIp = clientIp;
        this.userAgent = userAgent;
        this.requestParams = requestParams;
        this.requestBody = requestBody;
        this.responseBody = responseBody;
        this.statusCode = statusCode;
        this.processingTime = processingTime;
        this.userId = userId;
        this.errorMessage = errorMessage;
        this.status = status;
    }
}