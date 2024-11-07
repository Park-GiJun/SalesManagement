package com.gijun.salesmanagement.domain;

import com.gijun.salesmanagement.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "stores")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 6)
    private String storeCode;

    @Column(nullable = false)
    private String storeName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StoreCategory category;

    @Column(nullable = false, length = 12)
    private String businessNumber;

    @Column(nullable = false)
    private LocalDate openDate;

    private LocalDate closeDate;

    @Column(nullable = false, length = 1)
    private String closeYn = "N";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StoreType storeType = StoreType.FRANCHISE;  // 기본값은 가맹점

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "headquarter_id")
    private Store headquarter;  // 본사 매장 정보 (체인점인 경우에만 값이 있음)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false, updatable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    public enum StoreCategory {
        RESTAURANT, CAFE, RETAIL, FASHION, BEAUTY, GROCERY, OTHER
    }

    public enum StoreType {
        HEADQUARTER("본사"),
        FRANCHISE("가맹점");

        private final String description;

        StoreType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    @Builder
    public Store(String storeName, StoreCategory category, String businessNumber,
                 LocalDate openDate, StoreType storeType, Store headquarter, User createdBy) {
        this.storeName = storeName;
        this.category = category;
        this.businessNumber = businessNumber;
        this.openDate = openDate;
        this.storeType = storeType;
        this.headquarter = headquarter;
        this.createdBy = createdBy;
        this.updatedBy = createdBy;

        validateStoreType();
    }

    private void validateStoreType() {
        if (storeType == StoreType.HEADQUARTER && headquarter != null) {
            throw new IllegalArgumentException("본사는 상위 본사를 가질 수 없습니다.");
        }
        if (storeType == StoreType.FRANCHISE && headquarter == null) {
            throw new IllegalArgumentException("가맹점은 반드시 본사 정보가 필요합니다.");
        }
    }

    public void updateStoreInfo(String storeName, StoreCategory category,
                                String businessNumber, LocalDate openDate, User updatedBy) {
        this.storeName = storeName;
        this.category = category;
        this.businessNumber = businessNumber;
        this.openDate = openDate;
        this.updatedBy = updatedBy;
    }

    public void closeStore(LocalDate closeDate, User updatedBy) {
        this.closeDate = closeDate;
        this.closeYn = "Y";
        this.updatedBy = updatedBy;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }
}