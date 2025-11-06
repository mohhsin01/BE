package com.ltv.saas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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
    
    /**
     * OPTIMIZED METHOD: Gets complete seller summary in one optimized operation.
     * 
     * Performance Optimization Reasoning:
     * 1. Single Database Query: Fetches all sales data for this week in ONE query instead of multiple queries
     * 2. Stream Operations: Uses Java 8 streams for efficient in-memory processing:
     *    - Filter returns using stream().filter() instead of separate COUNT queries
     *    - Calculate revenue using stream().mapToDouble().sum() in one pass
     *    - All calculations done in memory from the same dataset
     * 3. Cached: Results cached for 30 seconds to avoid repeated database queries
     * 
     * This reduces database round trips from 4+ queries to just 2 queries (this week + last week)
     * and uses efficient stream operations for all calculations.
     */
    @Cacheable(value = "sellerSummary", key = "#sellerId")
    public SellerSummaryData getSellerSummaryOptimized(Long sellerId) {
        // Calculate start and end dates of this week
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);
        
        // Convert to String format (YYYY-MM-DD) for SQLite
        String startDate = startOfWeek.toString();
        String endDate = endOfWeek.toString();
        
        // OPTIMIZED: Single query to get ALL sales data for this week (including returns)
        // We'll filter and calculate in memory using streams
        List<Sale> allSalesThisWeek = saleRepository.findAllSalesBySellerAndDateRange(
            sellerId, 
            startDate, 
            endDate
        );
        
        // OPTIMIZED: Use streams to calculate everything from the same dataset
        // Filter non-returned sales
        List<Sale> salesThisWeek = allSalesThisWeek.stream()
            .filter(sale -> sale.getReturned() == 0)
            .toList();
        
        // Calculate total sales count
        int totalSales = salesThisWeek.size();
        
        // OPTIMIZED: Calculate revenue using stream in one pass
        double totalRevenue = salesThisWeek.stream()
            .mapToDouble(sale -> sale.getPrice() * sale.getQuantity())
            .sum();
        
        // OPTIMIZED: Calculate returns count using stream filter
        int returnsCount = (int) allSalesThisWeek.stream()
            .filter(sale -> sale.getReturned() == 1)
            .count();
        
        // Calculate return rate
        double returnRate = (totalSales == 0) ? 0.0 : ((double) returnsCount / totalSales) * 100.0;
        
        // Get last week sales for alerts
        int salesLastWeek = getTotalSalesLastWeek(sellerId);
        
        // Calculate alerts
        List<String> alerts = new ArrayList<>();
        if (salesLastWeek > 0) {
            double percentageChange = ((double)(totalSales - salesLastWeek) / salesLastWeek) * 100.0;
            if (percentageChange < -30.0) {
                alerts.add("Sales dropped by more than 30% vs last week");
            }
        }
        if (returnRate > 10.0) {
            alerts.add("Return rate above 10%");
        }
        
        return new SellerSummaryData(totalSales, totalRevenue, returnRate, alerts);
    }
    
    // Helper class to hold summary data
    public static class SellerSummaryData {
        public final int totalSales;
        public final double totalRevenue;
        public final double returnRate;
        public final List<String> alerts;
        
        public SellerSummaryData(int totalSales, double totalRevenue, double returnRate, List<String> alerts) {
            this.totalSales = totalSales;
            this.totalRevenue = totalRevenue;
            this.returnRate = returnRate;
            this.alerts = alerts;
        }
    }
}