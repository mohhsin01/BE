package com.ltv.saas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    
    @Query(value = "SELECT * FROM sales WHERE seller_id = :sellerId AND date >= :startDate AND date <= :endDate AND returned = 0", nativeQuery = true)
    List<Sale> findSalesBySellerAndDateRange(
        @Param("sellerId") Long sellerId,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate
    );
    
    @Query(value = "SELECT COUNT(*) FROM sales WHERE seller_id = :sellerId", nativeQuery = true)
    Integer countBySellerId(@Param("sellerId") Long sellerId);
    
    @Query(value = "SELECT COUNT(*) FROM sales WHERE seller_id = :sellerId AND date >= :startDate AND date <= :endDate AND returned = 1", nativeQuery = true)
    Integer countReturnsBySellerAndDateRange(
        @Param("sellerId") Long sellerId,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate
    );
    
    @Query(value = "SELECT * FROM sales WHERE seller_id = :sellerId AND date >= :startDate AND date <= :endDate", nativeQuery = true)
    List<Sale> findAllSalesBySellerAndDateRange(
        @Param("sellerId") Long sellerId,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate
    );
}
