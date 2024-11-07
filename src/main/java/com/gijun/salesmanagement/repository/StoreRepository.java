package com.gijun.salesmanagement.repository;

import com.gijun.salesmanagement.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByStoreCode(String storeCode);

    boolean existsByBusinessNumber(String businessNumber);

    @Query("SELECT s FROM Store s WHERE s.closeYn = 'N'")
    List<Store> findAllActiveStores();

    @Query("SELECT s FROM Store s WHERE s.closeYn = 'N' Order by s.createdAt DESC LIMIT 3")
    List<Store> findRecentCreatedStoresBy3();

    @Query("SELECT s FROM Store s WHERE s.category = :category AND s.closeYn = 'N'")
    List<Store> findActiveStoresByCategory(Store.StoreCategory category);

    @Query("SELECT s FROM Store s WHERE s.headquarter = :headquarter AND s.closeYn = 'N'")
    List<Store> findActiveStoresByHeadquarter(@Param("headquarter") Store headquarter);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Store s " +
            "WHERE s.businessNumber = :businessNumber AND s.id != :storeId")
    boolean existsByBusinessNumberAndIdNot(String businessNumber, Long storeId);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Store s " +
            "WHERE s.headquarter = :headquarter AND s.closeYn = 'N'")
    boolean existsActiveStoresByHeadquarter(@Param("headquarter") Store headquarter);
}