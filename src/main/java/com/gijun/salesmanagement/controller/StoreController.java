package com.gijun.salesmanagement.controller;

import com.gijun.salesmanagement.domain.Store;
import com.gijun.salesmanagement.dto.StoreDto;
import com.gijun.salesmanagement.dto.common.ApiResponse;
import com.gijun.salesmanagement.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Store", description = "매장 관리 API")
@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @Operation(summary = "매장 등록", description = "본사 또는 가맹점 매장을 등록합니다. 가맹점 등록 시 본사 매장 코드가 필요합니다.")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<StoreDto.Response>> createStore(
            @Valid @RequestBody StoreDto.CreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(storeService.createStore(request)));
    }

    @Operation(summary = "매장 정보 수정")
    @PutMapping("/{storeCode}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<StoreDto.Response>> updateStore(
            @Parameter(description = "매장 코드", required = true)
            @PathVariable String storeCode,
            @Valid @RequestBody StoreDto.UpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(storeService.updateStore(storeCode, request)));
    }

    @Operation(summary = "매장 폐점 처리", description = "본사의 경우 운영중인 가맹점이 없어야 폐점이 가능합니다.")
    @PutMapping("/{storeCode}/close")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<StoreDto.Response>> closeStore(
            @Parameter(description = "매장 코드", required = true)
            @PathVariable String storeCode,
            @Valid @RequestBody StoreDto.CloseRequest request) {
        return ResponseEntity.ok(ApiResponse.success(storeService.closeStore(storeCode, request)));
    }

    @Operation(summary = "매장 상세 정보 조회")
    @GetMapping("/{storeCode}")
    public ResponseEntity<ApiResponse<StoreDto.Response>> getStore(
            @Parameter(description = "매장 코드", required = true)
            @PathVariable String storeCode) {
        return ResponseEntity.ok(ApiResponse.success(storeService.getStore(storeCode)));
    }

    @Operation(summary = "운영중인 전체 매장 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<StoreDto.Response>>> getAllActiveStores(
            @Parameter(description = "매장 유형", required = false)
            @RequestParam(required = false) Store.StoreType storeType) {
        List<StoreDto.Response> stores = storeService.getAllActiveStores();
        if (storeType != null) {
            stores = stores.stream()
                    .filter(store -> store.storeType() == storeType)
                    .toList();
        }
        return ResponseEntity.ok(ApiResponse.success(stores));
    }

    @Operation(summary = "최근 생성된 3개의 매장 조회")
    @GetMapping("/createdRecentStores")
    public ResponseEntity<ApiResponse<List<StoreDto.Response>>> getCreatedRecentStoredBy3() {
        List<StoreDto.Response> stores = storeService.getAllActiveStores();
        return ResponseEntity.ok(ApiResponse.success(stores));
    }

    @Operation(summary = "카테고리별 운영중인 매장 목록 조회")
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<StoreDto.Response>>> getStoresByCategory(
            @Parameter(description = "매장 카테고리", required = true)
            @PathVariable Store.StoreCategory category) {
        return ResponseEntity.ok(ApiResponse.success(storeService.getActiveStoresByCategory(category)));
    }

    @Operation(summary = "본사 소속 가맹점 목록 조회", description = "특정 본사에 소속된 운영중인 가맹점 목록을 조회합니다.")
    @GetMapping("/headquarter/{headquarterStoreCode}/franchises")
    public ResponseEntity<ApiResponse<List<StoreDto.Response>>> getFranchiseStores(
            @Parameter(description = "본사 매장 코드", required = true)
            @PathVariable String headquarterStoreCode) {
        return ResponseEntity.ok(ApiResponse.success(
                storeService.getActiveFranchiseStores(headquarterStoreCode)));
    }

    @Operation(summary = "본사 매장 목록 조회", description = "운영중인 전체 본사 매장 목록을 조회합니다.")
    @GetMapping("/headquarters")
    public ResponseEntity<ApiResponse<List<StoreDto.Response>>> getHeadquarterStores() {
        List<StoreDto.Response> stores = storeService.getAllActiveStores();
        List<StoreDto.Response> headquarters = stores.stream()
                .filter(store -> store.storeType() == Store.StoreType.HEADQUARTER)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(headquarters));
    }

    @Operation(summary = "가맹점 목록 조회", description = "운영중인 전체 가맹점 목록을 조회합니다.")
    @GetMapping("/franchises")
    public ResponseEntity<ApiResponse<List<StoreDto.Response>>> getFranchiseStores() {
        List<StoreDto.Response> stores = storeService.getAllActiveStores();
        List<StoreDto.Response> franchises = stores.stream()
                .filter(store -> store.storeType() == Store.StoreType.FRANCHISE)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(franchises));
    }
}