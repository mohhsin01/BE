package com.ltv.saas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
public class SellerService {
    
    @Autowired
    private SaleRepository saleRepository;
    
    public int getTotalSalesThisWeek(Long sellerId) {
        // Calculate start and end dates of this week
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);
        
        // Convert to String format (YYYY-MM-DD) for SQLite
        String startDate = startOfWeek.toString();
        String endDate = endOfWeek.toString();
        
        // Query database for sales in this date range (excluding returns)
        List<Sale> sales = saleRepository.findSalesBySellerAndDateRange(
            sellerId, 
            startDate, 
            endDate
        );
        
        // Return the count of sales
        return sales.size();
    }
    
    public double getTotalRevenueThisWeek(Long sellerId) {
        // Calculate start and end dates of this week
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);
        
        // Convert to String format (YYYY-MM-DD) for SQLite
        String startDate = startOfWeek.toString();
        String endDate = endOfWeek.toString();
        
        // Query database for sales in this date range (excluding returns)
        List<Sale> sales = saleRepository.findSalesBySellerAndDateRange(
            sellerId, 
            startDate, 
            endDate
        );
        
        // Calculate revenue: price * quantity for each sale, then sum all
        return sales.stream()
                .mapToDouble(sale -> sale.getPrice() * sale.getQuantity())
                .sum();
    }
}