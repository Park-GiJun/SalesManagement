package com.gijun.salesmanagement.dto;

import com.gijun.salesmanagement.domain.Store;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record StoreDto() {

    public record CreateRequest(
            @NotBlank(message = "매장명은 필수입니다.")
            String storeName,

            @NotNull(message = "매장 카테고리는 필수입니다.")
            Store.StoreCategory category,

            @NotBlank(message = "사업자 번호는 필수입니다.")
            @Pattern(regexp = "\\d{3}-\\d{2}-\\d{5}", message = "올바른 사업자 번호 형식이 아닙니다.")
            String businessNumber,

            @NotNull(message = "개장일은 필수입니다.")
            LocalDate openDate,

            @NotNull(message = "매장 유형은 필수입니다.")
            Store.StoreType storeType,

            String headquarterStoreCode  // 본사 매장 코드 (가맹점인 경우 필수)
    ) {}

    public record UpdateRequest(
            @NotBlank(message = "매장명은 필수입니다.")
            String storeName,

            @NotNull(message = "매장 카테고리는 필수입니다.")
            Store.StoreCategory category,

            @NotBlank(message = "사업자 번호는 필수입니다.")
            @Pattern(regexp = "\\d{3}-\\d{2}-\\d{5}", message = "올바른 사업자 번호 형식이 아닙니다.")
            String businessNumber,

            @NotNull(message = "개장일은 필수입니다.")
            LocalDate openDate
    ) {}

    public record CloseRequest(
            @NotNull(message = "폐점일은 필수입니다.")
            LocalDate closeDate
    ) {}

    public record Response(
            String storeCode,
            String storeName,
            Store.StoreCategory category,
            String businessNumber,
            LocalDate openDate,
            LocalDate closeDate,
            String closeYn,
            Store.StoreType storeType,
            String headquarterStoreCode,
            String headquarterStoreName,
            LocalDate createdAt,
            LocalDate updatedAt,
            String createdBy,
            String updatedBy
    ) {
        public static Response from(Store store) {
            return new Response(
                    store.getStoreCode(),
                    store.getStoreName(),
                    store.getCategory(),
                    store.getBusinessNumber(),
                    store.getOpenDate(),
                    store.getCloseDate(),
                    store.getCloseYn(),
                    store.getStoreType(),
                    store.getHeadquarter() != null ? store.getHeadquarter().getStoreCode() : null,
                    store.getHeadquarter() != null ? store.getHeadquarter().getStoreName() : null,
                    store.getCreatedAt().toLocalDate(),
                    store.getUpdatedAt().toLocalDate(),
                    store.getCreatedBy().getName(),
                    store.getUpdatedBy().getName()
            );
        }
    }
}