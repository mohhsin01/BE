package com.ltv.saas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
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
    
    public double getReturnRateThisWeek(Long sellerId) {
        // Calculate start and end dates of this week
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);
        
        // Convert to String format (YYYY-MM-DD) for SQLite
        String startDate = startOfWeek.toString();
        String endDate = endOfWeek.toString();
        
        // Get count of returns this week
        Integer returnsCount = saleRepository.countReturnsBySellerAndDateRange(
            sellerId, 
            startDate, 
            endDate
        );
        
        // Get count of sales this week (non-returned)
        int salesCount = getTotalSalesThisWeek(sellerId);
        
        // Calculate return rate: returns ÷ sales
        // Handle division by zero (if no sales, return rate is 0)
        if (salesCount == 0) {
            return 0.0;
        }
        
        // Return as percentage (multiply by 100)
        return ((double) returnsCount / salesCount) * 100.0;
    }
    
    public int getTotalSalesLastWeek(Long sellerId) {
        // Calculate start and end dates of last week
        LocalDate today = LocalDate.now();
        LocalDate startOfThisWeek = today.with(DayOfWeek.MONDAY);
        
        // Last week: 7 days before this week's Monday
        LocalDate startOfLastWeek = startOfThisWeek.minusWeeks(1);
        LocalDate endOfLastWeek = startOfLastWeek.plusDays(6); // Sunday of last week
        
        // Convert to String format (YYYY-MM-DD) for SQLite
        String startDate = startOfLastWeek.toString();
        String endDate = endOfLastWeek.toString();
        
        // Query database for sales in last week's date range (excluding returns)
        List<Sale> sales = saleRepository.findSalesBySellerAndDateRange(
            sellerId, 
            startDate, 
            endDate
        );
        
        // Return the count of sales
        return sales.size();
    }
    
    public List<String> getAlerts(Long sellerId) {
        List<String> alerts = new ArrayList<>();
        
        // Alert 1: Check if sales dropped by more than 30% vs last week
        int salesThisWeek = getTotalSalesThisWeek(sellerId);
        int salesLastWeek = getTotalSalesLastWeek(sellerId);
        
        if (salesLastWeek > 0) {
            // Calculate percentage change: ((thisWeek - lastWeek) / lastWeek) * 100
            double percentageChange = ((double)(salesThisWeek - salesLastWeek) / salesLastWeek) * 100.0;
            
            // If sales dropped by more than 30% (negative change < -30%)
            if (percentageChange < -30.0) {
                alerts.add("Sales dropped by more than 30% vs last week");
            }
        }
        
        // Alert 2: Check if return rate is above 10%
        double returnRate = getReturnRateThisWeek(sellerId);
        if (returnRate > 10.0) {
            alerts.add("Return rate above 10%");
        }
        
        return alerts;
    }
}