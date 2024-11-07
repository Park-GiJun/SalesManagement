package com.gijun.salesmanagement.repository;

import com.gijun.salesmanagement.domain.ApiLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiLogRepository extends JpaRepository<ApiLog, Long> {
}