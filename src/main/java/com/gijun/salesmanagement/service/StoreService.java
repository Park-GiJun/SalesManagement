package com.gijun.salesmanagement.service;

import com.gijun.salesmanagement.domain.Store;
import com.gijun.salesmanagement.domain.User;
import com.gijun.salesmanagement.dto.StoreDto;
import com.gijun.salesmanagement.exception.DuplicateEntityException;
import com.gijun.salesmanagement.exception.EntityNotFoundException;
import com.gijun.salesmanagement.exception.InvalidValueException;
import com.gijun.salesmanagement.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;
    private final StoreCodeGenerator storeCodeGenerator;
    private final UserService userService;

    @Transactional
    public StoreDto.Response createStore(StoreDto.CreateRequest request) {
        // 사업자번호 중복 체크
        if (storeRepository.existsByBusinessNumber(request.businessNumber())) {
            throw new DuplicateEntityException("이미 등록된 사업자번호입니다.");
        }

        // 본사 매장 검증 및 조회
        Store headquarter = null;
        if (request.storeType() == Store.StoreType.FRANCHISE) {
            if (!StringUtils.hasText(request.headquarterStoreCode())) {
                throw new InvalidValueException("가맹점 등록 시 본사 매장 코드는 필수입니다.");
            }

            headquarter = storeRepository.findByStoreCode(request.headquarterStoreCode())
                    .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 본사 매장입니다."));

            if (headquarter.getStoreType() != Store.StoreType.HEADQUARTER) {
                throw new InvalidValueException("선택한 매장이 본사가 아닙니다.");
            }
        }

        User currentUser = userService.getCurrentUser();

        // 매장 생성
        Store store = Store.builder()
                .storeName(request.storeName())
                .category(request.category())
                .businessNumber(request.businessNumber())
                .openDate(request.openDate())
                .storeType(request.storeType())
                .headquarter(headquarter)
                .createdBy(currentUser)
                .build();

        // 매장 코드 생성 및 설정
        store.setStoreCode(storeCodeGenerator.generateStoreCode());

        Store savedStore = storeRepository.save(store);
        return StoreDto.Response.from(savedStore);
    }

    @Transactional
    public StoreDto.Response updateStore(String storeCode, StoreDto.UpdateRequest request) {
        Store store = storeRepository.findByStoreCode(storeCode)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 매장입니다."));

        // 다른 매장과의 사업자번호 중복 체크
        if (storeRepository.existsByBusinessNumberAndIdNot(request.businessNumber(), store.getId())) {
            throw new DuplicateEntityException("이미 등록된 사업자번호입니다.");
        }

        User currentUser = userService.getCurrentUser();

        store.updateStoreInfo(
                request.storeName(),
                request.category(),
                request.businessNumber(),
                request.openDate(),
                currentUser
        );

        return StoreDto.Response.from(store);
    }

    @Transactional
    public StoreDto.Response closeStore(String storeCode, StoreDto.CloseRequest request) {
        Store store = storeRepository.findByStoreCode(storeCode)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 매장입니다."));

        // 본사 매장인 경우, 운영중인 가맹점이 있는지 확인
        if (store.getStoreType() == Store.StoreType.HEADQUARTER) {
            boolean hasActiveFranchise = storeRepository.existsActiveStoresByHeadquarter(store);
            if (hasActiveFranchise) {
                throw new InvalidValueException("운영중인 가맹점이 있는 본사는 폐점할 수 없습니다.");
            }
        }

        if ("Y".equals(store.getCloseYn())) {
            throw new InvalidValueException("이미 폐점 처리된 매장입니다.");
        }

        User currentUser = userService.getCurrentUser();
        store.closeStore(request.closeDate(), currentUser);

        return StoreDto.Response.from(store);
    }

    public StoreDto.Response getStore(String storeCode) {
        Store store = storeRepository.findByStoreCode(storeCode)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 매장입니다."));

        return StoreDto.Response.from(store);
    }

    public List<StoreDto.Response> getAllActiveStores() {
        return storeRepository.findAllActiveStores().stream()
                .map(StoreDto.Response::from)
                .toList();
    }

    public List<StoreDto.Response> getCreatedRecentStoredBy3() {
        return storeRepository.findRecentCreatedStoresBy3().stream()
                .map(StoreDto.Response::from)
                .toList();
    }

    public List<StoreDto.Response> getActiveStoresByCategory(Store.StoreCategory category) {
        return storeRepository.findActiveStoresByCategory(category).stream()
                .map(StoreDto.Response::from)
                .toList();
    }

    public List<StoreDto.Response> getActiveFranchiseStores(String headquarterStoreCode) {
        Store headquarter = storeRepository.findByStoreCode(headquarterStoreCode)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 본사 매장입니다."));

        return storeRepository.findActiveStoresByHeadquarter(headquarter).stream()
                .map(StoreDto.Response::from)
                .toList();
    }
}